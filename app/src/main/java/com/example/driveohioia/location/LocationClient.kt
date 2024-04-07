package com.example.driveohioia.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.compose.ui.util.fastForEach
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject


/*
    This is the service that retrieves the users current location.
    It has to be retrieved as latitude and longitudinal values and update constantly
    This is because the googleMaps composable only takes latitude and longitude values
 */

class LocationClient @Inject constructor(
    private val context: Context,
    private val locationProvider: FusedLocationProviderClient //API: requests most recent location
): LocationServiceInterface {

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    // Obtains last known position on user's device
    //Returns a flow of LatLng objects, periodically provided objects of the same type
    override fun requestLocationUpdates(): Flow<LatLng?> = callbackFlow{
        if(!context.hasLocationPermission()){
            trySend(null)
            return@callbackFlow
        }

        // Encapsulated Parameters for requesting through FusedLocationProviderClient
        val request = LocationRequest.Builder(10000L)
            .setIntervalMillis(5000L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        // The latest location provided by the client
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult){
                /*
                    Callback actually provides all the locations by default, but we only send
                    the latest location into the channel to be used
                 */
                locationResult.locations.lastOrNull()?.let{
                    trySend(LatLng(it.latitude,it.longitude))
                }
            }
        }

        // Actually requests the location from the API
        locationProvider.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )

        // When provider is closed, clears the location updates that were provided
        awaitClose {
            locationProvider.removeLocationUpdates(locationCallback)
        }
    }

    // Obtains all of the positions that have been tracked
    //Returns a flow of a list of LatLng objects, periodically provided objects of the same type
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun requestAllLocations(): Flow<List<LatLng?>> = callbackFlow {

        if(!context.hasLocationPermission()){
            trySend(emptyList())
            return@callbackFlow
        }

        val request = LocationRequest.Builder(10000L)
            .setIntervalMillis(5000L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        // The latest location provided by the client, isn't used and doesn't really work well
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult){
                locationResult.locations.let{ it ->
                    /*
                        Creates a list of LatLng? objects. In the for each, has to turn each object
                        into a LatLng from a Location object. Likely will take too much time
                        as time scales with use
                     */
                    val locationList = mutableListOf<LatLng?>()
                    it.forEach {
                        locationList.add(LatLng(it.latitude,it.longitude))
                    }
                    trySend(locationList.toList())
                }
            }
        }

        locationProvider.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )

        awaitClose {
            locationProvider.removeLocationUpdates(locationCallback)
        }
    }
}

// Checks location permissions are granted
fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

}