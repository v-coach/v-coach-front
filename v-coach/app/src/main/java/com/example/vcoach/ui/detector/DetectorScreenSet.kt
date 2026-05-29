package com.example.vcoach.ui.detector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.vcoach.data.preferences.UserPreferences
import com.example.vcoach.domain.usecase.CheckRestrictedIngredientUseCase
import com.example.vcoach.domain.usecase.GetRestrictedIngredientsUseCase
import com.example.vcoach.ui.detector.components.IngredientAnalysisContent
import com.example.vcoach.ui.detector.components.NutritionAnalysisContent
import com.example.vcoach.ui.detector.components.RestrictedIngredientContent

@Composable
fun DetectorScreenSet(
    selectedTab: Int,
    uiState: DetectorUiState,
    onAlternativeFoodsRequest: (List<String>) -> Unit,
) {
    val detectedIngredientItems = when (uiState) {
        is DetectorUiState.Success -> uiState.detectedIngredients
        else -> emptyList()
    }
    val setListItems = when (uiState) {
        is DetectorUiState.Success -> uiState.setListItems
        else -> emptyList()
    }

    val userPreferences = UserPreferences(LocalContext.current)
    val userType = userPreferences.getUserType()
    val checkRestrictedIngredientUseCase = remember { CheckRestrictedIngredientUseCase() }
    val getRestrictedIngredientsUseCase = remember { GetRestrictedIngredientsUseCase() }
    val restrictedIngredients = remember(userType) {
        getRestrictedIngredientsUseCase(userType)
    }
    val restrictedDetectedIngredientItems = remember(restrictedIngredients, detectedIngredientItems) {
        detectedIngredientItems.filter { ingredient ->
            ingredient in restrictedIngredients
        }
    }
    val hasRestrictedIngredient = remember(userType, detectedIngredientItems) {
        checkRestrictedIngredientUseCase(
            userType = userType,
            detectedIngredients = detectedIngredientItems,
        )
    }
    val requestIngredients = restrictedDetectedIngredientItems

    LaunchedEffect(requestIngredients) {
        if (requestIngredients.isNotEmpty()) {
            onAlternativeFoodsRequest(requestIngredients)
        }
    }

    when (selectedTab) {
        0 -> IngredientAnalysisContent(detectedIngredientItems = detectedIngredientItems)
        1 -> RestrictedIngredientContent(
            userType = userType,
            hasRestrictedIngredient = hasRestrictedIngredient,
            restrictedIngredientItems = restrictedDetectedIngredientItems,
            setListItems = setListItems,
        )
        2 -> NutritionAnalysisContent()
    }
}
