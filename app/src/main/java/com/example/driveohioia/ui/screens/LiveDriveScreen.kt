package com.example.driveohioia.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.glance.action.actionStartActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.driveohioia.location.ForegroundTrackingService
import com.example.driveohioia.location.ViewState
import com.example.driveohioia.other.Constants.ACTION_STOP_SERVICE
import com.example.driveohioia.ui.activities.DriveActivity
import com.example.driveohioia.ui.activities.MainActivity
import com.example.driveohioia.ui.viewmodels.DriveViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import timber.log.Timber


/*
    NOTE: This Screen and the DriveScreen are very similar, but this screen is only shown inside
    the DriveActivity, and is recomposed by that activity to reflect the current location.
 */
@SuppressLint("TimberArgCount")
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun LiveDriveScreen(currentPosition: LatLng, cameraState: CameraPositionState){
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            Timber.d("XOXO - Live Drive Screen Function Called")
            val (startButton) = createRefs()
            val (timeValue) = createRefs()
            val (googleMap) = createRefs()
            val bottomGuideline = createGuidelineFromBottom(100.dp)

            // Placeholder Text. Where the stopwatch would/will Go
            /* TODO: Stopwatch Integration */
            Text(
                "PH:PH:PH", // Will replace with some string resource
                Modifier.constrainAs(timeValue) {
                    bottom.linkTo(bottomGuideline,margin = 25.dp)
                    centerHorizontallyTo(parent)
                },
                color = MaterialTheme.colorScheme.primary,
                fontSize = 32.sp

            )

            // Button that stops the service. Stop Button on Screen
            Button(
                onClick = {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent) // Starts the mainActivity

                    Intent(context, ForegroundTrackingService::class.java).apply{
                        action = ACTION_STOP_SERVICE
                        context.stopService(this) //Stops the tracking service
                    }
                },
                modifier = Modifier.constrainAs(startButton) {
                    centerHorizontallyTo(parent)
                    top.linkTo(bottomGuideline)
                }

            ) { Text("Stop") }

            var mapProperties by remember {
                mutableStateOf(
                    MapProperties(isMyLocationEnabled = true)
                )
            }
            var mapUiSettings by remember {
                mutableStateOf(
                    MapUiSettings(mapToolbarEnabled = true, myLocationButtonEnabled = true)
                )
            }

            /*
            * Sets the marker location to current position which is passed in when screen recomposes
            */
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

