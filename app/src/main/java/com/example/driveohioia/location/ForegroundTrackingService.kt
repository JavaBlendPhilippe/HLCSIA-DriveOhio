package com.example.driveohioia.location

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import com.example.driveohioia.R
import com.example.driveohioia.other.Constants.ACTION_SHOW_LIVE_DRIVE_SCREEN
import com.example.driveohioia.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.driveohioia.other.Constants.ACTION_STOP_SERVICE
import com.example.driveohioia.other.Constants.NOTIFICATION_CHANNEL_ID
import com.example.driveohioia.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.driveohioia.other.Constants.NOTIFICATION_ID
import com.example.driveohioia.ui.activities.DriveActivity
import com.example.driveohioia.ui.activities.MainActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
/*
    Tracks the users location in the background of the app, shows it in a notification.
 */
class ForegroundTrackingService: LifecycleService() {

    var isFirstRun = true

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationServiceInterface: LocationServiceInterface

    override fun onCreate() {
        super.onCreate()
        // passes the API client into the service
        locationServiceInterface = LocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    // When a command that starts service is called. NOTE: Not necessarily starts the service.
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("XOXO - On Start Command Reached")
        intent?.let{
            when(it.action){
                ACTION_START_OR_RESUME_SERVICE ->{
                    Timber.d("XOXO - Action is Start_Or_Resume")
                    if(isFirstRun){
                        Timber.d("XOXO - Is first run == true; Attempting to start foreground service")
                        startForegroundService()
                        isFirstRun = false
                    } else{
                        Timber.d("XOXO - Resuming Service")
                    }
                }
                ACTION_STOP_SERVICE -> stop()
            }
        }
        return super.onStartCommand(intent, flags, startId)

    }

    // Actions taken when the foreground service starts
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun startForegroundService(){
        Timber.d("XOXO - Starting Service")

        // Allows notifications to be sent
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        // Channel the notifications are sent through
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        // Notification Attributes
        val notification = NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Driving App")
            .setContentText("Elapsed Time: 00:00:00 \n Location:null")
            .setContentIntent(getDriveActivityPendingIntent())

        // Requests the changing location values from the location client
        locationServiceInterface
            .requestLocationUpdates()
            .onEach {location ->
                val lat = location?.latitude.toString()
                val lng = location?.longitude.toString()
                Timber.d("XOXO - New Location $lat, $lng")
                // Updates notification with the values
                val updatedNotification = notification.setContentText(
                    "Elapsed Time: 00:00:00 " +
                            "\n Location:($lat,$lng)"
                )
                notificationManager.notify(5,updatedNotification.build())

            }
            .launchIn(serviceScope)

        ServiceCompat.startForeground(this,5,notification.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun stop(){
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun getDriveActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this,DriveActivity::class.java).also{
            it.action = ACTION_SHOW_LIVE_DRIVE_SCREEN
        }, FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }





}