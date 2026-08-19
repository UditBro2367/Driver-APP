package com.example.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class GpsLocationData(
    val latitude: Double = 37.7749,
    val longitude: Double = -122.4194,
    val speedKmh: Float = 0f,
    val heading: Float = 0f,
    val altitudeMeters: Double = 32.0,
    val accuracyMeters: Float = 4.5f,
    val isEmergencySirenOn: Boolean = false,
    val streetName: String = "Market St & 4th Ave, Dispatch Zone 1",
    val activeNavTargetName: String? = null,
    val distanceRemainingKm: Double = 0.0,
    val etaMinutesRemaining: Int = 0,
    val currentNavInstruction: String = "Maintain heading on Market St"
)

data class HospitalPOI(
    val name: String,
    val traumaLevel: String, // "Level 1 Trauma", "Level 2 Trauma", "Cardiac & Stroke Center"
    val latitude: Double,
    val longitude: Double,
    val availableBeds: Int,
    val icuCapacityPercent: Int,
    val specialty: String
)

class GpsLocationManager(private val context: Context) {
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val _locationData = MutableStateFlow(GpsLocationData())
    val locationData: StateFlow<GpsLocationData> = _locationData.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var simulationJob: Job? = null

    val nearbyHospitals = listOf(
        HospitalPOI(
            name = "St. Jude Metro Level 1 Trauma Center",
            traumaLevel = "Level 1 Comprehensive Trauma",
            latitude = 37.7885,
            longitude = -122.4075,
            availableBeds = 14,
            icuCapacityPercent = 78,
            specialty = "24/7 Surgical Resuscitation • Cath Lab • Burn Unit"
        ),
        HospitalPOI(
            name = "Memorial University General Hospital",
            traumaLevel = "Level 2 Regional Trauma & Stroke",
            latitude = 37.7650,
            longitude = -122.4330,
            availableBeds = 22,
            icuCapacityPercent = 64,
            specialty = "Comprehensive Stroke Center • Pediatric ED"
        ),
        HospitalPOI(
            name = "St. Mary's Heart & Vascular Institute",
            traumaLevel = "Cardiac Emergency & PCI Center",
            latitude = 37.7712,
            longitude = -122.4520,
            availableBeds = 8,
            icuCapacityPercent = 85,
            specialty = "STEMI Rapid Revascularization • ECMO Ready"
        ),
        HospitalPOI(
            name = "Westside Community Healthcare Hospital",
            traumaLevel = "Level 3 Community Emergency",
            latitude = 37.7520,
            longitude = -122.4110,
            availableBeds = 31,
            icuCapacityPercent = 45,
            specialty = "General Emergency • Urgent Care Triage"
        )
    )

    private val simulatedWaypoints = listOf(
        Triple(37.7749, -122.4194, "Market St & 4th Ave"),
        Triple(37.7770, -122.4160, "Market St & 3rd St"),
        Triple(37.7802, -122.4120, "Mission St Expressway"),
        Triple(37.7845, -122.4095, "Approach to Hospital Emergency Bay"),
        Triple(37.7885, -122.4075, "St. Jude Trauma Resuscitation Bay")
    )

    private var currentWaypointIndex = 0

    init {
        startLocationTracking()
    }

    @SuppressLint("MissingPermission")
    fun startLocationTracking() {
        try {
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (isGpsEnabled || isNetworkEnabled) {
                val provider = if (isGpsEnabled) LocationManager.GPS_PROVIDER else LocationManager.NETWORK_PROVIDER
                locationManager.requestLocationUpdates(
                    provider,
                    2000L,
                    5f,
                    object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            if (simulationJob == null || !simulationJob!!.isActive) {
                                updateFromRealLocation(location)
                            }
                        }
                        @Deprecated("Deprecated in Java")
                        override fun onStatusChanged(p: String?, s: Int, e: Bundle?) {}
                        override fun onProviderEnabled(p: String) {}
                        override fun onProviderDisabled(p: String) {}
                    }
                )
            }
        } catch (_: SecurityException) {
            // Fallback to active dispatch simulation
        }

        // Start realistic simulation loop for navigation fidelity
        startNavigationSimulation()
    }

    private fun updateFromRealLocation(location: Location) {
        val speedKmh = location.speed * 3.6f
        _locationData.value = _locationData.value.copy(
            latitude = location.latitude,
            longitude = location.longitude,
            speedKmh = if (speedKmh > 0) speedKmh else _locationData.value.speedKmh,
            heading = location.bearing,
            altitudeMeters = location.altitude,
            accuracyMeters = location.accuracy
        )
    }

    fun toggleSiren() {
        val currentSiren = _locationData.value.isEmergencySirenOn
        val newSiren = !currentSiren
        val newSpeed = if (newSiren) 68.5f else 35.0f
        _locationData.value = _locationData.value.copy(
            isEmergencySirenOn = newSiren,
            speedKmh = newSpeed
        )
    }

    fun setEmergencyTarget(targetName: String, targetLat: Double, targetLng: Double) {
        val current = _locationData.value
        val distance = calculateDistanceKm(current.latitude, current.longitude, targetLat, targetLng)
        val speed = if (current.isEmergencySirenOn) 70.0 else 45.0
        val eta = ((distance / speed) * 60).toInt().coerceAtLeast(1)

        _locationData.value = current.copy(
            activeNavTargetName = targetName,
            distanceRemainingKm = (distance * 10).toInt() / 10.0,
            etaMinutesRemaining = eta,
            currentNavInstruction = "Proceed with priority to $targetName (Lights & Siren Active)"
        )
    }

    fun clearTarget() {
        _locationData.value = _locationData.value.copy(
            activeNavTargetName = null,
            distanceRemainingKm = 0.0,
            etaMinutesRemaining = 0,
            currentNavInstruction = "Patrol / Station Standby"
        )
    }

    private fun startNavigationSimulation() {
        simulationJob?.cancel()
        simulationJob = coroutineScope.launch {
            while (isActive) {
                delay(3000L)
                val current = _locationData.value
                val isSiren = current.isEmergencySirenOn
                val speed = if (isSiren) (62..82).random().toFloat() else (28..45).random().toFloat()
                
                // Advance waypoint
                currentWaypointIndex = (currentWaypointIndex + 1) % simulatedWaypoints.size
                val nextWp = simulatedWaypoints[currentWaypointIndex]

                val heading = calculateBearing(
                    current.latitude, current.longitude,
                    nextWp.first, nextWp.second
                )

                val remainingDist = if (current.activeNavTargetName != null) {
                    (current.distanceRemainingKm - 0.2).coerceAtLeast(0.1)
                } else {
                    0.0
                }

                val eta = if (remainingDist > 0) ((remainingDist / speed) * 60).toInt().coerceAtLeast(1) else 0

                val instructions = when (currentWaypointIndex) {
                    0 -> "Turn right on 4th Ave in 200m"
                    1 -> "Emergency priority: Straight across intersection"
                    2 -> "Continue on Expressway toward Hospital Bay"
                    3 -> "Prepare for left turn into Ambulance Entrance"
                    else -> "Arriving at destination bay. Prepare stretcher."
                }

                _locationData.value = current.copy(
                    latitude = nextWp.first,
                    longitude = nextWp.second,
                    speedKmh = speed,
                    heading = heading.toFloat(),
                    streetName = nextWp.third,
                    distanceRemainingKm = (remainingDist * 10).toInt() / 10.0,
                    etaMinutesRemaining = eta,
                    currentNavInstruction = if (current.activeNavTargetName != null) instructions else "Standby in Zone 1"
                )
            }
        }
    }

    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    private fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val deltaLambda = Math.toRadians(lon2 - lon1)
        val y = sin(deltaLambda) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)
        val bearing = Math.toDegrees(atan2(y, x))
        return (bearing + 360) % 360
    }
}
