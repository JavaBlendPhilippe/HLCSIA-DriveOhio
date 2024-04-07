package com.example.driveohioia.ui.navigation
// Routes for screens inside the navGraph
sealed class Screen(val route: String) {
    object HomeScreen: Screen("home")
    object StatisticsScreen: Screen("statistics")
    object HistoryScreen: Screen("history")
    object DriveScreen: Screen("newDrive")
}