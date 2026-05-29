package com.example.vcoach.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface FoodApiService {
    @POST("alternative-foods")
    suspend fun getAlternativeFoods(
        @Body request: IngredientRequest,
    ): List<SetListData>
}
