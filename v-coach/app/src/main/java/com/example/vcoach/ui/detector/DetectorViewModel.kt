package com.example.vcoach.ui.detector

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DetectorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<DetectorUiState>(DetectorUiState.Idle)
    val uiState: StateFlow<DetectorUiState> = _uiState.asStateFlow()
}
