package com.example.vcoach.ui.detector

sealed interface DetectorUiState {
    data object Idle : DetectorUiState
    data object Loading : DetectorUiState
    data class Success(val message: String) : DetectorUiState
    data class Error(val message: String) : DetectorUiState
}
