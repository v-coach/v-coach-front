package com.example.vcoach.ai

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.io.Closeable
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TfliteFoodNameClassifier(
    context: Context,
) : Closeable {
    private val appContext = context.applicationContext
    private var interpreter: Interpreter? = null
    private val displayLabels: List<String> by lazy { loadDisplayLabels() }

    suspend fun classify(bitmap: Bitmap): FoodNamePrediction = withContext(Dispatchers.Default) {
        val input = bitmap.toModelInput()
        val output = Array(1) { FloatArray(displayLabels.size) }

        getInterpreter().run(input, output)

        val scores = output[0]
        val topIndex = scores.indices.maxBy { index -> scores[index] }
        FoodNamePrediction(
            foodName = displayLabels[topIndex],
            confidence = scores[topIndex],
        )
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

    private fun loadDisplayLabels(): List<String> {
        val json = appContext.assets.open(LABELS_FILE_NAME)
            .bufferedReader()
            .use { reader -> reader.readText() }
        val labelArray = JSONObject(json).getJSONArray(DISPLAY_LABELS_KEY)
        return List(labelArray.length()) { index -> labelArray.getString(index) }
    }

    private fun Bitmap.toModelInput(): ByteBuffer {
        val resizedBitmap = if (width == INPUT_SIZE && height == INPUT_SIZE) {
            this
        } else {
            Bitmap.createScaledBitmap(this, INPUT_SIZE, INPUT_SIZE, true)
        }
        val pixels = IntArray(INPUT_PIXEL_COUNT)
        resizedBitmap.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)

        return ByteBuffer
            .allocateDirect(INPUT_PIXEL_COUNT * RGB_CHANNEL_COUNT * FLOAT_BYTE_SIZE)
            .order(ByteOrder.nativeOrder())
            .apply {
                pixels.forEach { pixel ->
                    putFloat(((pixel shr 16) and BYTE_MASK).toFloat())
                    putFloat(((pixel shr 8) and BYTE_MASK).toFloat())
                    putFloat((pixel and BYTE_MASK).toFloat())
                }
                rewind()
            }
    }

    private companion object {
        const val MODEL_FILE_NAME = "food101_classifier.tflite"
        const val LABELS_FILE_NAME = "food101_labels.json"
        const val DISPLAY_LABELS_KEY = "display_labels"
        const val INPUT_SIZE = 224
        const val INPUT_PIXEL_COUNT = INPUT_SIZE * INPUT_SIZE
        const val RGB_CHANNEL_COUNT = 3
        const val FLOAT_BYTE_SIZE = 4
        const val BYTE_MASK = 0xFF
        const val DEFAULT_THREAD_COUNT = 4
    }
}
