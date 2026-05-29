package com.example.vcoach

import android.graphics.Color
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vcoach.ui.detector.DetectorScreen
import com.example.vcoach.ui.detector.DetectorViewModel
import com.example.vcoach.ui.home.HomeScreen
import com.example.vcoach.ui.navigation.VCoachScreen
import com.example.vcoach.ui.selectitem.SelectItemScreen
import com.example.vcoach.ui.settings.SettingsScreen
import com.example.vcoach.ui.theme.VCoachTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.WHITE
        window.navigationBarColor = Color.WHITE
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        setContent {
            VCoachTheme {
                var currentScreen: VCoachScreen by remember {
                    mutableStateOf(VCoachScreen.Home)
                }
                val detectorViewModel: DetectorViewModel = viewModel()

                BackHandler(enabled = currentScreen != VCoachScreen.Home) {
                    currentScreen = when (currentScreen) {
                        VCoachScreen.Detector -> VCoachScreen.SelectItem
                        VCoachScreen.SelectItem,
                        VCoachScreen.Settings,
                        -> VCoachScreen.Home

                        VCoachScreen.Home -> VCoachScreen.Home
                    }
                }

                when (currentScreen) {
                    VCoachScreen.Home -> HomeScreen(
                        onAnalyzeClick = { currentScreen = VCoachScreen.SelectItem },
                        onSettingClick = { currentScreen = VCoachScreen.Settings },
                    )

                    VCoachScreen.SelectItem -> SelectItemScreen(
                        onBackClick = { currentScreen = VCoachScreen.Home },
                        onPhotoAdded = { photo ->
                            detectorViewModel.setSelectedPhoto(photo)
                            currentScreen = VCoachScreen.Detector
                        },
                    )

                    VCoachScreen.Detector -> DetectorScreen(
                        onBackClick = { currentScreen = VCoachScreen.SelectItem },
                        viewModel = detectorViewModel,
                    )

                    VCoachScreen.Settings -> SettingsScreen(
                        onBackClick = { currentScreen = VCoachScreen.Home },
                    )
                }
            }
        }
    }
}
