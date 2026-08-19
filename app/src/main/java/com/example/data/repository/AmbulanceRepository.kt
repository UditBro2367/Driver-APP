package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.model.AlertStatus
import com.example.data.model.DriverProfile
import com.example.data.model.EmergencyAlert
import com.example.data.model.GpsWaypoint
import com.example.data.model.PriorityLevel
import com.example.data.model.ScheduledTrip
import com.example.data.model.ShiftStatus
import com.example.data.model.SyncQueueItem
import com.example.data.model.SyncStatus
import com.example.data.model.TripStatus
import com.example.sync.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AmbulanceRepository(
    private val context: Context,
    private val database: AppDatabase,
    val networkMonitor: NetworkMonitor
) {
    private val alertDao = database.emergencyAlertDao()
    private val tripDao = database.scheduledTripDao()
    private val profileDao = database.driverProfileDao()
    private val gpsDao = database.gpsWaypointDao()
    private val syncDao = database.syncQueueDao()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncTimestamp = MutableStateFlow(System.currentTimeMillis())
    val lastSyncTimestamp: StateFlow<Long> = _lastSyncTimestamp.asStateFlow()

    // Public reactive flows
    val allAlerts: Flow<List<EmergencyAlert>> = alertDao.getAllAlerts()
    val activeEmergencyAlert: Flow<EmergencyAlert?> = alertDao.getActiveEmergencyAlert()
    val allScheduledTrips: Flow<List<ScheduledTrip>> = tripDao.getAllTrips()
    val driverProfile: Flow<DriverProfile?> = profileDao.getProfile()
    val pendingSyncCount: Flow<Int> = syncDao.getPendingCount()

    init {
        coroutineScope.launch {
            seedInitialDataIfNeeded()
            observeNetworkAndAutoSync()
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        val existingProfile = profileDao.getProfile().firstOrNull()
        if (existingProfile == null) {
            profileDao.insertOrUpdate(
                DriverProfile(
                    id = "primary_driver",
                    driverName = "Capt. Marcus Vance, NRP",
                    badgeNumber = "MED-9042",
                    unitId = "MEDIC-402 (MICU)",
                    stationName = "Station 12 - Metro Trauma Hub",
                    paramedicLicense = "CA-EMS-994120",
                    certifications = "ACLS • PHTLS • BLS-Instructor • EVOC-IV",
                    phone = "+1 (555) 392-8811",
                    emergencyContact = "Central Dispatch (555) 911-3000",
                    shiftStartTime = System.currentTimeMillis() - (4 * 3600 * 1000),
                    shiftStatus = ShiftStatus.ON_DUTY_AVAILABLE,
                    totalRunsToday = 6,
                    avgResponseMinutes = 3.8,
                    o2TankPsi = 1920,
                    fuelPercent = 92,
                    vehicleMileage = 54120,
                    syncStatus = SyncStatus.SYNCED
                )
            )
        }

        val existingAlerts = alertDao.getAllAlerts().firstOrNull()
        if (existingAlerts.isNullOrEmpty()) {
            val seedAlerts = listOf(
                EmergencyAlert(
                    id = "ALERT-001",
                    incidentCode = "CODE RED 911",
                    title = "Acute Cardiac Arrest / STEMI Triage",
                    priorityLevel = PriorityLevel.CODE_RED_CRITICAL,
                    patientCondition = "58 y/o male, bystander CPR, AED deployed",
                    patientAgeGender = "58M",
                    callerNotes = "Security gate 3341, lobby concierge will hold high-speed elevator",
                    locationAddress = "742 Market St, Financial District, Tower 3",
                    latitude = 37.7890,
                    longitude = -122.4015,
                    destinationHospital = "St. Jude Metro Level 1 Trauma Center",
                    hospitalLat = 37.7885,
                    hospitalLng = -122.4075,
                    distanceKm = 1.4,
                    etaMinutes = 3,
                    status = AlertStatus.PENDING,
                    timestamp = System.currentTimeMillis() - 60000,
                    syncStatus = SyncStatus.SYNCED
                ),
                EmergencyAlert(
                    id = "ALERT-002",
                    incidentCode = "TRAUMA-1",
                    title = "Multi-Vehicle Collision with Entrapment",
                    priorityLevel = PriorityLevel.CODE_RED_CRITICAL,
                    patientCondition = "Multiple victims, heavy extrication required",
                    patientAgeGender = "Multiple (3)",
                    callerNotes = "Highway patrol on scene, fire dept ladder 4 en route",
                    locationAddress = "Highway 101 Northbound & Exit 428B",
                    latitude = 37.7650,
                    longitude = -122.4100,
                    destinationHospital = "Memorial University General Trauma",
                    hospitalLat = 37.7650,
                    hospitalLng = -122.4330,
                    distanceKm = 3.2,
                    etaMinutes = 6,
                    status = AlertStatus.PENDING,
                    timestamp = System.currentTimeMillis() - 180000,
                    syncStatus = SyncStatus.SYNCED
                )
            )
            alertDao.insertAlerts(seedAlerts)
        }

        val existingTrips = tripDao.getAllTrips().firstOrNull()
        if (existingTrips.isNullOrEmpty()) {
            val seedTrips = listOf(
                ScheduledTrip(
                    id = "TRIP-201",
                    tripNumber = "NEMT-4401",
                    patientName = "Eleanor Vance",
                    patientAgeGender = "74F",
                    transportReason = "Inter-facility ICU Transfer (Specialized Cardiology)",
                    pickupAddress = "Westside Senior Care Facility, Wing B",
                    pickupLat = 37.7550,
                    pickupLng = -122.4250,
                    dropoffHospital = "St. Jude Metro Trauma Center - CCU",
                    dropoffLat = 37.7885,
                    dropoffLng = -122.4075,
                    scheduledTimeFormatted = "13:30 Today",
                    scheduledTimestamp = System.currentTimeMillis() + (45 * 60 * 1000),
                    equipmentRequired = "Cardiac Monitor • IV Infusion Pump • Stretcher",
                    specialPrecautions = "Requires continuous ECG telemetry during transport",
                    status = TripStatus.SCHEDULED,
                    syncStatus = SyncStatus.SYNCED
                ),
                ScheduledTrip(
                    id = "TRIP-202",
                    tripNumber = "NEMT-4402",
                    patientName = "Robert Thorne",
                    patientAgeGender = "61M",
                    transportReason = "Scheduled Hemodialysis Renal Session",
                    pickupAddress = "1208 Pine Valley Rd, Apt 12",
                    pickupLat = 37.7680,
                    pickupLng = -122.4410,
                    dropoffHospital = "Memorial University Renal Dialysis Clinic",
                    dropoffLat = 37.7650,
                    dropoffLng = -122.4330,
                    scheduledTimeFormatted = "15:15 Today",
                    scheduledTimestamp = System.currentTimeMillis() + (120 * 60 * 1000),
                    equipmentRequired = "Wheelchair Transport • 2L Nasal Cannula O2",
                    specialPrecautions = "Left forearm AV fistula - DO NOT use left arm for BP",
                    status = TripStatus.SCHEDULED,
                    syncStatus = SyncStatus.SYNCED
                ),
                ScheduledTrip(
                    id = "TRIP-203",
                    tripNumber = "NEMT-4403",
                    patientName = "Clara Henderson",
                    patientAgeGender = "82F",
                    transportReason = "Post-Surgical Hospital Discharge to Rehabilitation",
                    pickupAddress = "St. Mary's Orthopedic Wing, Room 304",
                    pickupLat = 37.7712,
                    pickupLng = -122.4520,
                    dropoffHospital = "Golden Sunset Skilled Nursing & Rehab",
                    dropoffLat = 37.7420,
                    dropoffLng = -122.4600,
                    scheduledTimeFormatted = "17:00 Today",
                    scheduledTimestamp = System.currentTimeMillis() + (240 * 60 * 1000),
                    equipmentRequired = "Stretcher • Orthopedic Leg Immobilizer",
                    specialPrecautions = "Total hip arthroplasty precautions, gentle transfer",
                    status = TripStatus.SCHEDULED,
                    syncStatus = SyncStatus.SYNCED
                )
            )
            tripDao.insertTrips(seedTrips)
        }
    }

    private suspend fun observeNetworkAndAutoSync() {
        networkMonitor.isOnline.collect { isOnline ->
            if (isOnline) {
                syncPendingItems()
            }
        }
    }

    // Process offline sync queue
    suspend fun syncPendingItems(): Boolean = withContext(Dispatchers.IO) {
        if (!networkMonitor.isOnline.value) return@withContext false
        _isSyncing.value = true
        try {
            val pending = syncDao.getPendingItems().firstOrNull() ?: emptyList()
            if (pending.isNotEmpty()) {
                // Simulate reliable low-latency cloud sync handshake
                delay(800L)
                for (item in pending) {
                    syncDao.markSynced(item.id)
                }
                syncDao.clearSynced()
            }
            _lastSyncTimestamp.value = System.currentTimeMillis()
            return@withContext true
        } catch (_: Exception) {
            return@withContext false
        } finally {
            _isSyncing.value = false
        }
    }

    // Emergency Alert Operations
    suspend fun acceptEmergencyAlert(alertId: String) = withContext(Dispatchers.IO) {
        val isOnline = networkMonitor.isOnline.value
        val syncStatus = if (isOnline) SyncStatus.SYNCED else SyncStatus.PENDING_SYNC

        // Prioritize emergency: pause any running scheduled trips
        val trips = tripDao.getAllTrips().firstOrNull() ?: emptyList()
        for (trip in trips) {
            if (trip.status == TripStatus.EN_ROUTE_PICKUP || trip.status == TripStatus.PATIENT_ABOARD) {
                tripDao.updateTripStatus(trip.id, TripStatus.PAUSED_FOR_EMERGENCY, syncStatus)
            }
        }

        alertDao.updateAlertStatus(alertId, AlertStatus.ACCEPTED, syncStatus)
        profileDao.updateShiftStatus(ShiftStatus.DISPATCHED_EMERGENCY, syncStatus)

        if (!isOnline) {
            syncDao.insert(
                SyncQueueItem(
                    entityType = "ALERT_STATUS",
                    entityId = alertId,
                    action = "ACCEPT_EMERGENCY",
                    payloadJson = "{\"alertId\":\"$alertId\",\"status\":\"ACCEPTED\"}",
                    status = SyncStatus.PENDING_SYNC
                )
            )
        }
    }

    suspend fun advanceAlertStatus(alertId: String, newStatus: AlertStatus) = withContext(Dispatchers.IO) {
        val isOnline = networkMonitor.isOnline.value
        val syncStatus = if (isOnline) SyncStatus.SYNCED else SyncStatus.PENDING_SYNC

        alertDao.updateAlertStatus(alertId, newStatus, syncStatus)

        if (newStatus == AlertStatus.HANDOVER_COMPLETE) {
            profileDao.updateShiftStatus(ShiftStatus.ON_DUTY_AVAILABLE, syncStatus)
        }

        if (!isOnline) {
            syncDao.insert(
                SyncQueueItem(
                    entityType = "ALERT_STATUS",
                    entityId = alertId,
                    action = "ADVANCE_STATUS",
                    payloadJson = "{\"alertId\":\"$alertId\",\"status\":\"${newStatus.name}\"}",
                    status = SyncStatus.PENDING_SYNC
                )
            )
        }
    }

    suspend fun declineAlert(alertId: String, reason: String) = withContext(Dispatchers.IO) {
        val isOnline = networkMonitor.isOnline.value
        val syncStatus = if (isOnline) SyncStatus.SYNCED else SyncStatus.PENDING_SYNC
        alertDao.updateAlertStatus(alertId, AlertStatus.DECLINED, syncStatus)
        profileDao.updateShiftStatus(ShiftStatus.ON_DUTY_AVAILABLE, syncStatus)

        if (!isOnline) {
            syncDao.insert(
                SyncQueueItem(
                    entityType = "ALERT_STATUS",
                    entityId = alertId,
                    action = "DECLINE_EMERGENCY",
                    payloadJson = "{\"alertId\":\"$alertId\",\"reason\":\"$reason\"}",
                    status = SyncStatus.PENDING_SYNC
                )
            )
        }
    }

    // Trigger mock incoming emergency dispatch for simulation and testing
    suspend fun triggerIncomingEmergencySimulation(
        code: String,
        title: String,
        condition: String,
        priority: PriorityLevel,
        address: String,
        hospital: String
    ) = withContext(Dispatchers.IO) {
        val id = "ALERT-${System.currentTimeMillis().toString().takeLast(4)}"
        val alert = EmergencyAlert(
            id = id,
            incidentCode = code,
            title = title,
            priorityLevel = priority,
            patientCondition = condition,
            patientAgeGender = "46F",
            callerNotes = "Paramedics cleared for hot response, lights & sirens authorized",
            locationAddress = address,
            latitude = 37.7820,
            longitude = -122.4180,
            destinationHospital = hospital,
            hospitalLat = 37.7885,
            hospitalLng = -122.4075,
            distanceKm = 1.9,
            etaMinutes = 4,
            status = AlertStatus.PENDING,
            timestamp = System.currentTimeMillis(),
            syncStatus = SyncStatus.SYNCED
        )
        alertDao.insertAlert(alert)
    }

    // Scheduled Trip Operations
    suspend fun updateTripStatus(tripId: String, newStatus: TripStatus) = withContext(Dispatchers.IO) {
        val isOnline = networkMonitor.isOnline.value
        val syncStatus = if (isOnline) SyncStatus.SYNCED else SyncStatus.PENDING_SYNC
        tripDao.updateTripStatus(tripId, newStatus, syncStatus)

        if (newStatus == TripStatus.EN_ROUTE_PICKUP || newStatus == TripStatus.PATIENT_ABOARD) {
            profileDao.updateShiftStatus(ShiftStatus.ON_SCHEDULED_RUN, syncStatus)
        } else if (newStatus == TripStatus.COMPLETED) {
            profileDao.updateShiftStatus(ShiftStatus.ON_DUTY_AVAILABLE, syncStatus)
        }

        if (!isOnline) {
            syncDao.insert(
                SyncQueueItem(
                    entityType = "TRIP_STATUS",
                    entityId = tripId,
                    action = "UPDATE_TRIP",
                    payloadJson = "{\"tripId\":\"$tripId\",\"status\":\"${newStatus.name}\"}",
                    status = SyncStatus.PENDING_SYNC
                )
            )
        }
    }

    // Profile Operations
    suspend fun updateDriverProfile(updated: DriverProfile) = withContext(Dispatchers.IO) {
        val isOnline = networkMonitor.isOnline.value
        val syncStatus = if (isOnline) SyncStatus.SYNCED else SyncStatus.PENDING_SYNC
        val toSave = updated.copy(syncStatus = syncStatus)
        profileDao.insertOrUpdate(toSave)

        if (!isOnline) {
            syncDao.insert(
                SyncQueueItem(
                    entityType = "DRIVER_PROFILE",
                    entityId = updated.id,
                    action = "UPDATE_PROFILE",
                    payloadJson = "{\"driverName\":\"${updated.driverName}\",\"badge\":\"${updated.badgeNumber}\",\"unit\":\"${updated.unitId}\"}",
                    status = SyncStatus.PENDING_SYNC
                )
            )
        }
    }

    // App Company Fleet Panel - Vehicle Health Score Upload & Maintenance Management
    suspend fun updateAmbulanceHealthScoreFromCompanyPanel(
        healthScore: Int,
        grade: String,
        status: String,
        enginePercent: Int,
        brakesPercent: Int,
        batteryPercent: Int,
        sanitizationPercent: Int,
        inspectorName: String,
        fleetNotes: String
    ) = withContext(Dispatchers.IO) {
        val isOnline = networkMonitor.isOnline.value
        val syncStatus = if (isOnline) SyncStatus.SYNCED else SyncStatus.PENDING_SYNC
        val current = profileDao.getProfile().firstOrNull() ?: DriverProfile()
        val updated = current.copy(
            vehicleHealthScore = healthScore,
            vehicleHealthGrade = grade,
            fleetInspectionStatus = status,
            engineHealthPercent = enginePercent,
            brakesHealthPercent = brakesPercent,
            batteryInverterHealthPercent = batteryPercent,
            medicalSanitizationPercent = sanitizationPercent,
            lastFleetInspector = inspectorName,
            lastInspectionDate = "Just Now (Uploaded via Fleet Panel)",
            companyFleetNotes = fleetNotes,
            syncStatus = syncStatus
        )
        profileDao.insertOrUpdate(updated)

        if (!isOnline) {
            syncDao.insert(
                SyncQueueItem(
                    entityType = "FLEET_HEALTH",
                    entityId = current.unitId,
                    action = "UPLOAD_HEALTH_SCORE",
                    payloadJson = "{\"unitId\":\"${current.unitId}\",\"healthScore\":$healthScore,\"status\":\"$status\",\"inspector\":\"$inspectorName\"}",
                    status = SyncStatus.PENDING_SYNC
                )
            )
        }
    }

    // Backend Rating Evaluation Simulator (Improved / Deproved from Backend CAD)
    suspend fun simulateBackendRatingAdjustment(isImprovement: Boolean) = withContext(Dispatchers.IO) {
        val current = profileDao.getProfile().firstOrNull() ?: DriverProfile()
        val isOnline = networkMonitor.isOnline.value
        val syncStatus = if (isOnline) SyncStatus.SYNCED else SyncStatus.PENDING_SYNC

        val newRating: Double
        val delta: Double
        val message: String

        if (isImprovement) {
            newRating = (current.driverRating + 0.08).coerceAtMost(5.00)
            delta = 0.14
            message = "Backend CAD Evaluation: Rating Improved (+0.14) due to optimal Code Red route choice and safe EVOC handling."
        } else {
            newRating = (current.driverRating - 0.12).coerceAtLeast(3.20)
            delta = 0.10
            message = "Backend CAD Evaluation: Rating Deproved (-0.10) flagged due to excessive idle turn-around time at hospital triage bay."
        }

        val updated = current.copy(
            driverRating = String.format("%.2f", newRating).toDoubleOrNull() ?: newRating,
            ratingDelta = delta,
            isRatingImproved = isImprovement,
            totalReviewsCount = current.totalReviewsCount + 1,
            backendRatingSyncMessage = message,
            syncStatus = syncStatus
        )
        profileDao.insertOrUpdate(updated)

        if (!isOnline) {
            syncDao.insert(
                SyncQueueItem(
                    entityType = "DRIVER_RATING",
                    entityId = current.id,
                    action = "SYNC_RATING",
                    payloadJson = "{\"rating\":$newRating,\"improved\":$isImprovement}",
                    status = SyncStatus.PENDING_SYNC
                )
            )
        }
    }

    suspend fun recordGpsWaypoint(waypoint: GpsWaypoint) = withContext(Dispatchers.IO) {
        val isOnline = networkMonitor.isOnline.value
        val syncStatus = if (isOnline) SyncStatus.SYNCED else SyncStatus.PENDING_SYNC
        gpsDao.insert(waypoint.copy(syncStatus = syncStatus))
    }
}
