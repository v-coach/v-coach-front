package com.example.vcoach.ui.detector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vcoach.ui.components.VCoachTopBar
import com.example.vcoach.ui.detector.components.DetectorTabBar
import com.example.vcoach.ui.detector.components.PhotoPreview

@Composable
fun DetectorScreen(
    onBackClick: () -> Unit = {},
    viewModel: DetectorViewModel = viewModel(),
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val uiState by viewModel.uiState.collectAsState()
    val selectedPhoto by viewModel.selectedPhoto.collectAsState()

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(Color.White),
        topBar = {
            VCoachTopBar(onBackClick = onBackClick)
        },
        bottomBar = {
            DetectorTabBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
            )
        },
        containerColor = Color.White,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White),
        ) {
            PhotoPreview(
                selectedPhoto = selectedPhoto,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                DetectorScreenSet(
                    selectedTab = selectedTab,
                    uiState = uiState,
                    onAlternativeFoodsRequest = viewModel::getAlternativeFoods,
                )
            }
        }
    }
}
