package com.example.vcoach.domain.usecase

class GetRestrictedIngredientsUseCase {
    operator fun invoke(userType: String): List<String> {
        val allowedUntilIndex = when (userType) {
            "A" -> -1
            "B" -> 0
            "C" -> 1
            "D" -> 2
            "E" -> 3
            "F" -> 4
            else -> -1
        }

        return TARGET_INGREDIENTS.drop(allowedUntilIndex + 1)
    }

    private companion object {
        val TARGET_INGREDIENTS = listOf("재료1", "재료2", "재료3", "재료4", "재료5")
    }
}
