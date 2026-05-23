package com.example.vcoach.domain

class DetectFoodUseCase {
    suspend operator fun invoke(): AnalysisResult {
        return AnalysisResult(
            detectedIngredients = emptyList(),
            containsTargetIngredient = false,
            confidence = 0f,
        )
    }
}
