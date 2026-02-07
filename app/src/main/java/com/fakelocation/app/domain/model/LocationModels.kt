package com.fakelocation.app.domain.model

/**
 * Represents a geographic location with coordinates
 */
data class LocationPoint(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float = 10f,
    val name: String = ""
) {
    init {
        require(latitude in -90.0..90.0) { "Latitude must be between -90 and 90" }
        require(longitude in -180.0..180.0) { "Longitude must be between -180 and 180" }
        require(accuracy > 0) { "Accuracy must be positive" }
    }

    override fun toString(): String {
        return if (name.isNotEmpty()) {
            "$name ($latitude, $longitude)"
        } else {
            "$latitude, $longitude"
        }
    }
}

/**
 * Spoofing state for UI representation
 */
sealed class SpoofingState {
    data object Inactive : SpoofingState()
    data class Active(val location: LocationPoint) : SpoofingState()
    data class Error(val message: String) : SpoofingState()
}
