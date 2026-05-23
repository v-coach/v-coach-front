package com.example.vcoach.ui.detector

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DetectorTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
) {
    TabRow(
        selectedTabIndex = selectedTab,
        modifier = Modifier.height(72.dp),
        containerColor = Color.White,
        contentColor = Color(0xFF147A4B),
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = Color(0xFF147A4B),
            )
        },
        divider = {},
    ) {
        Tab(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            text = {
                Text(
                    text = "식품 재료 분석",
                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                )
            },
            selectedContentColor = Color(0xFF147A4B),
            unselectedContentColor = Color(0xFF555555),
        )

        Tab(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            text = {
                Text(
                    text = "피해야 하는 재료",
                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                )
            },
            selectedContentColor = Color(0xFF147A4B),
            unselectedContentColor = Color(0xFF555555),
        )

        Tab(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            text = {
                Text(
                    text = "영양소 분석",
                    fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                )
            },
            selectedContentColor = Color(0xFF147A4B),
            unselectedContentColor = Color(0xFF555555),
        )
    }
}