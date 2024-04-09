package com.example.driveohioia.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.driveohioia.ui.screens.DriveScreen
import com.example.driveohioia.ui.screens.HistoryScreen
import com.example.driveohioia.ui.screens.HomeScreen
import com.example.driveohioia.ui.screens.StatisticsScreen
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Navigation(navController: NavHostController){
    // Initializes the navigation graph and the navigation host
    // Each composable call is a node.
    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
        composable(route = Screen.HomeScreen.route){
            HomeScreen()
        }
        composable(route = Screen.StatisticsScreen.route){
            StatisticsScreen()
        }
        composable(route = Screen.DriveScreen.route){
            // Passes an unchanging value to the Drive Screen so a location is presented
            val EiffelTower = LatLng(48.8584,2.2945)
            DriveScreen(currentPosition = EiffelTower,
                cameraState = rememberCameraPositionState() {
                    position = CameraPosition.fromLatLngZoom(EiffelTower,10f)
                }
            )
        }
        composable(Screen.HistoryScreen.route){
            HistoryScreen()
        }



    }
}