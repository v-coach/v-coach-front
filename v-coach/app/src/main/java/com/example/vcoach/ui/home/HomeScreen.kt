package com.example.vcoach.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.vcoach.ui.detector.DetectorTopBar

@Composable
fun HomeScreen (
    onBackClick: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            DetectorTopBar(onBackClick = onBackClick)
        },
        bottomBar = {
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White),
        ) {

        }
    }

}
