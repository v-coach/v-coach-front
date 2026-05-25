package com.example.vcoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.vcoach.ui.detector.DetectorScreen
import com.example.vcoach.ui.home.HomeScreen
import com.example.vcoach.ui.navigation.VCoachScreen
import com.example.vcoach.ui.selectitem.SelectItemScreen
import com.example.vcoach.ui.settings.SettingsScreen
import com.example.vcoach.ui.theme.VCoachTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VCoachTheme {
                var currentScreen: VCoachScreen by remember {
                    mutableStateOf(VCoachScreen.Home)
                }

                when (currentScreen) {
                    VCoachScreen.Home -> HomeScreen(
                        onAnalyzeClick = { currentScreen = VCoachScreen.SelectItem },
                        onSettingClick = { currentScreen = VCoachScreen.Settings },
                    )

                    VCoachScreen.SelectItem -> SelectItemScreen(
                        onBackClick = { currentScreen = VCoachScreen.Home },
                        onAddPhotoClick = { currentScreen = VCoachScreen.Detector },
                    )

                    VCoachScreen.Detector -> DetectorScreen(
                        onBackClick = { currentScreen = VCoachScreen.SelectItem },
                    )

                    VCoachScreen.Settings -> SettingsScreen(
                        onBackClick = { currentScreen = VCoachScreen.Home },
                    )
                }
            }
        }
    }
}
