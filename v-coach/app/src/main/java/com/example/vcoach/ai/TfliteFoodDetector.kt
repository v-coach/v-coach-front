package com.example.vcoach.ai

class TfliteFoodDetector : FoodDetector {
    override suspend fun detect(): List<FoodDetectionResult> {
        return emptyList()
    }
}
