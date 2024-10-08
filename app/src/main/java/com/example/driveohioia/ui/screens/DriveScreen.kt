package com.example.driveohioia.ui.screens

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.driveohioia.location.ForegroundTrackingService
import com.example.driveohioia.ui.activities.DriveActivity
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import timber.log.Timber

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun DriveScreen(currentPosition: LatLng, cameraState: CameraPositionState) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (startButton) = createRefs()
            val (timeValue) = createRefs()
            val (googleMap) = createRefs()
            val bottomGuideline = createGuidelineFromBottom(150.dp)

            Text(
                "PH:PH:PH", // Will replace with some string resource
                Modifier.constrainAs(timeValue) {
                    bottom.linkTo(bottomGuideline,margin = 25.dp)
                    centerHorizontallyTo(parent)
                },
                color = MaterialTheme.colorScheme.primary,
                fontSize = 32.sp

            )

            Button(
                onClick = {
                    Timber.d("XOXO - DriveScreen Button Clicked")
                    // Launches Drive Activity
                    val activityIntent = Intent(context,DriveActivity::class.java)
                    context.startActivity(activityIntent)

                    // Launches Tracking Service
                    val serviceIntent = Intent(context,ForegroundTrackingService::class.java)
                    context.startForegroundService(serviceIntent)
                },
                modifier = Modifier.constrainAs(startButton) {
                    centerHorizontallyTo(parent)
                    top.linkTo(bottomGuideline)
                }

            ) { Text("Start") }

            var mapProperties by remember {
                mutableStateOf(
                    MapProperties(isMyLocationEnabled = true )
                )
            }
            var mapUiSettings by remember {
                mutableStateOf(
                    MapUiSettings(mapToolbarEnabled = false)
                )
            }

            // Current position doesn't change because it doesn't recompose and receive updates
            // Position is set to Dublin, Ohio
            val marker = LatLng(currentPosition.latitude,currentPosition.longitude)
            Timber.d("XOXO - Current LatLng:%s", marker.toString())
            GoogleMap(
                modifier = Modifier.constrainAs(googleMap){
                    bottom.linkTo(bottomGuideline, margin = 100.dp)
                },
                properties = mapProperties,
                uiSettings = mapUiSettings,
                cameraPositionState = cameraState
            ) {
                Marker(
                    state = MarkerState(position = marker),
                    title = "MyPosition",
                    snippet = "This is a description of this marker",
                    draggable = true
                )
            }

        }
    }

}



@Preview
@Composable
fun TrackingPreview(){
    MaterialTheme{

    }
}

