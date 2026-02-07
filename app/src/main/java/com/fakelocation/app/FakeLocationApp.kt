package com.fakelocation.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class FakeLocationApp : Application() {

    companion object {
        const val LOCATION_CHANNEL_ID = "location_spoofing_channel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                LOCATION_CHANNEL_ID,
                "Location Spoofing",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notification channel for location spoofing service"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
