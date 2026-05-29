package com.example.vcoach.ui.detector

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vcoach.ai.TfliteFoodDetector
import com.example.vcoach.data.preferences.UserPreferences
import com.example.vcoach.data.remote.IngredientRequest
import com.example.vcoach.data.remote.RetrofitClient
import com.example.vcoach.data.remote.SetListData
import com.example.vcoach.domain.usecase.GetRestrictedIngredientsUseCase
import com.example.vcoach.ui.photo.SelectedPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

class DetectorViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<DetectorUiState>(DetectorUiState.Idle)
    val uiState: StateFlow<DetectorUiState> = _uiState.asStateFlow()

    private val _selectedPhoto = MutableStateFlow<SelectedPhoto?>(null)
    val selectedPhoto: StateFlow<SelectedPhoto?> = _selectedPhoto.asStateFlow()

    private val foodDetector = TfliteFoodDetector(application)
    private val userPreferences = UserPreferences(application)
    private val getRestrictedIngredientsUseCase = GetRestrictedIngredientsUseCase()

    fun setSelectedPhoto(photo: SelectedPhoto) {
        _selectedPhoto.value = photo
        analyzePhoto(photo)
    }

    fun setDetectedIngredients(ingredients: List<String>) {
        _uiState.value = DetectorUiState.Success(
            detectedIngredients = ingredients,
            isAdditionalAnalysisComplete = true,
        )
    }

    private suspend fun getAlternativeFoods(ingredients: List<String>): List<SetListData> {
        val requestIngredients = ingredients.distinct()
        if (requestIngredients.isEmpty()) return emptyList()
        val requestIngredientText = requestIngredients.joinToString(", ")

        Log.d(TAG, "Request alternative foods: ingredient=$requestIngredientText")
        return runCatching {
            RetrofitClient.foodApiService.getAlternativeFoods(
                IngredientRequest(requestIngredientText),
            )
        }.onSuccess { setListItems ->
            Log.d(TAG, "Alternative foods response size=${setListItems.size}, items=$setListItems")

            if (setListItems.isEmpty()) {
                Log.w(TAG, "Alternative foods response is empty")
            }
        }.onFailure { throwable ->
            Log.e(TAG, "Failed to get alternative foods: ingredient=$requestIngredientText", throwable)
        }.getOrDefault(emptyList())
    }

    private suspend fun getAlternativeFoodsForRestrictedIngredients(
        detectedIngredients: List<String>,
    ): List<SetListData> {
        val userType = userPreferences.getUserType()
        val restrictedIngredients = getRestrictedIngredientsUseCase(userType)
        val restrictedDetectedIngredients = detectedIngredients.filter { ingredient ->
            ingredient in restrictedIngredients
        }

        return getAlternativeFoods(restrictedDetectedIngredients)
    }

    fun clearResult() {
        _uiState.value = DetectorUiState.Idle
    }

    fun setError(message: String) {
        _uiState.value = DetectorUiState.Error(message)
    }

    override fun onCleared() {
        foodDetector.close()
        super.onCleared()
    }

    private fun analyzePhoto(photo: SelectedPhoto) {
        viewModelScope.launch {
            _uiState.value = DetectorUiState.Loading()

            runCatching {
                val bitmap = photo.toBitmap()
                    ?: error("Selected photo could not be loaded.")
                val detectedIngredients = foodDetector.detect(bitmap).map { result ->
                    result.ingredientName
                }

                _uiState.value = DetectorUiState.Success(
                    detectedIngredients = detectedIngredients,
                    isAdditionalAnalysisComplete = true,
                )
                fetchAlternativeFoodsInBackground(detectedIngredients)
            }.onFailure { throwable ->
                Log.e(TAG, "Failed to analyze selected photo", throwable)
                setError(throwable.message ?: "Failed to analyze selected photo")
            }
        }
    }

    private fun fetchAlternativeFoodsInBackground(detectedIngredients: List<String>) {
        viewModelScope.launch {
            val setListItems = getAlternativeFoodsForRestrictedIngredients(detectedIngredients)
            if (setListItems.isEmpty()) return@launch

            val currentState = _uiState.value as? DetectorUiState.Success ?: return@launch
            if (currentState.detectedIngredients != detectedIngredients) return@launch

            _uiState.value = currentState.copy(
                setListItems = setListItems,
            )
        }
    }

    private suspend fun SelectedPhoto.toBitmap(): Bitmap? = withContext(Dispatchers.IO) {
        bitmap ?: uri?.let { selectedUri ->
            getApplication<Application>().contentResolver
                .openInputStream(selectedUri)
                ?.use(BitmapFactory::decodeStream)
        }
    }

    private companion object {
        const val TAG = "DetectorViewModel"
    }
}
