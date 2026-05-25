package com.example.vcoach.ui.detector

import com.example.vcoach.data.remote.SetListData

sealed interface DetectorUiState {
    data object Idle : DetectorUiState
    data object Loading : DetectorUiState
    data class Success(
        val detectedIngredients: List<String>,
        val setListItems: List<SetListData> = emptyList(),
    ) : DetectorUiState
    data class Error(val message: String) : DetectorUiState
}
