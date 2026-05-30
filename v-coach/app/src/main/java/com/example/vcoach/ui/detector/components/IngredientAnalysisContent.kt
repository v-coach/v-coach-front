package com.example.vcoach.ui.detector.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vcoach.ui.theme.VCoachGreen
import java.util.Locale

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
                    text = "식품에 ${detectedIngredientName}가 보여요",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 32.sp,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "식품 제한 여부와 탄소 배출량을 확인해 주세요",
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

        CarbonEmissionCard(detectedIngredientItems = detectedIngredientItems)
    }
}

@Composable
private fun CarbonEmissionCard(
    detectedIngredientItems: List<String>,
) {
    val carbonEmission = estimateCarbonEmission(detectedIngredientItems)
    val formattedEmission = String.format(Locale.US, "%.1f", carbonEmission)
    val mainIngredient = detectedIngredientItems.firstOrNull() ?: "식품"

    AnalysisResultCard(
        text = "탄소 배출량",
        textColor = VCoachGreen,
    ) {
        Text(
            text = "탄소 배출량",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFEAF7EE))
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            CarbonCloudIcon()

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = "탄소 배출량",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                )

                if (detectedIngredientItems.isNotEmpty()) {
                    Text(
                        text = "$formattedEmission kg CO2e",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    )

                    Text(
                        text = "$mainIngredient 기준 예상 배출량이에요",
                        fontSize = 13.sp,
                        color = Color(0xFF555555),
                    )
                } else {
                    Text(
                        text = "분석 대기 중",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                    )

                    Text(
                        text = "식품을 인식하면 예상 배출량을 보여드려요",
                        fontSize = 13.sp,
                        color = Color(0xFF555555),
                    )
                }
            }
        }
    }
}

@Composable
private fun CarbonCloudIcon() {
    Box(
        modifier = Modifier.size(72.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cloudFill = Color(0xFFD7EEF4)
            val cloudStroke = Color(0xFF5B8A99)
            val strokeWidth = 3.dp.toPx()

            drawRoundRect(
                color = cloudFill,
                topLeft = Offset(size.width * 0.14f, size.height * 0.28f),
                size = Size(size.width * 0.72f, size.height * 0.36f),
                cornerRadius = CornerRadius(size.height * 0.18f, size.height * 0.18f),
            )
            drawCircle(
                color = cloudFill,
                radius = size.width * 0.18f,
                center = Offset(size.width * 0.34f, size.height * 0.31f),
            )
            drawCircle(
                color = cloudFill,
                radius = size.width * 0.23f,
                center = Offset(size.width * 0.52f, size.height * 0.26f),
            )
            drawCircle(
                color = cloudFill,
                radius = size.width * 0.18f,
                center = Offset(size.width * 0.68f, size.height * 0.35f),
            )

            drawRoundRect(
                color = cloudStroke,
                topLeft = Offset(size.width * 0.14f, size.height * 0.28f),
                size = Size(size.width * 0.72f, size.height * 0.36f),
                cornerRadius = CornerRadius(size.height * 0.18f, size.height * 0.18f),
                style = Stroke(width = strokeWidth),
            )

        }

        Text(
            text = "CO2",
            modifier = Modifier.padding(bottom = 2.dp),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF315E6D),
            textAlign = TextAlign.Center,
        )
    }
}

private fun estimateCarbonEmission(
    detectedIngredientItems: List<String>,
): Double {
    if (detectedIngredientItems.isEmpty()) return 0.0

    val totalEmission = detectedIngredientItems.sumOf { ingredient ->
        carbonEmissionFactors.entries
            .firstOrNull { (name, _) ->
                ingredient.contains(name, ignoreCase = true)
            }
            ?.value ?: DEFAULT_CARBON_EMISSION
    }

    return totalEmission / detectedIngredientItems.size
}

private val carbonEmissionFactors = mapOf(
    "소고기" to 60.0,
    "쇠고기" to 60.0,
    "beef" to 60.0,
    "양고기" to 24.0,
    "치즈" to 21.0,
    "돼지고기" to 7.0,
    "pork" to 7.0,
    "닭고기" to 6.5,
    "chicken" to 6.5,
    "생선" to 5.0,
    "fish" to 5.0,
    "계란" to 4.5,
    "달걀" to 4.5,
    "egg" to 4.5,
    "쌀" to 4.0,
    "rice" to 4.0,
    "두부" to 3.0,
    "tofu" to 3.0,
    "우유" to 3.0,
    "milk" to 3.0,
    "토마토" to 1.4,
    "tomato" to 1.4,
    "감자" to 0.5,
    "potato" to 0.5,
    "채소" to 0.4,
    "vegetable" to 0.4,
)

private const val DEFAULT_CARBON_EMISSION = 2.0
