package com.example.vcoach.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vcoach.data.preferences.UserPreferences
import com.example.vcoach.ui.components.VCoachOptionCard
import com.example.vcoach.ui.components.VCoachPrimaryButton
import com.example.vcoach.ui.components.VCoachTopBar
import com.example.vcoach.ui.theme.VCoachTextGray

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    var selectedUserType by remember {
        mutableStateOf(userPreferences.getUserType())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        VCoachTopBar(
            onBackClick = onBackClick,
        ) {
            Text(
                text = "설정",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
        ) {
            Text(
                text = "식단 설정",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )

            Text(
                text = "선택한 단계에 맞춰 식품 분석 결과를 제공합니다.",
                modifier = Modifier.padding(top = 10.dp, bottom = 28.dp),
                fontSize = 15.sp,
                color = VCoachTextGray,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                USER_TYPES.forEach { userType ->
                    VCoachOptionCard(
                        title = "${userType} 단계",
                        selected = selectedUserType == userType,
                        onClick = {
                            selectedUserType = userType
                        },
                    )
                }
            }
        }

        VCoachPrimaryButton(
            text = "저장하기",
            onClick = {
                userPreferences.saveUserType(selectedUserType)
                onBackClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .height(58.dp),
        )
    }
}

private val USER_TYPES = listOf("A", "B", "C", "D", "E", "F")
