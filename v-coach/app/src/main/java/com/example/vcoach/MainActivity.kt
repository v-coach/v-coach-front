package com.example.vcoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.vcoach.ui.detector.DetectorScreen
import com.example.vcoach.ui.theme.VCoachTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VCoachTheme {
                DetectorScreen()
            }
        }
    }
}
