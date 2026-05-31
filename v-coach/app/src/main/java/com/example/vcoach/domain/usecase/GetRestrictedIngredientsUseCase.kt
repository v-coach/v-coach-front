package com.example.vcoach.domain.usecase

class GetRestrictedIngredientsUseCase {
    operator fun invoke(userType: String): List<String> {
        return RestrictedIngredientSet.getRestrictedIngredients(userType)
    }
}

object RestrictedIngredientSet {
    val ingredientOrder = listOf(
        "\uacc4\ub780",
        "\uc720\uc81c\ud488",
        "\uc5b4\ud328\ub958",
        "\ub2ed\uace0\uae30",
        "\uc721\ub958",
    )

    val userTypes = listOf("A", "B", "C", "D", "E")

    fun getRestrictedIngredients(userType: String): List<String> {
        val count = when (userType) {
            "A" -> 1
            "B" -> 2
            "C" -> 3
            "D" -> 4
            "E" -> 5
            else -> 1
        }

        return ingredientOrder.take(count)
    }
}
