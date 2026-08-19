package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AlertStatus
import com.example.data.model.DriverProfile
import com.example.data.model.EmergencyAlert
import com.example.data.model.GpsWaypoint
import com.example.data.model.PriorityLevel
import com.example.data.model.ScheduledTrip
import com.example.data.model.ShiftStatus
import com.example.data.model.TripStatus
import com.example.data.repository.AmbulanceRepository
import com.example.location.GpsLocationData
import com.example.location.GpsLocationManager
import com.example.location.HospitalPOI
import com.example.sync.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab {
    DISPATCH_DASHBOARD,
    GPS_MAP,
    SCHEDULED_TRIPS,
    DRIVER_PROFILE
}

data class AuthState(
    val isLoggedIn: Boolean = false, // Starts on login/onboarding screen as requested
    val driverNameInput: String = "Capt. Marcus Vance, NRP",
    val badgeInput: String = "MED-9042",
    val licenseInput: String = "CA-EMS-994120",
    val phoneInput: String = "+1 (555) 392-8811",
    val unitInput: String = "MEDIC-402 (MICU)",
    val stationInput: String = "Station 12 - Metro Trauma Hub",
    val vehiclePlateInput: String = "CA-EMS-402",
    val pinInput: String = "1042",
    val loginError: String? = null,
    val isVerifying: Boolean = false
)

class AmbulanceDriverViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    val networkMonitor = NetworkMonitor(application)
    val repository = AmbulanceRepository(application, database, networkMonitor)
    val gpsManager = GpsLocationManager(application)

    // Navigation & Auth State
    private val _selectedTab = MutableStateFlow(AppNavTab.DISPATCH_DASHBOARD)
    val selectedTab: StateFlow<AppNavTab> = _selectedTab.asStateFlow()

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Emergency Alert Focus / Active Dialog
    private val _selectedAlertForDetails = MutableStateFlow<EmergencyAlert?>(null)
    val selectedAlertForDetails: StateFlow<EmergencyAlert?> = _selectedAlertForDetails.asStateFlow()

    private val _showProfileEditDialog = MutableStateFlow(false)
    val showProfileEditDialog: StateFlow<Boolean> = _showProfileEditDialog.asStateFlow()

    private val _showCompanyFleetPanel = MutableStateFlow(false)
    val showCompanyFleetPanel: StateFlow<Boolean> = _showCompanyFleetPanel.asStateFlow()

    private val _emergencyBannerDismissed = MutableStateFlow(false)
    val emergencyBannerDismissed: StateFlow<Boolean> = _emergencyBannerDismissed.asStateFlow()

    // Data streams from repository
    val allAlerts: StateFlow<List<EmergencyAlert>> = repository.allAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeEmergencyAlert: StateFlow<EmergencyAlert?> = repository.activeEmergencyAlert
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val scheduledTrips: StateFlow<List<ScheduledTrip>> = repository.allScheduledTrips
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val driverProfile: StateFlow<DriverProfile?> = repository.driverProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val pendingSyncCount: StateFlow<Int> = repository.pendingSyncCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
    val isSimulatedOffline: StateFlow<Boolean> = networkMonitor.isSimulatedOffline
    val isSyncing: StateFlow<Boolean> = repository.isSyncing
    val lastSyncTimestamp: StateFlow<Long> = repository.lastSyncTimestamp
    val locationData: StateFlow<GpsLocationData> = gpsManager.locationData
    val nearbyHospitals: List<HospitalPOI> = gpsManager.nearbyHospitals

    // Unaccepted critical alerts count
    val unacceptedCriticalAlertsCount: StateFlow<Int> = repository.allAlerts
        .combine(repository.activeEmergencyAlert) { alerts, _ ->
            alerts.count { it.status == AlertStatus.PENDING && it.priorityLevel == PriorityLevel.CODE_RED_CRITICAL }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Navigation Tab Selection
    fun selectTab(tab: AppNavTab) {
        _selectedTab.value = tab
    }

    // Login & Driver/Ambulance Onboarding Actions
    fun onDriverNameChanged(name: String) {
        _authState.value = _authState.value.copy(driverNameInput = name, loginError = null)
    }

    fun onBadgeChanged(badge: String) {
        _authState.value = _authState.value.copy(badgeInput = badge, loginError = null)
    }

    fun onLicenseChanged(license: String) {
        _authState.value = _authState.value.copy(licenseInput = license, loginError = null)
    }

    fun onPhoneChanged(phone: String) {
        _authState.value = _authState.value.copy(phoneInput = phone, loginError = null)
    }

    fun onUnitChanged(unit: String) {
        _authState.value = _authState.value.copy(unitInput = unit, loginError = null)
    }

    fun onStationChanged(station: String) {
        _authState.value = _authState.value.copy(stationInput = station, loginError = null)
    }

    fun onVehiclePlateChanged(plate: String) {
        _authState.value = _authState.value.copy(vehiclePlateInput = plate, loginError = null)
    }

    fun onPinChanged(pin: String) {
        _authState.value = _authState.value.copy(pinInput = pin, loginError = null)
    }

    fun login() {
        val state = _authState.value
        // Validation checks
        if (state.driverNameInput.isBlank()) {
            _authState.value = state.copy(loginError = "Please enter Paramedic / Driver Full Name")
            return
        }
        if (state.badgeInput.isBlank() || state.badgeInput.length < 3) {
            _authState.value = state.copy(loginError = "Please enter a valid Paramedic Badge ID (e.g. MED-9042)")
            return
        }
        if (state.licenseInput.isBlank()) {
            _authState.value = state.copy(loginError = "Please enter State EMS / Paramedic License number")
            return
        }
        if (state.unitInput.isBlank()) {
            _authState.value = state.copy(loginError = "Please enter Assigned Ambulance Unit Call Sign")
            return
        }
        if (state.stationInput.isBlank()) {
            _authState.value = state.copy(loginError = "Please enter Station / Trauma Base Hub")
            return
        }
        if (state.pinInput.length < 4) {
            _authState.value = state.copy(loginError = "Security PIN must be at least 4 digits")
            return
        }

        viewModelScope.launch {
            _authState.value = state.copy(isVerifying = true, loginError = null)
            val current = driverProfile.value ?: DriverProfile()
            val updated = current.copy(
                driverName = state.driverNameInput.trim(),
                badgeNumber = state.badgeInput.trim().uppercase(),
                unitId = state.unitInput.trim(),
                stationName = state.stationInput.trim(),
                paramedicLicense = state.licenseInput.trim(),
                phone = state.phoneInput.trim(),
                shiftStatus = ShiftStatus.ON_DUTY_AVAILABLE,
                shiftStartTime = System.currentTimeMillis()
            )
            repository.updateDriverProfile(updated)
            _authState.value = state.copy(isLoggedIn = true, isVerifying = false, loginError = null)
        }
    }

    fun quickLoginDemo(
        driverName: String,
        badge: String,
        license: String,
        phone: String,
        unit: String,
        station: String,
        plate: String
    ) {
        _authState.value = AuthState(
            isLoggedIn = true,
            driverNameInput = driverName,
            badgeInput = badge,
            licenseInput = license,
            phoneInput = phone,
            unitInput = unit,
            stationInput = station,
            vehiclePlateInput = plate,
            pinInput = "1042"
        )
        viewModelScope.launch {
            val current = driverProfile.value ?: DriverProfile()
            repository.updateDriverProfile(
                current.copy(
                    driverName = driverName,
                    badgeNumber = badge,
                    unitId = unit,
                    stationName = station,
                    paramedicLicense = license,
                    phone = phone,
                    shiftStatus = ShiftStatus.ON_DUTY_AVAILABLE,
                    shiftStartTime = System.currentTimeMillis()
                )
            )
        }
    }

    fun logout() {
        _authState.value = _authState.value.copy(isLoggedIn = false)
        viewModelScope.launch {
            driverProfile.value?.let {
                repository.updateDriverProfile(it.copy(shiftStatus = ShiftStatus.OFF_DUTY))
            }
        }
    }

    // Emergency Alert Actions
    fun acceptEmergencyAlert(alert: EmergencyAlert) {
        viewModelScope.launch {
            repository.acceptEmergencyAlert(alert.id)
            gpsManager.setEmergencyTarget(
                alert.destinationHospital,
                alert.hospitalLat,
                alert.hospitalLng
            )
            // Auto switch to GPS navigation map for immediate route guidance
            _selectedTab.value = AppNavTab.GPS_MAP
        }
    }

    fun advanceAlertStep(alertId: String, currentStatus: AlertStatus) {
        val nextStatus = when (currentStatus) {
            AlertStatus.PENDING -> AlertStatus.ACCEPTED
            AlertStatus.ACCEPTED -> AlertStatus.EN_ROUTE
            AlertStatus.EN_ROUTE -> AlertStatus.ON_SCENE
            AlertStatus.ON_SCENE -> AlertStatus.PATIENT_LOADED
            AlertStatus.PATIENT_LOADED -> AlertStatus.TRANSPORTING
            AlertStatus.TRANSPORTING -> AlertStatus.HANDOVER_COMPLETE
            AlertStatus.HANDOVER_COMPLETE -> AlertStatus.HANDOVER_COMPLETE
            AlertStatus.DECLINED -> AlertStatus.DECLINED
        }
        viewModelScope.launch {
            repository.advanceAlertStatus(alertId, nextStatus)
            if (nextStatus == AlertStatus.HANDOVER_COMPLETE) {
                gpsManager.clearTarget()
            }
        }
    }

    fun declineAlert(alertId: String, reason: String) {
        viewModelScope.launch {
            repository.declineAlert(alertId, reason)
            _selectedAlertForDetails.value = null
        }
    }

    fun selectAlertForDetails(alert: EmergencyAlert?) {
        _selectedAlertForDetails.value = alert
    }

    // Scheduled Trip Actions
    fun startScheduledTrip(trip: ScheduledTrip) {
        viewModelScope.launch {
            // Check if emergency is active
            val activeEmergency = activeEmergencyAlert.value
            if (activeEmergency != null && activeEmergency.status != AlertStatus.HANDOVER_COMPLETE) {
                // Emergency takes precedence!
                return@launch
            }
            repository.updateTripStatus(trip.id, TripStatus.EN_ROUTE_PICKUP)
            gpsManager.setEmergencyTarget(
                trip.pickupAddress,
                trip.pickupLat,
                trip.pickupLng
            )
            _selectedTab.value = AppNavTab.GPS_MAP
        }
    }

    fun advanceTripStatus(trip: ScheduledTrip) {
        val nextStatus = when (trip.status) {
            TripStatus.SCHEDULED -> TripStatus.EN_ROUTE_PICKUP
            TripStatus.PREPARING -> TripStatus.EN_ROUTE_PICKUP
            TripStatus.EN_ROUTE_PICKUP -> TripStatus.PATIENT_ABOARD
            TripStatus.PATIENT_ABOARD -> TripStatus.COMPLETED
            TripStatus.PAUSED_FOR_EMERGENCY -> TripStatus.EN_ROUTE_PICKUP
            TripStatus.COMPLETED -> TripStatus.COMPLETED
            TripStatus.CANCELLED -> TripStatus.CANCELLED
        }
        viewModelScope.launch {
            repository.updateTripStatus(trip.id, nextStatus)
            if (nextStatus == TripStatus.COMPLETED) {
                gpsManager.clearTarget()
            }
        }
    }

    // GPS & Navigation Actions
    fun toggleEmergencySiren() {
        gpsManager.toggleSiren()
        viewModelScope.launch {
            val loc = locationData.value
            repository.recordGpsWaypoint(
                GpsWaypoint(
                    latitude = loc.latitude,
                    longitude = loc.longitude,
                    speedKmh = loc.speedKmh,
                    heading = loc.heading,
                    isSirenActive = loc.isEmergencySirenOn
                )
            )
        }
    }

    fun routeToHospital(hospital: HospitalPOI) {
        gpsManager.setEmergencyTarget(
            hospital.name,
            hospital.latitude,
            hospital.longitude
        )
    }

    // Profile & Telemetry Actions
    fun openProfileEditor() {
        _showProfileEditDialog.value = true
    }

    fun closeProfileEditor() {
        _showProfileEditDialog.value = false
    }

    fun updateProfile(
        name: String,
        badge: String,
        unit: String,
        station: String,
        license: String,
        certifications: String,
        phone: String
    ) {
        val current = driverProfile.value ?: DriverProfile()
        viewModelScope.launch {
            repository.updateDriverProfile(
                current.copy(
                    driverName = name,
                    badgeNumber = badge,
                    unitId = unit,
                    stationName = station,
                    paramedicLicense = license,
                    certifications = certifications,
                    phone = phone
                )
            )
            _showProfileEditDialog.value = false
        }
    }

    fun toggleShiftDutyStatus() {
        val current = driverProfile.value ?: return
        val newStatus = when (current.shiftStatus) {
            ShiftStatus.ON_DUTY_AVAILABLE -> ShiftStatus.STANDBY_STATION
            ShiftStatus.STANDBY_STATION -> ShiftStatus.ON_DUTY_AVAILABLE
            ShiftStatus.DISPATCHED_EMERGENCY -> ShiftStatus.ON_DUTY_AVAILABLE
            ShiftStatus.ON_SCHEDULED_RUN -> ShiftStatus.ON_DUTY_AVAILABLE
            ShiftStatus.OFF_DUTY -> ShiftStatus.ON_DUTY_AVAILABLE
        }
        viewModelScope.launch {
            repository.updateDriverProfile(current.copy(shiftStatus = newStatus))
        }
    }

    fun openCompanyFleetPanel() {
        _showCompanyFleetPanel.value = true
    }

    fun closeCompanyFleetPanel() {
        _showCompanyFleetPanel.value = false
    }

    fun updateAmbulanceHealthScore(
        score: Int,
        grade: String,
        status: String,
        enginePercent: Int,
        brakesPercent: Int,
        batteryPercent: Int,
        sanitizationPercent: Int,
        inspectorName: String,
        fleetNotes: String
    ) {
        viewModelScope.launch {
            repository.updateAmbulanceHealthScoreFromCompanyPanel(
                healthScore = score,
                grade = grade,
                status = status,
                enginePercent = enginePercent,
                brakesPercent = brakesPercent,
                batteryPercent = batteryPercent,
                sanitizationPercent = sanitizationPercent,
                inspectorName = inspectorName,
                fleetNotes = fleetNotes
            )
            _showCompanyFleetPanel.value = false
        }
    }

    fun simulateBackendRatingAdjustment(isImprovement: Boolean) {
        viewModelScope.launch {
            repository.simulateBackendRatingAdjustment(isImprovement)
        }
    }

    // Offline & Sync Controls
    fun toggleSimulatedOffline() {
        networkMonitor.toggleSimulatedOffline()
    }

    fun forceSyncNow() {
        viewModelScope.launch {
            repository.syncPendingItems()
        }
    }

    // Trigger test emergency incoming dispatch
    fun triggerSimulatedEmergencyDispatch() {
        viewModelScope.launch {
            val scenarios = listOf(
                Triple("CODE RED 911", "Severe Respiratory Arrest & Anaphylaxis", "44F, Angioedema, Epinephrine administered"),
                Triple("TRAUMA RESCUE", "Pedestrian Struck by Vehicle", "29M, Unconscious, Open pelvic fracture"),
                Triple("CARDIAC ALERT", "Refractory Ventricular Tachycardia", "67M, Pulse present, unstable vitals")
            )
            val selected = scenarios.random()
            repository.triggerIncomingEmergencySimulation(
                code = selected.first,
                title = selected.second,
                condition = selected.third,
                priority = PriorityLevel.CODE_RED_CRITICAL,
                address = "520 Columbus Ave, North Beach District",
                hospital = "St. Jude Metro Level 1 Trauma Center"
            )
        }
    }
}
