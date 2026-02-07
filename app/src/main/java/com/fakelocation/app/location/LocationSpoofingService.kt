package com.fakelocation.app.location

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.fakelocation.app.FakeLocationApp
import com.fakelocation.app.MainActivity
import com.fakelocation.app.R

/**
 * Foreground service that handles location spoofing
 * Keeps running even when app is in background
 */
class LocationSpoofingService : Service() {

    companion object {
        const val ACTION_START = "com.fakelocation.app.START_SPOOFING"
        const val ACTION_STOP = "com.fakelocation.app.STOP_SPOOFING"
        const val EXTRA_LATITUDE = "latitude"
        const val EXTRA_LONGITUDE = "longitude"
        const val EXTRA_ACCURACY = "accuracy"

        const val NOTIFICATION_ID = 1001
    }

    private lateinit var spoofingManager: com.fakelocation.app.location.LocationSpoofingManager

    override fun onCreate() {
        super.onCreate()
        spoofingManager = com.fakelocation.app.location.LocationSpoofingManager(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val latitude = intent.getDoubleExtra(EXTRA_LATITUDE, 0.0)
                val longitude = intent.getDoubleExtra(EXTRA_LONGITUDE, 0.0)
                val accuracy = intent.getFloatExtra(EXTRA_ACCURACY, 10f)

                startForegroundService()
                startSpoofing(latitude, longitude, accuracy)
            }
            ACTION_STOP -> {
                stopSpoofing()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, LocationSpoofingService::class.java).apply {
            action = ACTION_STOP
        }

        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, FakeLocationApp.LOCATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.service_notification_title))
            .setContentText(getString(R.string.service_notification_text))
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                getString(R.string.stop_spoofing),
                stopPendingIntent
            )
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun startSpoofing(latitude: Double, longitude: Double, accuracy: Float) {
        try {
            val location = spoofingManager.createLocation(
                latitude = latitude,
                longitude = longitude,
                accuracy = accuracy
            )
            spoofingManager.setMockLocation(location)
        } catch (e: Exception) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun stopSpoofing() {
        try {
            spoofingManager.stopMockMode()
        } catch (e: Exception) {
        }
    }
}
