package com.example.vcoach.ui.selectitem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vcoach.ui.detector.DetectorTopBar

@Composable
fun SelectItemScreen(
    onBackClick: () -> Unit = {},
    onAddPhotoClick: () -> Unit = {},
) {
    val foodItems = remember { emptyList<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        DetectorTopBar(onBackClick = onBackClick)

        if (foodItems.isEmpty()) {
            EmptyFoodItemContent(
                onAddPhotoClick = onAddPhotoClick,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun EmptyFoodItemContent(
    onAddPhotoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "사진을 통해 식품을 분석해 보세요",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(28.dp))

        ElevatedButton(
            onClick = onAddPhotoClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF147A4B),
                contentColor = Color.White,
            ),
        ) {
            Text(
                text = "사진 추가하기",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
