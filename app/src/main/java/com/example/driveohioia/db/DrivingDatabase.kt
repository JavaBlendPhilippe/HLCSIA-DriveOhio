package com.example.driveohioia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Drive::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class DrivingDatabase: RoomDatabase() {

    abstract fun getDriveDao(): DriveDAO

}