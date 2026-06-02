package com.example.vcoach.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.Closeable
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class TfliteFoodDetector(
    context: Context,
) : FoodDetector, Closeable {
    private val appContext = context.applicationContext
    private var interpreter: Interpreter? = null

    override suspend fun detect(bitmap: Bitmap): List<FoodDetectionResult> = withContext(Dispatchers.Default) {
        val input = bitmap.toModelInput()
        val interpreter = getInterpreter()
        val outputShapes = List(interpreter.outputTensorCount) { index ->
            interpreter.getOutputTensor(index).shape()
        }
        val outputs = outputShapes
            .mapIndexed { index, shape -> index to createOutputBuffer(shape) }
            .toMap()
            .toMutableMap()

        interpreter.runForMultipleInputsOutputs(arrayOf(input), outputs)

        val detectionIndex = outputShapes.indexOfFirst { shape ->
            shape.size == DETECTION_OUTPUT_RANK &&
                (shape[1] >= MIN_DETECTION_CHANNEL_COUNT || shape[2] >= MIN_DETECTION_CHANNEL_COUNT)
        }
        if (detectionIndex == NOT_FOUND) {
            Log.w(TAG, "YOLO detection output was not found. shapes=${outputShapes.joinToString { it.contentToString() }}")
            return@withContext emptyList()
        }
        val maskPrototypeIndex = findMaskPrototypeOutputIndex(
            outputShapes = outputShapes,
            detectionIndex = detectionIndex,
        )

        val parsedDetections = parseDetections(
            output = outputs.getValue(detectionIndex),
            shape = outputShapes[detectionIndex],
        )
        Log.d(TAG, "YOLO parsed candidates=${parsedDetections.size}, maskPrototypeIndex=$maskPrototypeIndex")

        val selectedDetections = parsedDetections
            .nonMaxSuppression()
            .filterByMaskPrototype(
                maskPrototype = maskPrototypeIndex
                    ?.let { index -> outputs.getValue(index) as? Array<Array<Array<FloatArray>>> },
                maskPrototypeShape = maskPrototypeIndex
                    ?.let { index -> outputShapes[index] },
            )

        selectedDetections
            .groupBy { detection -> detection.classId }
            .mapNotNull { (classId, detections) ->
                val ingredient = IngredientClass.entries.getOrNull(classId) ?: return@mapNotNull null
                FoodDetectionResult(
                    ingredientName = ingredient.displayName,
                    confidence = detections.maxOf { detection -> detection.score },
                )
            }
            .sortedByDescending { result -> result.confidence }
            .also { results ->
                Log.d(
                    TAG,
                    "YOLO final ingredients=${
                        results.joinToString { result ->
                            "${result.ingredientName}=${"%.3f".format(result.confidence)}"
                        }
                    }",
                )
            }
    }

    override fun close() {
        interpreter?.close()
        interpreter = null
    }

    private fun getInterpreter(): Interpreter {
        return interpreter ?: Interpreter(
            loadModelFile(),
            Interpreter.Options().setNumThreads(DEFAULT_THREAD_COUNT),
        ).also { loadedInterpreter ->
            interpreter = loadedInterpreter
            logTensorShapes(loadedInterpreter)
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        return appContext.assets.openFd(MODEL_FILE_NAME).use { fileDescriptor ->
            FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
                inputStream.channel.map(
                    FileChannel.MapMode.READ_ONLY,
                    fileDescriptor.startOffset,
                    fileDescriptor.declaredLength,
                )
            }
        }
    }

    private fun logTensorShapes(interpreter: Interpreter) {
        val inputShapes = List(interpreter.inputTensorCount) { index ->
            interpreter.getInputTensor(index).shape().contentToString()
        }
        val outputShapes = List(interpreter.outputTensorCount) { index ->
            interpreter.getOutputTensor(index).shape().contentToString()
        }
        Log.d(TAG, "YOLO input shapes=$inputShapes, output shapes=$outputShapes")
    }

    private fun createOutputBuffer(shape: IntArray): Any {
        return when (shape.size) {
            2 -> Array(shape[0]) { FloatArray(shape[1]) }
            3 -> Array(shape[0]) { Array(shape[1]) { FloatArray(shape[2]) } }
            4 -> Array(shape[0]) { Array(shape[1]) { Array(shape[2]) { FloatArray(shape[3]) } } }
            else -> error("Unsupported TFLite output shape: ${shape.contentToString()}")
        }
    }

    private fun findMaskPrototypeOutputIndex(
        outputShapes: List<IntArray>,
        detectionIndex: Int,
    ): Int? {
        return outputShapes.indices.indexOfFirst { index ->
            val shape = outputShapes[index]
            index != detectionIndex &&
                shape.size == MASK_PROTOTYPE_OUTPUT_RANK &&
                shape.drop(1).any { dimension -> dimension == MASK_COEFFICIENT_COUNT }
        }.takeIf { index -> index != NOT_FOUND }
    }

    private fun Bitmap.toModelInput(): ByteBuffer {
        val letterboxedBitmap = toLetterboxedBitmap()
        val pixels = IntArray(INPUT_PIXEL_COUNT)
        letterboxedBitmap.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)

        return ByteBuffer
            .allocateDirect(INPUT_PIXEL_COUNT * RGB_CHANNEL_COUNT * FLOAT_BYTE_SIZE)
            .order(ByteOrder.nativeOrder())
            .apply {
                pixels.forEach { pixel ->
                    putFloat(((pixel shr 16) and BYTE_MASK) / PIXEL_MAX_VALUE)
                    putFloat(((pixel shr 8) and BYTE_MASK) / PIXEL_MAX_VALUE)
                    putFloat((pixel and BYTE_MASK) / PIXEL_MAX_VALUE)
                }
                rewind()
            }
    }

    private fun Bitmap.toLetterboxedBitmap(): Bitmap {
        val scale = min(INPUT_SIZE.toFloat() / width, INPUT_SIZE.toFloat() / height)
        val scaledWidth = (width * scale).toInt().coerceAtLeast(1)
        val scaledHeight = (height * scale).toInt().coerceAtLeast(1)
        val left = (INPUT_SIZE - scaledWidth) / 2f
        val top = (INPUT_SIZE - scaledHeight) / 2f
        val output = Bitmap.createBitmap(INPUT_SIZE, INPUT_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        canvas.drawColor(LETTERBOX_COLOR)
        canvas.drawBitmap(
            this,
            null,
            RectF(left, top, left + scaledWidth, top + scaledHeight),
            paint,
        )

        return output
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseDetections(
        output: Any,
        shape: IntArray,
    ): List<YoloDetection> {
        val tensor = output as Array<Array<FloatArray>>
        val channelsFirst = shape[1] <= MAX_DETECTION_CHANNEL_COUNT && shape[2] > shape[1]
        val channelCount = if (channelsFirst) shape[1] else shape[2]
        val anchorCount = if (channelsFirst) shape[2] else shape[1]
        val maskCoefficientCount = channelCount - BOX_CHANNEL_COUNT - IngredientClass.entries.size
        val detections = mutableListOf<YoloDetection>()

        if (channelCount < BOX_CHANNEL_COUNT + IngredientClass.entries.size) return emptyList()

        for (anchor in 0 until anchorCount) {
            var bestClassId = 0
            var bestScore = Float.NEGATIVE_INFINITY

            for (classId in IngredientClass.entries.indices) {
                val score = tensor.valueAt(
                    anchor = anchor,
                    channel = BOX_CHANNEL_COUNT + classId,
                    channelsFirst = channelsFirst,
                )
                if (score > bestScore) {
                    bestScore = score
                    bestClassId = classId
                }
            }

            val ingredient = IngredientClass.entries[bestClassId]
            if (bestScore < ingredient.confidenceThreshold) continue

            val centerX = tensor.valueAt(anchor, X_CHANNEL, channelsFirst).toInputPixels()
            val centerY = tensor.valueAt(anchor, Y_CHANNEL, channelsFirst).toInputPixels()
            val width = tensor.valueAt(anchor, WIDTH_CHANNEL, channelsFirst).toInputPixels()
            val height = tensor.valueAt(anchor, HEIGHT_CHANNEL, channelsFirst).toInputPixels()

            if (width < MIN_BOX_SIZE || height < MIN_BOX_SIZE) continue

            val box = RectF(
                (centerX - width / 2f).coerceIn(0f, INPUT_SIZE_FLOAT),
                (centerY - height / 2f).coerceIn(0f, INPUT_SIZE_FLOAT),
                (centerX + width / 2f).coerceIn(0f, INPUT_SIZE_FLOAT),
                (centerY + height / 2f).coerceIn(0f, INPUT_SIZE_FLOAT),
            )
            if (box.width() < MIN_BOX_SIZE || box.height() < MIN_BOX_SIZE) continue
            if (box.areaRatio() < ingredient.minBoxAreaRatio) continue

            val maskCoefficients = if (maskCoefficientCount >= MASK_COEFFICIENT_COUNT) {
                FloatArray(MASK_COEFFICIENT_COUNT) { index ->
                    tensor.valueAt(
                        anchor = anchor,
                        channel = BOX_CHANNEL_COUNT + IngredientClass.entries.size + index,
                        channelsFirst = channelsFirst,
                    )
                }
            } else {
                FloatArray(0)
            }

            detections += YoloDetection(
                classId = bestClassId,
                score = bestScore,
                box = box,
                maskCoefficients = maskCoefficients,
            )
        }

        return detections
    }

    private fun Array<Array<FloatArray>>.valueAt(
        anchor: Int,
        channel: Int,
        channelsFirst: Boolean,
    ): Float {
        return if (channelsFirst) {
            this[0][channel][anchor]
        } else {
            this[0][anchor][channel]
        }
    }

    private fun Float.toInputPixels(): Float {
        return if (this <= NORMALIZED_COORDINATE_MAX) {
            this * INPUT_SIZE
        } else {
            this
        }
    }

    private fun List<YoloDetection>.nonMaxSuppression(): List<YoloDetection> {
        val selected = mutableListOf<YoloDetection>()

        groupBy { detection -> detection.classId }.forEach { (_, classDetections) ->
            val candidates = classDetections.sortedByDescending { detection -> detection.score }
            val suppressed = BooleanArray(candidates.size)
            var selectedForClass = 0

            for (index in candidates.indices) {
                if (suppressed[index]) continue

                val current = candidates[index]
                selected += current
                selectedForClass += 1
                if (selectedForClass >= MAX_DETECTIONS_PER_CLASS) break

                for (otherIndex in index + 1 until candidates.size) {
                    if (suppressed[otherIndex]) continue
                    if (current.box.iou(candidates[otherIndex].box) > IOU_THRESHOLD) {
                        suppressed[otherIndex] = true
                    }
                }
            }
        }

        return selected
    }

    private fun List<YoloDetection>.filterByMaskPrototype(
        maskPrototype: Array<Array<Array<FloatArray>>>?,
        maskPrototypeShape: IntArray?,
    ): List<YoloDetection> {
        if (maskPrototype == null || maskPrototypeShape == null) return this

        return filter { detection ->
            if (detection.maskCoefficients.size < MASK_COEFFICIENT_COUNT) return@filter true

            val ingredient = IngredientClass.entries[detection.classId]
            val maskAreaRatio = detection.maskAreaRatio(
                maskPrototype = maskPrototype,
                maskPrototypeShape = maskPrototypeShape,
            )
            val keep = detection.score >= ingredient.strongConfidenceThreshold ||
                maskAreaRatio >= ingredient.minMaskAreaRatio
            if (!keep) {
                Log.d(
                    TAG,
                    "Drop ${ingredient.displayName}: score=${"%.3f".format(detection.score)}, maskArea=${"%.4f".format(maskAreaRatio)}",
                )
            }
            keep
        }
    }

    private fun YoloDetection.maskAreaRatio(
        maskPrototype: Array<Array<Array<FloatArray>>>,
        maskPrototypeShape: IntArray,
    ): Float {
        val channelsFirst = maskPrototypeShape[1] == MASK_COEFFICIENT_COUNT
        val prototypeHeight = if (channelsFirst) maskPrototypeShape[2] else maskPrototypeShape[1]
        val prototypeWidth = if (channelsFirst) maskPrototypeShape[3] else maskPrototypeShape[2]
        val xStart = floor(box.left / INPUT_SIZE_FLOAT * prototypeWidth).toInt().coerceIn(0, prototypeWidth - 1)
        val yStart = floor(box.top / INPUT_SIZE_FLOAT * prototypeHeight).toInt().coerceIn(0, prototypeHeight - 1)
        val xEnd = ceil(box.right / INPUT_SIZE_FLOAT * prototypeWidth).toInt().coerceIn(xStart + 1, prototypeWidth)
        val yEnd = ceil(box.bottom / INPUT_SIZE_FLOAT * prototypeHeight).toInt().coerceIn(yStart + 1, prototypeHeight)
        var visiblePixelCount = 0
        var sampledPixelCount = 0

        for (y in yStart until yEnd step MASK_SAMPLE_STEP) {
            for (x in xStart until xEnd step MASK_SAMPLE_STEP) {
                var logit = 0f
                for (channel in 0 until MASK_COEFFICIENT_COUNT) {
                    logit += maskCoefficients[channel] * maskPrototype.valueAt(
                        y = y,
                        x = x,
                        channel = channel,
                        channelsFirst = channelsFirst,
                    )
                }
                if (logit.sigmoid() >= MASK_THRESHOLD) {
                    visiblePixelCount += 1
                }
                sampledPixelCount += 1
            }
        }

        return if (sampledPixelCount == 0) 0f else {
            visiblePixelCount.toFloat() / sampledPixelCount
        }
    }

    private fun Array<Array<Array<FloatArray>>>.valueAt(
        y: Int,
        x: Int,
        channel: Int,
        channelsFirst: Boolean,
    ): Float {
        return if (channelsFirst) {
            this[0][channel][y][x]
        } else {
            this[0][y][x][channel]
        }
    }

    private fun Float.sigmoid(): Float {
        return (1f / (1f + exp(-this))).toFloat()
    }

    private fun RectF.areaRatio(): Float {
        return width() * height() / (INPUT_SIZE_FLOAT * INPUT_SIZE_FLOAT)
    }

    private fun RectF.iou(other: RectF): Float {
        val intersectionLeft = max(left, other.left)
        val intersectionTop = max(top, other.top)
        val intersectionRight = min(right, other.right)
        val intersectionBottom = min(bottom, other.bottom)
        val intersectionWidth = max(0f, intersectionRight - intersectionLeft)
        val intersectionHeight = max(0f, intersectionBottom - intersectionTop)
        val intersectionArea = intersectionWidth * intersectionHeight
        val unionArea = width() * height() + other.width() * other.height() - intersectionArea

        return if (unionArea <= 0f) 0f else intersectionArea / unionArea
    }

    private data class YoloDetection(
        val classId: Int,
        val score: Float,
        val box: RectF,
        val maskCoefficients: FloatArray,
    )

    private enum class IngredientClass(
        val displayName: String,
        val confidenceThreshold: Float,
        val strongConfidenceThreshold: Float,
        val minBoxAreaRatio: Float,
        val minMaskAreaRatio: Float,
    ) {
        Meat("\uc721\ub958", confidenceThreshold = 0.22f, strongConfidenceThreshold = 0.40f, minBoxAreaRatio = 0.0015f, minMaskAreaRatio = 0.04f),
        Chicken("\ub2ed\uace0\uae30", confidenceThreshold = 0.16f, strongConfidenceThreshold = 0.35f, minBoxAreaRatio = 0.0010f, minMaskAreaRatio = 0.035f),
        Seafood("\uc5b4\ud328\ub958", confidenceThreshold = 0.22f, strongConfidenceThreshold = 0.40f, minBoxAreaRatio = 0.0010f, minMaskAreaRatio = 0.035f),
        Egg("\uacc4\ub780", confidenceThreshold = 0.16f, strongConfidenceThreshold = 0.35f, minBoxAreaRatio = 0.0008f, minMaskAreaRatio = 0.03f),
        Dairy("\uc720\uc81c\ud488", confidenceThreshold = 0.20f, strongConfidenceThreshold = 0.38f, minBoxAreaRatio = 0.0010f, minMaskAreaRatio = 0.035f),
    }

    private companion object {
        const val TAG = "TfliteFoodDetector"
        const val MODEL_FILE_NAME = "ingredient_yolov8s_seg.tflite"
        const val INPUT_SIZE = 640
        const val INPUT_SIZE_FLOAT = INPUT_SIZE.toFloat()
        const val INPUT_PIXEL_COUNT = INPUT_SIZE * INPUT_SIZE
        const val RGB_CHANNEL_COUNT = 3
        const val FLOAT_BYTE_SIZE = 4
        const val DEFAULT_THREAD_COUNT = 4
        const val BYTE_MASK = 0xFF
        const val PIXEL_MAX_VALUE = 255f
        val LETTERBOX_COLOR = Color.rgb(114, 114, 114)
        const val DETECTION_OUTPUT_RANK = 3
        const val MASK_PROTOTYPE_OUTPUT_RANK = 4
        const val MIN_DETECTION_CHANNEL_COUNT = 9
        const val MAX_DETECTION_CHANNEL_COUNT = 128
        const val MASK_COEFFICIENT_COUNT = 32
        const val BOX_CHANNEL_COUNT = 4
        const val X_CHANNEL = 0
        const val Y_CHANNEL = 1
        const val WIDTH_CHANNEL = 2
        const val HEIGHT_CHANNEL = 3
        const val IOU_THRESHOLD = 0.45f
        const val MIN_BOX_SIZE = 3f
        const val MAX_DETECTIONS_PER_CLASS = 3
        const val MASK_THRESHOLD = 0.50f
        const val MASK_SAMPLE_STEP = 2
        const val NORMALIZED_COORDINATE_MAX = 1.5f
        const val NOT_FOUND = -1
    }
}
