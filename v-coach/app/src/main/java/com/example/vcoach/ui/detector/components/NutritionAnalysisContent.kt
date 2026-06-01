package com.example.vcoach.ui.detector.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.vcoach.ui.theme.VCoachTextGray

@Composable
fun NutritionAnalysisContent(
    foodName: String,
    nutritionItems: List<String>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 30.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        AnalysisResultCard(
            text = "영양 분석",
            textColor = VCoachGreen,
        ) {
            if (nutritionItems.isNotEmpty()) {
                Text(
                    text = "${foodName.ifBlank { "식품" }}에 대한 영양 분석입니다",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 32.sp,
                )

                Column(
                    modifier = Modifier.padding(top = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    nutritionItems.forEach { nutritionItem ->
                        NutritionListItem(text = nutritionItem)
                    }
                }
            } else {
                Text(
                    text = "영양 분석 결과를 불러오는 중입니다",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = VCoachTextGray,
                )
            }
        }
    }
}

@Composable
private fun NutritionListItem(
    text: String,
) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
    )
}
