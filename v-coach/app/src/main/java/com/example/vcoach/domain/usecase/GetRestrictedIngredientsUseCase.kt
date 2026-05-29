package com.example.vcoach.domain.usecase

class GetRestrictedIngredientsUseCase {
    operator fun invoke(userType: String): List<String> {
        return RestrictedIngredientSet.getRestrictedIngredients(userType)
    }
}

object RestrictedIngredientSet {
    val ingredientOrder = listOf(
        "유제품",
        "계란",
        "어패류",
        "닭고기",
        "육류",
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
