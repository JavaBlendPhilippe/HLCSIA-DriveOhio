package com.example.driveohioia.repositories

import com.example.driveohioia.db.Drive
import com.example.driveohioia.db.DriveDAO
import javax.inject.Inject

class MainRepository @Inject constructor(
    val driveDAO: DriveDAO

){ // Collects Data from all the data sources,
    // if we needed another api, would also get api data here
    suspend fun insertDrive(drive: Drive) = driveDAO.insertDrive(drive)

    suspend fun deleteDrive(drive: Drive) = driveDAO.deleteDrive(drive)

    fun getAllDrivesSortedByDate() = driveDAO.getAllDrivesSortedByDate()

    fun getAllDrivesSortedByDistance() = driveDAO.getAllDrivesSortedByDistance()

    fun getAllDrivesSortedByTimeInSecs() = driveDAO.getAllDrivesSortedByTimeInSecs()

    fun getAllDrivesSortedByAvgSpeed() = driveDAO.getAllDrivesSortedByAvgSpeed()

    fun getAllDrivesSortedBySafetyScore() = driveDAO.getAllDrivesSortedBySafetyScore()

    fun getTotalAvgSpeed() = driveDAO.getTotalAvgSpeed()

    fun getTotalSafetyScore() = driveDAO.getTotalSafetyScore()

    fun getTotalDistance() = driveDAO.getTotalDistance()

    fun getTotalTimeInSecs() = driveDAO.getTotalTimeInSecs()

}