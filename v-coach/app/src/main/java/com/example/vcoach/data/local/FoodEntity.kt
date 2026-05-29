package com.example.vcoach.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val foodName: String,
    val imagePath: String,
    val includedIngredients: List<String>,
    val emissionAmount: Int,
    val alternativeFoods: List<String>,
    val data: List<String>,
)
