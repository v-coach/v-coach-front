package com.example.vcoach.ui.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
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

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val preferences = remember {
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
    var selectedUserType by remember {
        mutableStateOf(preferences.getString(USER_TYPE_KEY, DEFAULT_USER_TYPE) ?: DEFAULT_USER_TYPE)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        SettingsTopBar(
            onBackClick = onBackClick
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
                text = "선택한 단계에 맞춰 음식 분석 결과를 제공해요.",
                modifier = Modifier.padding(top = 10.dp, bottom = 28.dp),
                fontSize = 15.sp,
                color = Color(0xFF777777),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                USER_TYPES.forEach { userType ->
                    SettingOptionCard(
                        title = "${userType} 단계",
                        selected = selectedUserType == userType,
                        onClick = {
                            selectedUserType = userType
                        },
                    )
                }
            }
        }

        ElevatedButton(
            onClick = {
                preferences.edit()
                    .putString(USER_TYPE_KEY, selectedUserType)
                    .apply()
                onBackClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .height(58.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF147A4B),
                contentColor = Color.White,
            ),
        ) {
            Text(
                text = "저장하기",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}


private const val PREFERENCE_NAME = "v_coach_preferences"
private const val USER_TYPE_KEY = "user_type"
private const val DEFAULT_USER_TYPE = "A"

private val USER_TYPES = listOf("A", "B", "C", "D", "E", "F")
