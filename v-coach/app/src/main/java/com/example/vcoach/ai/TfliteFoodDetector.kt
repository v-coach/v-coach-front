package com.example.vcoach.ai

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.Closeable
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TfliteFoodDetector(
    context: Context,
) : FoodDetector, Closeable {
    private val appContext = context.applicationContext
    private var interpreter: Interpreter? = null

    override suspend fun detect(bitmap: Bitmap): List<FoodDetectionResult> = withContext(Dispatchers.Default) {
        val input = bitmap.toModelInput()
        val output = Array(1) { Array(INPUT_SIZE) { LongArray(INPUT_SIZE) } }

        getInterpreter().run(input, output)

        output[0]
            .countVisibleGroups()
            .map { (group, pixelCount) ->
                FoodDetectionResult(
                    ingredientName = group.displayName,
                    confidence = pixelCount.toFloat() / MASK_PIXEL_COUNT,
                )
            }
            .sortedByDescending { it.confidence }
    }

    override fun close() {
        interpreter?.close()
        interpreter = null
    }

    private fun getInterpreter(): Interpreter {
        return interpreter ?: Interpreter(
            loadModelFile(),
            Interpreter.Options().setNumThreads(DEFAULT_THREAD_COUNT),
        ).also { interpreter = it }
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

    private fun Bitmap.toModelInput(): ByteBuffer {
        val resizedBitmap = if (width == INPUT_SIZE && height == INPUT_SIZE) {
            this
        } else {
            Bitmap.createScaledBitmap(this, INPUT_SIZE, INPUT_SIZE, true)
        }
        val pixels = IntArray(MASK_PIXEL_COUNT)
        resizedBitmap.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)

        return ByteBuffer
            .allocateDirect(MASK_PIXEL_COUNT * RGB_CHANNEL_COUNT * FLOAT_BYTE_SIZE)
            .order(ByteOrder.nativeOrder())
            .apply {
                for (channel in 0 until RGB_CHANNEL_COUNT) {
                    val mean = IMAGE_MEAN[channel]
                    val std = IMAGE_STD[channel]

                    pixels.forEach { pixel ->
                        val value = when (channel) {
                            RED_CHANNEL -> (pixel shr 16) and BYTE_MASK
                            GREEN_CHANNEL -> (pixel shr 8) and BYTE_MASK
                            else -> pixel and BYTE_MASK
                        }
                        putFloat(((value / PIXEL_MAX_VALUE) - mean) / std)
                    }
                }
                rewind()
            }
    }

    private fun Array<LongArray>.countVisibleGroups(): Map<IngredientGroup, Int> {
        val counts = mutableMapOf<IngredientGroup, Int>()

        forEach { row ->
            row.forEach { classId ->
                CLASS_ID_TO_GROUP[classId.toInt()]?.let { group ->
                    counts[group] = counts.getOrDefault(group, 0) + 1
                }
            }
        }

        return counts.filterValues { pixelCount ->
            pixelCount >= MIN_VISIBLE_PIXEL_COUNT
        }
    }

    private enum class IngredientGroup(
        val displayName: String,
    ) {
        Egg("\uacc4\ub780"),
        Dairy("\uc720\uc81c\ud488"),
        Chicken("\ub2ed\uace0\uae30"),
        Seafood("\uc5b4\ud328\ub958"),
        Meat("\uc721\ub958"),
    }

    private companion object {
        const val MODEL_FILE_NAME = "foodseg103.tflite"
        const val INPUT_SIZE = 512
        const val MASK_PIXEL_COUNT = INPUT_SIZE * INPUT_SIZE
        const val RGB_CHANNEL_COUNT = 3
        const val FLOAT_BYTE_SIZE = 4
        const val DEFAULT_THREAD_COUNT = 4
        const val BYTE_MASK = 0xFF
        const val PIXEL_MAX_VALUE = 255f
        const val RED_CHANNEL = 0
        const val GREEN_CHANNEL = 1
        const val MIN_VISIBLE_PIXEL_RATIO = 0.0025f
        val MIN_VISIBLE_PIXEL_COUNT = (MASK_PIXEL_COUNT * MIN_VISIBLE_PIXEL_RATIO).toInt()

        val IMAGE_MEAN = floatArrayOf(0.485f, 0.456f, 0.406f)
        val IMAGE_STD = floatArrayOf(0.229f, 0.224f, 0.225f)

        val CLASS_ID_TO_GROUP = mapOf(
            2 to IngredientGroup.Egg,
            24 to IngredientGroup.Egg,
            8 to IngredientGroup.Dairy,
            9 to IngredientGroup.Dairy,
            12 to IngredientGroup.Dairy,
            15 to IngredientGroup.Dairy,
            48 to IngredientGroup.Chicken,
            53 to IngredientGroup.Seafood,
            54 to IngredientGroup.Seafood,
            55 to IngredientGroup.Seafood,
            56 to IngredientGroup.Seafood,
            46 to IngredientGroup.Meat,
            47 to IngredientGroup.Meat,
            49 to IngredientGroup.Meat,
            50 to IngredientGroup.Meat,
            51 to IngredientGroup.Meat,
            60 to IngredientGroup.Meat,
        )
    }
}
