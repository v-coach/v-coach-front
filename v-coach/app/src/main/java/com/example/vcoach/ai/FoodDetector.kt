package com.example.vcoach.ai

import android.graphics.Bitmap

interface FoodDetector {
    suspend fun detect(bitmap: Bitmap): List<FoodDetectionResult>
}
