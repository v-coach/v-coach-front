package com.example.vcoach.ui.navigation

sealed interface VCoachScreen {
    data object Home : VCoachScreen
    data object SelectItem : VCoachScreen
    data object Detector : VCoachScreen
    data object Settings : VCoachScreen
}
