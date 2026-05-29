package com.example.vcoach.ui.detector.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vcoach.ui.theme.VCoachGreen
import com.example.vcoach.ui.theme.VCoachLightGreen
import com.example.vcoach.ui.theme.VCoachTextGray

@Composable
fun IngredientAnalysisContent(
    detectedIngredientItems: List<String>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 30.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        AnalysisResultCard(
            text = "AI 분석 완료",
            textColor = VCoachGreen,
        ) {
            IngredientResultSummary(detectedIngredientItems = detectedIngredientItems)
        }
    }
}

@Composable
private fun IngredientResultSummary(
    detectedIngredientItems: List<String>,
) {
    if (detectedIngredientItems.isNotEmpty()) {
        DetectedIngredient(detectedIngredientItems = detectedIngredientItems)

        Spacer(modifier = Modifier.height(18.dp))

        DetectedIngredientList(detectedIngredientItems = detectedIngredientItems)

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "식단 제한이 있다면 성분을 확인해 주세요",
            fontSize = 14.sp,
            color = VCoachTextGray,
        )
    } else {
        EmptyIngredientResult()
    }
}

@Composable
private fun DetectedIngredient(
    detectedIngredientItems: List<String>,
) {
    val detectedIngredientName = detectedIngredientItems.joinToString(", ")

    Text(
        text = "식품에서 ${detectedIngredientName}가 보여요",
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        lineHeight = 32.sp,
    )
}

@Composable
private fun DetectedIngredientList(
    detectedIngredientItems: List<String>,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = "포함된 성분",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            detectedIngredientItems.forEach { ingredient ->
                DetectedIngredientItem(ingredient = ingredient)
            }
        }
    }
}

@Composable
private fun DetectedIngredientItem(
    ingredient: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = VCoachLightGreen,
                shape = RoundedCornerShape(10.dp),
            )
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = ingredient,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
        )

        Text(
            text = "확인됨",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = VCoachGreen,
        )
    }
}

@Composable
private fun EmptyIngredientResult() {
    Text(
        text = "식품에서 확인되는 성분이 없어요",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = VCoachTextGray,
    )
}
