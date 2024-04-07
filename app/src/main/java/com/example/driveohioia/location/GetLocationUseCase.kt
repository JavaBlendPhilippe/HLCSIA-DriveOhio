package com.example.driveohioia.location

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Sets up using the Location service and what the intended outcome is
class GetLocationUseCase @Inject constructor(
    private val locationService: LocationServiceInterface
) {
    @RequiresApi(Build.VERSION_CODES.S)
    operator fun invoke(): Flow<LatLng?> = locationService.requestLocationUpdates()
}