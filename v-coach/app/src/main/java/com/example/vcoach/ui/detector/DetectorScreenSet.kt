package com.example.vcoach.ui.detector

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun DetectorScreenSet (
    selectedTab: Int,
    ) {
    when (selectedTab) {
        0 -> IngredientAnalysisContent()
        1 -> AvoidIngredientContent()
        2 -> NutritionAnalysisContent()
    }
}

@Composable
private fun IngredientAnalysisContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 30.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        ResultSummaryCard()

        EmptyInfoCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp),
        )
    }
}

@Composable
private fun AvoidIngredientContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 30.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        EmptyInfoCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp),
        )
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
        EmptyInfoCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp),
        )
    }
}

@Composable
private fun ResultSummaryCard() {
    val detectedIngredientName = ""

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(178.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(2.dp, Color(0xFFD0D0D0)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 26.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFF2FA36B)),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "●",
                        fontSize = 10.sp,
                        color = Color(0xFF147A4B),
                    )

                    Text(
                        text = "AI 분석 완료",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF147A4B),
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

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
                color = Color(0xFF666666),
            )
        }
    }
}

@Composable
private fun EmptyInfoCard(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(2.dp, Color(0xFFD0D0D0)),
    ) {}
}