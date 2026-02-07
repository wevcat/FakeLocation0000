package com.fakelocation.app.ui.screens

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fakelocation.app.domain.model.LocationPoint
import com.fakelocation.app.domain.usecase.SpoofLocationUseCase
import com.fakelocation.app.domain.usecase.SpoofingUiState
import com.fakelocation.app.location.LocationSpoofingManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val locationManager = LocationSpoofingManager(application.applicationContext)
    private val spoofLocationUseCase = SpoofLocationUseCase(locationManager)

    val spoofingState: StateFlow<SpoofingUiState> = spoofLocationUseCase.spoofingState

    private val _hasPermissions = mutableStateOf(false)
    val hasPermissions: State<Boolean> = _hasPermissions

    private val _isMockLocationEnabled = mutableStateOf(false)
    val isMockLocationEnabled: State<Boolean> = _isMockLocationEnabled

    private val _isApi30Plus = mutableStateOf(false)
    val isApi30Plus: State<Boolean> = _isApi30Plus

    init {
        _isApi30Plus.value = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

    fun startSpoofing(location: LocationPoint) {
        viewModelScope.launch {
            // Android 11+ has system-level restrictions on mock location
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                return@launch
            }

            val result = spoofLocationUseCase.startSpoofing(location)
            if (result.isFailure) {
                _isMockLocationEnabled.value = false
            }
        }
    }

    fun stopSpoofing() {
        spoofLocationUseCase.stopSpoofing()
    }

    fun updateLocation(location: LocationPoint) {
        spoofLocationUseCase.updateLocation(location)
    }

    fun checkPermissions() {
        val permissions = getRequiredPermissions()
        val allGranted = permissions.all { permission ->
            getApplication<Application>().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }
        _hasPermissions.value = allGranted
    }

    fun updatePermissionState(granted: Boolean) {
        _hasPermissions.value = granted
    }

    fun checkMockLocationEnabled() {
        _isMockLocationEnabled.value = spoofLocationUseCase.isMockLocationEnabled()
    }

    fun getAndroidVersionInfo(): String {
        return Build.VERSION.RELEASE
    }

    fun getApiLevel(): Int {
        return Build.VERSION.SDK_INT
    }

    private fun getRequiredPermissions(): Array<String> {
        return arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}
