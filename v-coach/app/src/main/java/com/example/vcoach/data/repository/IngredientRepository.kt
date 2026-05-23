package com.example.vcoach.data.repository

import com.example.vcoach.domain.Ingredient

interface IngredientRepository {
    suspend fun getTargetIngredients(): List<Ingredient>
}
