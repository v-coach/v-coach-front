package com.example.vcoach.domain

data class AnalysisResult(
    val detectedIngredients: List<Ingredient>,
    val containsTargetIngredient: Boolean,
    val confidence: Float,
)
