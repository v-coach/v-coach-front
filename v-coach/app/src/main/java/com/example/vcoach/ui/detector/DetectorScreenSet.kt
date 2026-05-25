package com.example.vcoach.ui.detector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.vcoach.data.preferences.UserPreferences
import com.example.vcoach.domain.usecase.CheckRestrictedIngredientUseCase
import com.example.vcoach.ui.detector.components.IngredientAnalysisContent
import com.example.vcoach.ui.detector.components.NutritionAnalysisContent
import com.example.vcoach.ui.detector.components.RestrictedIngredientContent

@Composable
fun DetectorScreenSet(
    selectedTab: Int,
    uiState: DetectorUiState,
) {
    val detectedIngredientItems = when (uiState) {
        is DetectorUiState.Success -> uiState.detectedIngredients
        else -> emptyList()
    }
    val userPreferences = UserPreferences(LocalContext.current)
    val userType = userPreferences.getUserType()
    val checkRestrictedIngredientUseCase = remember { CheckRestrictedIngredientUseCase() }
    val hasRestrictedIngredient = remember(userType, detectedIngredientItems) {
        checkRestrictedIngredientUseCase(
            userType = userType,
            detectedIngredients = detectedIngredientItems,
        )
    }

    when (selectedTab) {
        0 -> IngredientAnalysisContent(detectedIngredientItems = detectedIngredientItems)
        1 -> RestrictedIngredientContent(
            userType = userType,
            hasRestrictedIngredient = hasRestrictedIngredient,
        )
        2 -> NutritionAnalysisContent()
    }
}
