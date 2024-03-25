package com.example.driveohioia.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
// DAO = Data Access Object, describes all actions we want to be able to perform with database
interface DriveDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrive(drive: Drive)

    @Delete
    suspend fun deleteDrive(drive: Drive)

    @Query("SELECT * FROM driving_table ORDER BY timestamp DESC")
    fun getAllDrivesSortedByDate(): LiveData<List<Drive>>

    @Query("SELECT * FROM driving_table ORDER BY timeInSecs DESC")
    fun getAllDrivesSortedByTimeInSecs(): LiveData<List<Drive>>

    @Query("SELECT * FROM driving_table ORDER BY safetyScore DESC")
    fun getAllDrivesSortedBySafetyScore(): LiveData<List<Drive>>

    @Query("SELECT * FROM driving_table ORDER BY avgSpeedInMPH DESC")
    fun getAllDrivesSortedByAvgSpeed(): LiveData<List<Drive>>

    @Query("SELECT * FROM driving_table ORDER BY distanceInMiles DESC")
    fun getAllDrivesSortedByDistance(): LiveData<List<Drive>>

    @Query("SELECT SUM(timeInSecs) From driving_table")
    fun getTotalTimeInSecs(): LiveData<Long>

    @Query("SELECT SUM(safetyScore) From driving_table")
    fun getTotalSafetyScore(): LiveData<Float>

    @Query("SELECT SUM(distanceInMiles) From driving_table")
    fun getTotalDistance(): LiveData<Long>

    @Query("SELECT AVG(avgSpeedInMPH) From driving_table")
    fun getTotalAvgSpeed(): LiveData<Float>

}