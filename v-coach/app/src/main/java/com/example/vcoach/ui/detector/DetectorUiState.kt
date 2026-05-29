package com.example.vcoach.ui.detector

import com.example.vcoach.data.remote.SetListData

sealed interface DetectorUiState {
    data object Idle : DetectorUiState
    data class Loading(
        val message: String = "추가로 식품 분석 중입니다",
    ) : DetectorUiState

    data class Success(
        val detectedIngredients: List<String>,
        val setListItems: List<SetListData> = emptyList(),
        val isAdditionalAnalysisComplete: Boolean = false,
    ) : DetectorUiState

    data class Error(val message: String) : DetectorUiState
}
