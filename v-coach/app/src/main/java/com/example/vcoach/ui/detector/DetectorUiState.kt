package com.example.vcoach.ui.detector

import com.example.vcoach.data.remote.SetListData
import com.example.vcoach.domain.usecase.EmissionItem

sealed interface DetectorUiState {
    data object Idle : DetectorUiState
    data class Loading(
        val message: String = "식품 분석 중입니다",
    ) : DetectorUiState

    data class Success(
        val foodName: String = "",
        val detectedIngredients: List<String>,
        val emissionAmount: Int = 0,
        val emissionItems: List<EmissionItem> = emptyList(),
        val nutritionItems: List<String> = emptyList(),
        val setListItems: List<SetListData> = emptyList(),
        val isAdditionalAnalysisComplete: Boolean = false,
    ) : DetectorUiState

    data class Error(val message: String) : DetectorUiState
}
