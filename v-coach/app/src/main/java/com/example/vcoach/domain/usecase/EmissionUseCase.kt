package com.example.vcoach.domain.usecase

data class EmissionItem(
    val ingredientName: String,
    val emissionAmount: Int,
)

class EmissionUseCase {
    operator fun invoke(ingredients: List<String>): Int {
        return getItems(ingredients).sumOf { item -> item.emissionAmount }
    }

    fun getItems(ingredients: List<String>): List<EmissionItem> {
        return ingredients
            .distinct()
            .mapNotNull { ingredient ->
                emissionFactors[ingredient]?.let { emissionAmount ->
                    EmissionItem(
                        ingredientName = ingredient,
                        emissionAmount = emissionAmount,
                    )
                }
            }
    }

    private companion object {
        val emissionFactors = mapOf(
            "\uacc4\ub780" to 450,
            "\uc720\uc81c\ud488" to 300,
            "\ub2ed\uace0\uae30" to 650,
            "\uc5b4\ud328\ub958" to 500,
            "\uc721\ub958" to 2400,
        )
    }
}
