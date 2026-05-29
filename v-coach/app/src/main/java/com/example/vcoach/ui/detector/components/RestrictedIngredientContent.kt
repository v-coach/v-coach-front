package com.example.vcoach.ui.detector.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vcoach.data.remote.SetListData
import com.example.vcoach.ui.theme.VCoachGreen

@Composable
fun RestrictedIngredientContent(
    userType: String,
    hasRestrictedIngredient: Boolean,
    restrictedIngredientItems: List<String> = List(DEFAULT_PLACEHOLDER_ITEM_COUNT) { "" },
    setListItems: List<SetListData> = emptyList(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 30.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        AnalysisResultCard(
            text = "제한 성분 분석 완료",
            textColor = VCoachGreen,
        ) {
            Text(
                text = if (hasRestrictedIngredient) {
                    "해당 식품에는 ${userType} 유형이 제한해야 하는 성분이 있어요"
                } else {
                    "이 식품에는 제한해야 하는 성분이 없어요"
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
            )

        }

        if (hasRestrictedIngredient || setListItems.isNotEmpty()) {
            AnalysisResultCard(
                text = "대체 식품 추천",
                textColor = VCoachGreen,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "대체식품을 추천해드려요",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        if (setListItems.isEmpty()) {
                            Text(
                                text = "응답을 기다리는 중입니다.",
                                fontSize = 14.sp,
                                color = Color.Gray,
                            )
                        } else {
                            setListItems.forEach { item ->
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(
                                        text = item.name,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                    )

                                    Text(
                                        text = item.content,
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private const val DEFAULT_PLACEHOLDER_ITEM_COUNT = 2
