package com.example.driveohioia

sealed class Screen(val route: String) {
    object HomeScreen: Screen("home")
    object StatisticsScreen: Screen("statistics")
    object HistoryScreen: Screen("history")
    object DriveScreen: Screen("newDrive")

}