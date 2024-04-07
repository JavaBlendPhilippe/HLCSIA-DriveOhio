package com.example.driveohioia.location

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationCollectionUseCase @Inject constructor(
    private val locationService: LocationServiceInterface
) {
    @RequiresApi(Build.VERSION_CODES.S)
    operator fun invoke(): Flow<List<LatLng?>> = locationService.requestAllLocations()
}