package com.example.vcoach.ui.detector.components

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vcoach.ui.theme.VCoachGreen

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
        val detectedIngredientName = detectedIngredientItems.joinToString(", ")

        AnalysisResultCard(
            text = "AI 분석 완료",
            textColor = VCoachGreen,
        ) {
            if (detectedIngredientItems.isNotEmpty()) {
                Text(
                    text = "식품에서 ${detectedIngredientName}이 보여요",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 32.sp,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "식단 제한이 있다면 성분을 확인해 주세요",
                    fontSize = 14.sp,
                    color = Color.Gray,
                )
            } else {
                Text(
                    text = "식품에서 확인되는 성분이 없어요",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                )
            }
        }
    }
}
