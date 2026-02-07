package com.fakelocation.app.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationSpoofingManager(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest: LocationRequest by lazy {
        LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000L
        ).apply {
            setMinUpdateIntervalMillis(500L)
            setWaitForAccurateLocation(false)
        }.build()
    }

    fun hasMockLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                context.checkSelfPermission("android.permission.MOCK_LOCATION") == PackageManager.PERMISSION_GRANTED
            } catch (e: Exception) {
                false
            }
        } else {
            context.packageManager.checkPermission(
                "android.permission.MOCK_LOCATION",
                context.packageName
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun isMockLocationEnabled(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                try {
                    locationManager.getProvider("mock")
                    true
                } catch (e: Exception) {
                    false
                }
            } else {
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) {
            throw SecurityException("Location permission not granted")
        }

        return suspendCancellableCoroutine { continuation ->
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        continuation.resume(location)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            } catch (e: SecurityException) {
                continuation.resumeWithException(e)
            }
        }
    }

    fun setMockLocation(location: Location): Boolean {
        if (!hasMockLocationPermission()) {
            throw SecurityException("Mock location permission not granted")
        }

        if (!isMockLocationEnabled()) {
            throw SecurityException("Mock location is not enabled in Developer Options")
        }

        return try {
            fusedLocationClient.setMockMode(true)
            fusedLocationClient.setMockLocation(location)
            true
        } catch (e: SecurityException) {
            throw SecurityException("Failed to set mock location: ${e.message}")
        } catch (e: Exception) {
            throw RuntimeException("Failed to set mock location: ${e.message}")
        }
    }

    fun stopMockMode() {
        try {
            fusedLocationClient.setMockMode(false)
        } catch (e: Exception) {
        }
    }

    fun observeLocationUpdates(): Flow<Location> = callbackFlow {
        if (!hasLocationPermission()) {
            close(SecurityException("Location permission not granted"))
            return@callbackFlow
        }

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(location)
                }
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                callback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            close(e)
        }

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun createLocation(
        latitude: Double,
        longitude: Double,
        accuracy: Float = 10f,
        altitude: Double = 0.0,
        bearing: Float = 0f,
        speed: Float = 0f
    ): Location {
        return Location(LocationManager.FUSED_PROVIDER).apply {
            this.latitude = latitude
            this.longitude = longitude
            this.accuracy = accuracy
            this.altitude = altitude
            this.bearing = bearing
            this.speed = speed
            time = System.currentTimeMillis()
            elapsedRealtimeNanos = System.nanoTime()
        }
    }
}
