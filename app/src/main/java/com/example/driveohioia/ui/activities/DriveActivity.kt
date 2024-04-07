package com.example.driveohioia.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.driveohioia.location.ForegroundTrackingService
import com.example.driveohioia.location.PermissionEvent
import com.example.driveohioia.location.ViewState
import com.example.driveohioia.location.hasLocationPermission
import com.example.driveohioia.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.driveohioia.ui.screens.LiveDriveScreen
import com.example.driveohioia.ui.theme.AppTheme
import com.example.driveohioia.ui.viewmodels.DriveViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/*

    Activity that manages data from live location tracking, and presents it to user.
    App checks permissions twice for redundancy, especially since this process is held in a
    seperate activity from where permissions are requested

    Some commented code is from code that was in progress and is still being developed, and I don't
    want that progress to be lost but I dont want it to impact the current performance

 */
@AndroidEntryPoint
class DriveActivity: ComponentActivity() {

    //private var pathPoints = mutableListOf<LatLng>()

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    @OptIn(ExperimentalPermissionsApi::class)
    // When Activity is created on app startup
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        Timber.d("XOXO - Drive Activity Created")
    }

    @SuppressLint("TimberArgCount")
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @OptIn(ExperimentalPermissionsApi::class)
    // Activity is started
    override fun onStart() {
        super.onStart()
        Timber.d("XOXO - Drive Activity Started")
        val locationViewModel: DriveViewModel by viewModels() // Intializes View Model
        setContent{
            // View State, which contains updating information to present to user
            val viewState by locationViewModel.viewState.collectAsStateWithLifecycle()
            AppTheme{
                val permissionState = rememberMultiplePermissionsState(
                    // Ensures that system Remembers that permissions were granted
                    permissions = listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    LaunchedEffect(!hasLocationPermission()){
                        permissionState.launchMultiplePermissionRequest()
                    }

                    // How app handles permissions.
                    when{
                        permissionState.allPermissionsGranted -> {
                            LaunchedEffect(Unit) {
                                locationViewModel.handle(PermissionEvent.Granted)
                            }
                        }

                        permissionState.shouldShowRationale -> {
                            RationaleAlert(onDismiss = { }){
                                permissionState.launchMultiplePermissionRequest()
                            }
                        }

                        !permissionState.allPermissionsGranted && permissionState.shouldShowRationale -> {
                            LaunchedEffect(Unit) {
                                locationViewModel.handle(PermissionEvent.Revoked)
                            }
                        }
                    }
                }
            }
            // Action the activity takes depending on the view state
            with(viewState){
                when(this) {
                    ViewState.Loading -> {
                        Timber.d("XOXO - ViewState is Loading")
                        Box(
                            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ){
                            CircularProgressIndicator()
                        }
                    }
                    // Composable tells to go to settings to grant permissions
                    //
                    ViewState.RevokedPermissions -> {
                        Timber.d("XOXO - ViewState is Permission Revoked")
                        Column(
                            modifier = androidx.compose.ui.Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                        ) {
                            Text("Permissions Needed to use this feature. Please grant them in Settings.")
                            Button(
                                onClick = {
                                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                                },
                                enabled = !hasLocationPermission()
                            ){
                                if(hasLocationPermission()) CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                else Text("Settings")
                            }
                        }
                    }
                    // Major activity, when permissions are granted
                    // ViewState.Success has the location object, whoch is accessed here
                    is ViewState.Success -> {
                        //var polylinePoints = mutableListOf<LatLng>()
                        Timber.d("XOXO - ViewState is Success")
                        //Timber.d("XOXO - polylinePoints Contents:",polylinePoints.toString())
                        val currentLoc =
                            LatLng(
                                location?.latitude ?: 0.0,
                                location?.longitude ?:0.0
                                /*
                                    Location is nullable, "?:" provides a value of 0.0 if the value
                                    present is null
                                 */
                            )
                        val cameraState = rememberCameraPositionState()
                        LaunchedEffect(key1 = currentLoc){
                            cameraState.centerOnLocation(currentLoc)
                        }
                        // Sets the position to be passed to screen as the values obtained from state
                        var position = LatLng(
                            currentLoc.latitude,
                            currentLoc.longitude
                        )
                        //polylinePoints.add(position)
                       // Timber.d("XOXO - polylinePoints Contents post add:",polylinePoints.toString())

                        Timber.d("XOXO - position value passed into current position:%s", position.toString())
                        // Calls and recomposes the LiveDriveScreen
                        LiveDriveScreen(
                            currentPosition = position,
                            cameraState = cameraState,
                        )
                        // Resumes the tracking service to request a new location
                        sendCommandToService(ACTION_START_OR_RESUME_SERVICE)


                    }
                }
            }
        }
    }

    private fun sendCommandToService(action: String) =
        Intent(this,ForegroundTrackingService::class.java).also{
            it.action = action
            this.startService(it)
        }

}

// Permission request composable
@Composable
fun RationaleAlert(onDismiss: () -> Unit, onConfirm: () -> Unit){
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties()
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentSize(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ){
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "We need location permissions to use this app"
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(onClick = {
                    onConfirm()
                    onDismiss()
                }, modifier = Modifier.align(Alignment.End)
                ) {
                    Text("OK")
                }
            }
        }
    }
}

// Changes the position that the camera is centered on the the camera's current position
private suspend fun CameraPositionState.centerOnLocation(
    location: LatLng
) = animate(
    update = CameraUpdateFactory.newLatLngZoom(
        location,
        12f
    ),
    durationMs = 1500
)
