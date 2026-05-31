package com.example.vcoach.ui.detector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vcoach.ui.components.VCoachTopBar
import com.example.vcoach.ui.detector.components.DetectorTabBar
import com.example.vcoach.ui.detector.components.PhotoPreview
import com.example.vcoach.ui.theme.VCoachGreen
import com.example.vcoach.ui.theme.VCoachTextGray

@Composable
fun DetectorScreen(
    onBackClick: () -> Unit = {},
    onDeleted: () -> Unit = {},
    viewModel: DetectorViewModel = viewModel(),
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val selectedPhoto by viewModel.selectedPhoto.collectAsState()
    val canDeleteCurrentFood by viewModel.canDeleteCurrentFood.collectAsState()

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "삭제 하시겠습니까?",
                    color = Color.Black,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteCurrentFood(onDeleted = onDeleted)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = VCoachGreen,
                    ),
                ) {
                    Text(text = "확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = VCoachTextGray,
                    ),
                ) {
                    Text(text = "취소")
                }
            },
        )
    }

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(Color.White),
        topBar = {
            VCoachTopBar(onBackClick = onBackClick) {
                Spacer(modifier = Modifier.weight(1f))

                if (canDeleteCurrentFood) {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "삭제",
                            tint = Color.Black,
                        )
                    }
                }
            }
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
                )
            }
        }
    }
}
