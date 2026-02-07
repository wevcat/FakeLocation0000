package com.fakelocation.app.domain.usecase

import com.fakelocation.app.domain.model.LocationPoint
import com.fakelocation.app.location.LocationSpoofingManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Use case for managing location spoofing state
 */
class SpoofLocationUseCase(
    private val locationManager: LocationSpoofingManager
) {
    private val _spoofingState = MutableStateFlow<SpoofingUiState>(SpoofingUiState.Inactive)
    val spoofingState: StateFlow<SpoofingUiState> = _spoofingState.asStateFlow()

    private var currentLocation: LocationPoint? = null

    fun startSpoofing(location: LocationPoint): Result<Unit> {
        return try {
            val androidLocation = locationManager.createLocation(
                latitude = location.latitude,
                longitude = location.longitude,
                accuracy = location.accuracy
            )
            locationManager.setMockLocation(androidLocation)
            currentLocation = location
            _spoofingState.value = SpoofingUiState.Active(location)
            Result.success(Unit)
        } catch (e: SecurityException) {
            _spoofingState.value = SpoofingUiState.Error(e.message ?: "Security exception")
            Result.failure(e)
        } catch (e: Exception) {
            _spoofingState.value = SpoofingUiState.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }

    fun stopSpoofing() {
        locationManager.stopMockMode()
        currentLocation = null
        _spoofingState.value = SpoofingUiState.Inactive
    }

    fun updateLocation(location: LocationPoint) {
        if (_spoofingState.value is SpoofingUiState.Active) {
            startSpoofing(location)
        }
    }

    fun isMockLocationEnabled(): Boolean {
        return locationManager.isMockLocationEnabled()
    }

    fun hasMockLocationPermission(): Boolean {
        return locationManager.hasMockLocationPermission()
    }
}

sealed class SpoofingUiState {
    data object Inactive : SpoofingUiState()
    data class Active(val location: LocationPoint) : SpoofingUiState()
    data class Error(val message: String) : SpoofingUiState()
}
