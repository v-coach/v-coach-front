package com.example.vcoach.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vcoach.ui.components.VCoachPrimaryButton
import com.example.vcoach.ui.theme.VCoachGreen
import com.example.vcoach.ui.theme.VCoachLightGreen
import com.example.vcoach.ui.theme.VCoachTextGray

@Composable
fun HomeScreen(
    onAnalyzeClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(Color.White)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "v-coach",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )

        Text(
            text = "식품을 분석하고 나에게 맞는 식단 관리를 해보세요",
            modifier = Modifier.padding(top = 16.dp),
            fontSize = 16.sp,
            color = VCoachTextGray,
        )

        Spacer(modifier = Modifier.height(32.dp))

        HomeStatusPanel()

        Spacer(modifier = Modifier.height(16.dp))

        HomeFlowPanel()

        Spacer(modifier = Modifier.height(42.dp))

        VCoachPrimaryButton(
            text = "식품 분석하기",
            onClick = onAnalyzeClick,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(23.dp))

        OutlinedButton(
            onClick = onSettingClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = VCoachGreen,
            ),
        ) {
            Text(
                text = "설정",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun HomeFlowPanel() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        HomeFlowItem(
            number = "1",
            title = "사진 선택",
            modifier = Modifier.weight(1f),
        )
        HomeFlowItem(
            number = "2",
            title = "AI 분석",
            modifier = Modifier.weight(1f),
        )
        HomeFlowItem(
            number = "3",
            title = "결과 확인",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun HomeFlowItem(
    number: String,
    title: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(10.dp),
            )
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Text(
            text = number,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = VCoachGreen,
        )
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = VCoachTextGray,
        )
    }
}

@Composable
private fun HomeStatusPanel() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = VCoachLightGreen,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 18.dp, vertical = 17.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            HomeStatusItem(
                title = "성분",
                value = "5종",
                modifier = Modifier.weight(1f),
            )
            HomeStatusItem(
                title = "분석",
                value = "AI",
                modifier = Modifier.weight(1f),
            )
            HomeStatusItem(
                title = "기록",
                value = "저장",
                modifier = Modifier.weight(1f),
            )
        }

        Text(
            text = "사진을 선택하면 식품명, 제한 성분, 배출량을 함께 정리합니다",
            fontSize = 13.sp,
            color = VCoachTextGray,
        )
    }
}

@Composable
private fun HomeStatusItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(10.dp),
            )
            .padding(vertical = 13.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = VCoachGreen,
        )
    }
}
