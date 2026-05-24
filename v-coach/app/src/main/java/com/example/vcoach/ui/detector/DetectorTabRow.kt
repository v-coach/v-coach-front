package com.example.vcoach.ui.detector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.remember

@Composable
fun DetectorTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(58.dp)
                .background(
                    color = if (selectedTab == 0) Color(0xFFEAF3EE) else Color.Transparent,
                    shape = RoundedCornerShape(12.dp),
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    onTabSelected(0)
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "식품 재료 분석",
                fontSize = 16.sp,
                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.SemiBold,
                color = if (selectedTab == 0) Color(0xFF147A4B) else Color(0xFF777777),
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(58.dp)
                .background(
                    color = if (selectedTab == 1) Color(0xFFEAF3EE) else Color.Transparent,
                    shape = RoundedCornerShape(12.dp),
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    onTabSelected(1)
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "피할 재료",
                fontSize = 16.sp,
                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.SemiBold,
                color = if (selectedTab == 1) Color(0xFF147A4B) else Color(0xFF777777),
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(58.dp)
                .background(
                    color = if (selectedTab == 2) Color(0xFFEAF3EE) else Color.Transparent,
                    shape = RoundedCornerShape(12.dp),
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    onTabSelected(2)
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "영양소 분석",
                fontSize = 16.sp,
                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.SemiBold,
                color = if (selectedTab == 2) Color(0xFF147A4B) else Color(0xFF777777),
            )
        }
    }
}