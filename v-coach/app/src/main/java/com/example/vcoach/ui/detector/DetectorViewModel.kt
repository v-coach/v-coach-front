package com.example.vcoach.ui.detector

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vcoach.data.remote.IngredientRequest
import com.example.vcoach.data.remote.RetrofitClient
import com.example.vcoach.ui.photo.SelectedPhoto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetectorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<DetectorUiState>(DetectorUiState.Idle)
    val uiState: StateFlow<DetectorUiState> = _uiState.asStateFlow()

    private val _selectedPhoto = MutableStateFlow<SelectedPhoto?>(null)
    val selectedPhoto: StateFlow<SelectedPhoto?> = _selectedPhoto.asStateFlow()

    fun setSelectedPhoto(photo: SelectedPhoto) {
        _selectedPhoto.value = photo
    }

    fun setDetectedIngredients(ingredients: List<String>) {
        _uiState.value = DetectorUiState.Success(
            detectedIngredients = ingredients,
        )
    }

    fun getAlternativeFoods(ingredient: String) {
        viewModelScope.launch {
            Log.d(TAG, "Request alternative foods: ingredient=$ingredient")

            runCatching {
                RetrofitClient.foodApiService.getAlternativeFoods(
                    IngredientRequest(ingredient),
                )
            }.onSuccess { setListItems ->
                Log.d(TAG, "Alternative foods response size=${setListItems.size}, items=$setListItems")

                val detectedIngredients = when (val currentState = _uiState.value) {
                    is DetectorUiState.Success -> currentState.detectedIngredients
                    else -> listOf(ingredient)
                }

                if (setListItems.isEmpty()) {
                    Log.w(TAG, "Alternative foods response is empty")
                }

                _uiState.value = DetectorUiState.Success(
                    detectedIngredients = detectedIngredients,
                    setListItems = setListItems,
                )
            }.onFailure { throwable ->
                Log.e(TAG, "Failed to get alternative foods", throwable)
                setError(throwable.message ?: "Failed to get alternative foods")
            }
        }
    }

    fun clearResult() {
        _uiState.value = DetectorUiState.Idle
    }

    fun setError(message: String) {
        _uiState.value = DetectorUiState.Error(message)
    }

    private companion object {
        const val TAG = "DetectorViewModel"
    }
}
