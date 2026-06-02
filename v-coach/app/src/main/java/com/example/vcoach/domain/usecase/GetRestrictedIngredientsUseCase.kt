package com.example.vcoach.domain.usecase

class GetRestrictedIngredientsUseCase {
    operator fun invoke(userType: String): List<String> {
        return RestrictedIngredientSet.getRestrictedIngredients(userType)
    }
}

object RestrictedIngredientSet {
    val userTypes = listOf("A", "B", "C", "D", "E")

    fun getUserTypeName(userType: String): String {
        return when (userType) {
            "A" -> "\ud3f4\ub85c"
            "B" -> "\ud398\uc2a4\ucf54"
            "C" -> "\ub77d\ud1a0-\uc624\ubcf4"
            "D" -> "\ub77d\ud1a0"
            "E" -> "\ube44\uac74"
            else -> userType
        }
    }

    fun getRestrictedIngredients(userType: String): List<String> {
        return when (userType) {
            "A" -> listOf(MEAT)
            "B" -> listOf(CHICKEN, MEAT)
            "C" -> listOf(SEAFOOD, CHICKEN, MEAT)
            "D" -> listOf(EGG, SEAFOOD, CHICKEN, MEAT)
            "E" -> listOf(DAIRY, EGG, SEAFOOD, CHICKEN, MEAT)
            else -> listOf(MEAT)
        }
    }

    private const val DAIRY = "\uc720\uc81c\ud488"
    private const val EGG = "\uacc4\ub780"
    private const val SEAFOOD = "\uc5b4\ud328\ub958"
    private const val CHICKEN = "\ub2ed\uace0\uae30"
    private const val MEAT = "\uc721\ub958"
}
