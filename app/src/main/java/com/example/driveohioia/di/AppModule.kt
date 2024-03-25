package com.example.driveohioia.di

import android.content.Context
import androidx.room.Room
import com.example.driveohioia.db.DrivingDatabase
import com.example.driveohioia.other.Constants.DRIVING_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDrivingDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        DrivingDatabase::class.java,
        DRIVING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideDriveDao(db: DrivingDatabase) = db.getDriveDao()
}