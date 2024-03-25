package com.example.driveohioia.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "driving_table")
// Drive object, constructor is stuff that goes into the database
data class Drive(
    var img: Bitmap? = null,
    var timestamp: Long = 0L, // When Drive was
    var avgSpeedInMPH: Float = 0f,
    var distanceInMiles: Float = 0f,
    var timeInSecs: Long = 0L, // How Long Drive was
    var safetyScore: Float = 0f
) {
    @PrimaryKey(autoGenerate = true) //automatically generates keys/locations in database
    var id: Int? = null
}