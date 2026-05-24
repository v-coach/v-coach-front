package com.example.vcoach.ui.detector

import androidx.compose.runtime.LaunchedEffect
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun DetectorScreenSet (
    selectedTab: Int,
    uiState: DetectorUiState,
) {
    val detectedIngredientItems = when (uiState) {
        is DetectorUiState.Success -> uiState.detectedIngredients
        else -> emptyList()
    }

    when (selectedTab) {
        0 -> IngredientAnalysisContent(detectedIngredientItems = detectedIngredientItems)
        1 -> SetContent(detectedIngredientItems = detectedIngredientItems)
        2 -> NutritionAnalysisContent()
    }
}

@Composable
private fun IngredientAnalysisContent(
    detectedIngredientItems: List<String>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 30.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        val detectedIngredientName = detectedIngredientItems.joinToString(", ")

        SetDataContainer(
            text = "AI 분석 완료",
            textColor = Color(0xFF147A4B),
        ) {
            if (detectedIngredientItems.isNotEmpty()) {
                Text(
                    text = "식품에 ${detectedIngredientName}가 보여요",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 32.sp,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "식단 제한이 있다면 재료를 확인해주세요",
                    fontSize = 14.sp,
                    color = Color.Gray,
                )
            } else {
                Text(
                    text = "식품에 피해야 하는 재료가 없어요",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                )
            }
        }
    }
}

@Composable
private fun SetContent(
    detectedIngredientItems: List<String>,
) {
    val context = LocalContext.current
    val preferences = remember {
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
    val userType = preferences.getString(USER_TYPE_KEY, DEFAULT_USER_TYPE) ?: DEFAULT_USER_TYPE
    val allowedUntilIndex = when (userType) {
        "A" -> -1
        "B" -> 0
        "C" -> 1
        "D" -> 2
        "E" -> 3
        "F" -> 4
        else -> -1
    }
    val detectedIngredients = TARGET_INGREDIENTS.drop(allowedUntilIndex + 1)
    val detectedIngredientItem = detectedIngredientItems.any { it in detectedIngredients }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 30.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        SetDataContainer(
            text = "피할 재료 분석 완료",
            textColor = Color(0xFF147A4B),
        ) {
            Text(
                text = if (detectedIngredientItem) {
                    "해당 식품에는 ${userType} 유형의 사람들이 피해야 하는 재료가 있어요"
                } else {
                    "이 식품은 피해야 하는 재료가 없네요"
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
            )
        }



    }
}

@Composable
private fun NutritionAnalysisContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 30.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {

    }
}

private const val PREFERENCE_NAME = "v_coach_preferences"
private const val USER_TYPE_KEY = "user_type"
private const val DEFAULT_USER_TYPE = "A"

private val TARGET_INGREDIENTS = listOf("재료1", "재료2", "재료3", "재료4", "재료5")
