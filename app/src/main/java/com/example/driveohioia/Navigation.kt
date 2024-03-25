package com.example.driveohioia

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.driveohioia.ui.screens.HomeScreen
import com.example.driveohioia.ui.screens.DriveScreen
import com.example.driveohioia.ui.screens.HistoryScreen
import com.example.driveohioia.ui.screens.StatisticsScreen

@Composable
fun Navigation(navController: NavHostController){
    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
        composable(route = Screen.HomeScreen.route){
            HomeScreen()
        }
        composable(route = Screen.StatisticsScreen.route){
            StatisticsScreen()
        }
        composable(route = Screen.DriveScreen.route){
            DriveScreen()
        }
        composable(Screen.HistoryScreen.route){
            HistoryScreen()
        }
    }
}