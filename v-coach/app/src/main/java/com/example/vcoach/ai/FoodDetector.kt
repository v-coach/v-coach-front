package com.example.vcoach.ai

interface FoodDetector {
    suspend fun detect(): List<FoodDetectionResult>
}
