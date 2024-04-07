package com.example.driveohioia.location

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

// Initializes the functions that will be built out in ForegroundTrackingService to be linked to API
interface LocationServiceInterface {
    fun requestLocationUpdates(): Flow<LatLng?>
    /*
        Isn't used yet to send data to screen yet, but will be used to send a collection of all the
        LatLng objects to work with.
     */
    fun requestAllLocations(): Flow<List<LatLng?>>
}