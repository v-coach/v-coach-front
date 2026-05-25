package com.example.vcoach.domain.usecase

class CheckRestrictedIngredientUseCase(
    private val getRestrictedIngredientsUseCase: GetRestrictedIngredientsUseCase = GetRestrictedIngredientsUseCase(),
) {
    operator fun invoke(
        userType: String,
        detectedIngredients: List<String>,
    ): Boolean {
        val restrictedIngredients = getRestrictedIngredientsUseCase(userType)
        return detectedIngredients.any { it in restrictedIngredients }
    }
}
