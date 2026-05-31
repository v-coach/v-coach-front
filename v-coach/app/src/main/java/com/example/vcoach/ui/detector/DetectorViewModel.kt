package com.example.vcoach.ui.detector

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vcoach.ai.TfliteFoodDetector
import com.example.vcoach.data.local.AppDatabase
import com.example.vcoach.data.local.FoodEntity
import com.example.vcoach.data.preferences.UserPreferences
import com.example.vcoach.data.remote.IngredientRequest
import com.example.vcoach.data.remote.RetrofitClient
import com.example.vcoach.data.remote.SetListData
import com.example.vcoach.domain.usecase.EmissionUseCase
import com.example.vcoach.domain.usecase.GetRestrictedIngredientsUseCase
import com.example.vcoach.ui.photo.SelectedPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import java.io.File

class DetectorViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<DetectorUiState>(DetectorUiState.Idle)
    val uiState: StateFlow<DetectorUiState> = _uiState.asStateFlow()

    private val _selectedPhoto = MutableStateFlow<SelectedPhoto?>(null)
    val selectedPhoto: StateFlow<SelectedPhoto?> = _selectedPhoto.asStateFlow()

    private val _canDeleteCurrentFood = MutableStateFlow(false)
    val canDeleteCurrentFood: StateFlow<Boolean> = _canDeleteCurrentFood.asStateFlow()

    private var currentFoodId: Int? = null

    private val foodDetector = TfliteFoodDetector(application)
    private val foodDao = AppDatabase.getInstance(application).foodDao()
    private val userPreferences = UserPreferences(application)
    private val getRestrictedIngredientsUseCase = GetRestrictedIngredientsUseCase()
    private val emissionUseCase = EmissionUseCase()

    fun setSelectedPhoto(photo: SelectedPhoto) {
        currentFoodId = null
        _canDeleteCurrentFood.value = false
        _selectedPhoto.value = photo
        analyzePhoto(photo)
    }

    fun setDetectedIngredients(ingredients: List<String>) {
        _uiState.value = DetectorUiState.Success(
            detectedIngredients = ingredients,
            emissionAmount = emissionUseCase(ingredients),
            emissionItems = emissionUseCase.getItems(ingredients),
            isAdditionalAnalysisComplete = true,
        )
    }

    fun loadSavedFood(foodId: Int) {
        viewModelScope.launch {
            _uiState.value = DetectorUiState.Loading()

            runCatching {
                val food = withContext(Dispatchers.IO) {
                    foodDao.getFoodById(foodId)
                } ?: error("Saved food could not be loaded.")

                currentFoodId = food.id
                _canDeleteCurrentFood.value = true
                _selectedPhoto.value = SelectedPhoto(
                    uri = Uri.fromFile(File(food.imagePath)),
                    imagePath = food.imagePath,
                )
                _uiState.value = DetectorUiState.Success(
                    detectedIngredients = food.includedIngredients,
                    emissionAmount = food.emissionAmount,
                    emissionItems = emissionUseCase.getItems(food.includedIngredients),
                    setListItems = food.alternativeFoods.mapIndexed { index, foodName ->
                        SetListData(
                            name = foodName,
                            content = food.alternativeFoodDescriptions.getOrElse(index) { "" },
                        )
                    },
                    isAdditionalAnalysisComplete = true,
                )
            }.onFailure { throwable ->
                Log.e(TAG, "Failed to load saved food: id=$foodId", throwable)
                setError(throwable.message ?: "Failed to load saved food")
            }
        }
    }

    fun deleteCurrentFood(onDeleted: () -> Unit = {}) {
        val foodId = currentFoodId ?: return

        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    foodDao.getFoodById(foodId)?.let { food ->
                        foodDao.deleteFood(food)
                        deleteInternalImageFile(food.imagePath)
                    }
                }
            }.onSuccess {
                currentFoodId = null
                _canDeleteCurrentFood.value = false
                _selectedPhoto.value = null
                _uiState.value = DetectorUiState.Idle
                onDeleted()
            }.onFailure { throwable ->
                Log.e(TAG, "Failed to delete food: id=$foodId", throwable)
                setError(throwable.message ?: "Failed to delete food")
            }
        }
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
                val analysisBitmap = bitmap.toAnalysisBitmap()
                val foodName = detectFoodName(analysisBitmap)
                val detectedIngredients = foodDetector.detect(analysisBitmap).map { result ->
                    result.ingredientName
                }
                val emissionAmount = emissionUseCase(detectedIngredients)
                val emissionItems = emissionUseCase.getItems(detectedIngredients)

                _uiState.value = DetectorUiState.Success(
                    detectedIngredients = detectedIngredients,
                    emissionAmount = emissionAmount,
                    emissionItems = emissionItems,
                    isAdditionalAnalysisComplete = true,
                )
                val savedFoodId = saveFoodAnalysis(
                    photo = photo,
                    foodName = foodName,
                    detectedIngredients = detectedIngredients,
                    emissionAmount = emissionAmount,
                )
                fetchAlternativeFoodsAndUpdateInBackground(
                    savedFoodId = savedFoodId,
                    detectedIngredients = detectedIngredients,
                )
            }.onFailure { throwable ->
                Log.e(TAG, "Failed to analyze selected photo", throwable)
                setError(throwable.message ?: "Failed to analyze selected photo")
            }
        }
    }

    private fun fetchAlternativeFoodsAndUpdateInBackground(
        savedFoodId: Int?,
        detectedIngredients: List<String>,
    ) {
        viewModelScope.launch {
            val setListItems = getAlternativeFoodsForRestrictedIngredients(detectedIngredients)
            val currentState = _uiState.value as? DetectorUiState.Success ?: return@launch
            if (currentState.detectedIngredients != detectedIngredients) return@launch

            if (setListItems.isNotEmpty()) {
                _uiState.value = currentState.copy(
                    setListItems = setListItems,
                )
            }

            if (savedFoodId != null && setListItems.isNotEmpty()) {
                updateAlternativeFoods(
                    savedFoodId = savedFoodId,
                    setListItems = setListItems,
                )
            }
        }
    }

    private suspend fun saveFoodAnalysis(
        photo: SelectedPhoto,
        foodName: String,
        detectedIngredients: List<String>,
        emissionAmount: Int,
    ): Int? {
        val imagePath = photo.imagePath ?: return null

        return withContext(Dispatchers.IO) {
            val savedFoodId = foodDao.insertFood(
                FoodEntity(
                    foodName = foodName,
                    imagePath = imagePath,
                    includedIngredients = detectedIngredients,
                    emissionAmount = emissionAmount,
                    alternativeFoods = emptyList(),
                    alternativeFoodDescriptions = emptyList(),
                    data = emptyList(),
                ),
            ).toInt()
            currentFoodId = savedFoodId
            _canDeleteCurrentFood.value = true
            savedFoodId
        }
    }

    private suspend fun updateAlternativeFoods(
        savedFoodId: Int,
        setListItems: List<SetListData>,
    ) {
        withContext(Dispatchers.IO) {
            val savedFood = foodDao.getFoodById(savedFoodId) ?: return@withContext
            foodDao.updateFood(
                savedFood.copy(
                    alternativeFoods = setListItems.map { item -> item.name },
                    alternativeFoodDescriptions = setListItems.map { item -> item.content },
                ),
            )
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private suspend fun detectFoodName(bitmap: Bitmap): String {
        return withContext(Dispatchers.Default) {
            // Food-101 model integration point.
            DEFAULT_FOOD_NAME
        }
    }

    private suspend fun SelectedPhoto.toBitmap(): Bitmap? = withContext(Dispatchers.IO) {
        bitmap ?: uri?.let { selectedUri ->
            getApplication<Application>().contentResolver
                .openInputStream(selectedUri)
                ?.use(BitmapFactory::decodeStream)
        }
    }

    private fun deleteInternalImageFile(imagePath: String) {
        runCatching {
            val filesDir = getApplication<Application>().filesDir.canonicalFile
            val imageFile = File(imagePath).canonicalFile
            if (!imageFile.toPath().startsWith(filesDir.toPath())) return

            if (imageFile.isFile) {
                imageFile.delete()
            }
        }.onFailure { throwable ->
            Log.w(TAG, "Failed to delete image file: path=$imagePath", throwable)
        }
    }

    private suspend fun Bitmap.toAnalysisBitmap(): Bitmap = withContext(Dispatchers.Default) {
        if (width == ANALYSIS_IMAGE_SIZE && height == ANALYSIS_IMAGE_SIZE) {
            this@toAnalysisBitmap
        } else {
            Bitmap.createScaledBitmap(
                this@toAnalysisBitmap,
                ANALYSIS_IMAGE_SIZE,
                ANALYSIS_IMAGE_SIZE,
                true,
            )
        }
    }

    private companion object {
        const val TAG = "DetectorViewModel"
        const val ANALYSIS_IMAGE_SIZE = 512
        const val DEFAULT_FOOD_NAME = "식품"
    }
}
