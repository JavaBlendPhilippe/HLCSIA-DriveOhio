package com.example.driveohioia.location

import com.google.android.gms.maps.model.LatLng
/*
The states the drive activity can be in and the values that entails
 */
sealed interface ViewState {
    object Loading: ViewState
    data class Success(val location: LatLng?) : ViewState // Current location value
    object RevokedPermissions: ViewState
}