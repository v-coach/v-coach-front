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
import com.example.vcoach.ui.selectitem.SelectItemScreen
import com.example.vcoach.ui.settings.SettingsScreen
import com.example.vcoach.ui.theme.VCoachTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VCoachTheme {
                var currentScreen by remember { mutableStateOf("home") }

                when (currentScreen) {
                    "home" -> HomeScreen(
                        onAnalyzeClick = { currentScreen = "select_item" },
                        onSettingClick = { currentScreen = "settings" },
                    )

                    "select_item" -> SelectItemScreen(
                        onBackClick = { currentScreen = "home" },
                        onAddPhotoClick = { currentScreen = "detector" },
                    )

                    "detector" -> DetectorScreen(
                        onBackClick = { currentScreen = "select_item" },
                    )

                    "settings" -> SettingsScreen(
                        onBackClick = { currentScreen = "home" },
                    )
                }
            }
        }
    }
}
