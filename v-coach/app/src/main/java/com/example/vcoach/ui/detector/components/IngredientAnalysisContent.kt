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
                    text = "식품 제한 여부를 확인해 주세요",
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
    val report = buildCarbonReport(detectedIngredientItems)

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
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CarbonCloudIcon()

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = "현재 배출량",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                )

                Text(
                    text = if (detectedIngredientItems.isEmpty()) {
                        "분석 대기 중"
                    } else {
                        "${formatEmission(report.currentEmission)} kg CO2e"
                    },
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (detectedIngredientItems.isEmpty()) Color.Gray else Color.Black,
                )

                Text(
                    text = if (detectedIngredientItems.isEmpty()) {
                        "식품을 인식하면 예상 배출량을 보여드려요"
                    } else {
                        carbonEmissionLevelText(report.currentEmission)
                    },
                    fontSize = 13.sp,
                    color = Color(0xFF555555),
                    lineHeight = 18.sp,
                )
            }
        }

        if (report.alternatives.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF6FBF8))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "육류 대체 시",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )

                Text(
                    text = "${formatEmission(report.replacedEmission)} kg CO2e",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = VCoachGreen,
                )

                Text(
                    text = "약 ${formatEmission(report.savedEmission)} kg CO2e 절약 가능",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                )

                report.alternatives.forEach { alternative ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "${alternative.meatName} -> ${alternative.replacementName} 대체",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF333333),
                        )

                        Text(
                            text = alternative.description,
                            fontSize = 12.sp,
                            color = Color(0xFF666666),
                            lineHeight = 17.sp,
                        )
                    }
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

private fun buildCarbonReport(
    detectedIngredientItems: List<String>,
): CarbonReport {
    val currentEmission = detectedIngredientItems.sumOf { ingredient ->
        carbonEmissionFor(ingredient)
    }
    val alternatives = detectedIngredientItems.mapNotNull { ingredient ->
        findMeatAlternative(ingredient)
    }
    val savedEmission = alternatives.sumOf { alternative ->
        (alternative.meatEmission - alternative.replacementEmission).coerceAtLeast(0.0)
    }

    return CarbonReport(
        currentEmission = currentEmission,
        replacedEmission = (currentEmission - savedEmission).coerceAtLeast(0.0),
        savedEmission = savedEmission,
        alternatives = alternatives,
    )
}

private fun carbonEmissionFor(
    ingredient: String,
): Double {
    return carbonEmissionFactors.entries
        .firstOrNull { (name, _) ->
            ingredient.contains(name, ignoreCase = true)
        }
        ?.value ?: DEFAULT_CARBON_EMISSION
}

private fun findMeatAlternative(
    ingredient: String,
): MeatAlternative? {
    return meatAlternativeFactors.entries
        .firstOrNull { (meatName, _) ->
            ingredient.contains(meatName, ignoreCase = true)
        }
        ?.let { (meatName, replacement) ->
            MeatAlternative(
                meatName = ingredient.ifBlank { meatName },
                replacementName = replacement.name,
                meatEmission = carbonEmissionFor(ingredient),
                replacementEmission = replacement.emission,
                description = replacement.description,
            )
        }
}

private fun formatEmission(value: Double): String {
    return String.format(Locale.US, "%.1f", value)
}

private fun carbonEmissionLevelText(emission: Double): String {
    return when {
        emission >= 30.0 -> "탄소 배출량이 매우 높은 편이에요"
        emission >= 10.0 -> "탄소 배출량이 높은 편이에요"
        emission >= 3.0 -> "탄소 배출량이 보통 수준이에요"
        else -> "탄소 배출량이 낮은 편이에요"
    }
}

private data class CarbonReport(
    val currentEmission: Double,
    val replacedEmission: Double,
    val savedEmission: Double,
    val alternatives: List<MeatAlternative>,
)

private data class MeatAlternative(
    val meatName: String,
    val replacementName: String,
    val meatEmission: Double,
    val replacementEmission: Double,
    val description: String,
)

private data class ReplacementFactor(
    val name: String,
    val emission: Double,
    val description: String,
)

private val meatAlternativeFactors = mapOf(
    "소고기" to ReplacementFactor(
        name = "두부",
        emission = 3.0,
        description = "소고기는 반추동물 사육 과정에서 메탄 배출이 커 탄소 발자국이 높은 편이에요.",
    ),
    "쇠고기" to ReplacementFactor(
        name = "두부",
        emission = 3.0,
        description = "소고기는 반추동물 사육 과정에서 메탄 배출이 커 탄소 발자국이 높은 편이에요.",
    ),
    "beef" to ReplacementFactor(
        name = "tofu",
        emission = 3.0,
        description = "Beef has a high carbon footprint because cattle emit methane during digestion.",
    ),
    "돼지고기" to ReplacementFactor(
        name = "버섯",
        emission = 1.0,
        description = "돼지고기는 소·양보다 메탄 배출은 적지만 사료 소비와 분뇨 처리 과정에서 온실가스가 발생해요.",
    ),
    "pork" to ReplacementFactor(
        name = "mushroom",
        emission = 1.0,
        description = "Pork emits less methane than beef or lamb, but feed production and manure still add emissions.",
    ),
    "닭고기" to ReplacementFactor(
        name = "병아리콩",
        emission = 2.0,
        description = "닭고기는 사료 효율이 높아 흔한 육류 중 탄소 발자국이 낮은 편이지만, 식물성 재료로 바꾸면 더 줄일 수 있어요.",
    ),
    "chicken" to ReplacementFactor(
        name = "chickpea",
        emission = 2.0,
        description = "Chicken is relatively efficient among meats, but swapping it for legumes can reduce emissions further.",
    ),
    "양고기" to ReplacementFactor(
        name = "두부",
        emission = 3.0,
        description = "양고기도 반추동물에서 나오는 메탄 영향이 커 탄소 배출량이 높은 편이에요.",
    ),
    "lamb" to ReplacementFactor(
        name = "tofu",
        emission = 3.0,
        description = "Lamb has high emissions because sheep are ruminants that produce methane.",
    ),
)

private val carbonEmissionFactors = mapOf(
    "소고기" to 60.0,
    "쇠고기" to 60.0,
    "beef" to 60.0,
    "양고기" to 24.0,
    "lamb" to 24.0,
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
    "병아리콩" to 2.0,
    "chickpea" to 2.0,
    "우유" to 3.0,
    "milk" to 3.0,
    "토마토" to 1.4,
    "tomato" to 1.4,
    "버섯" to 1.0,
    "mushroom" to 1.0,
    "감자" to 0.5,
    "potato" to 0.5,
    "채소" to 0.4,
    "vegetable" to 0.4,
)

private const val DEFAULT_CARBON_EMISSION = 2.0
