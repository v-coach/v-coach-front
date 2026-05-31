package com.example.vcoach.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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

        Spacer(modifier = Modifier.height(56.dp))

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
