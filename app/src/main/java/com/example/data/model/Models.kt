package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PriorityLevel {
    CODE_RED_CRITICAL,
    CODE_AMBER_URGENT,
    CODE_YELLOW_MEDIUM,
    NON_EMERGENCY
}

enum class AlertStatus {
    PENDING,
    ACCEPTED,
    EN_ROUTE,
    ON_SCENE,
    PATIENT_LOADED,
    TRANSPORTING,
    HANDOVER_COMPLETE,
    DECLINED
}

enum class TripStatus {
    SCHEDULED,
    PREPARING,
    EN_ROUTE_PICKUP,
    PATIENT_ABOARD,
    COMPLETED,
    CANCELLED,
    PAUSED_FOR_EMERGENCY
}

enum class ShiftStatus {
    ON_DUTY_AVAILABLE,
    DISPATCHED_EMERGENCY,
    ON_SCHEDULED_RUN,
    STANDBY_STATION,
    OFF_DUTY
}

enum class SyncStatus {
    SYNCED,
    PENDING_SYNC,
    SYNC_FAILED
}

@Entity(tableName = "emergency_alerts")
data class EmergencyAlert(
    @PrimaryKey val id: String,
    val incidentCode: String, // e.g. "CR-904"
    val title: String, // e.g. "Acute Myocardial Infarction / Cardiac Arrest"
    val priorityLevel: PriorityLevel,
    val patientCondition: String, // e.g. "Unconscious, CPR in progress by bystander"
    val patientAgeGender: String, // e.g. "58M"
    val callerNotes: String, // e.g. "Gate code #4492, 2nd floor apt 4B"
    val locationAddress: String, // e.g. "742 Evergreen Terrace, Sector 4"
    val latitude: Double,
    val longitude: Double,
    val destinationHospital: String, // e.g. "St. Jude Level 1 Trauma Center"
    val hospitalLat: Double,
    val hospitalLng: Double,
    val distanceKm: Double,
    val etaMinutes: Int,
    val status: AlertStatus = AlertStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

@Entity(tableName = "scheduled_trips")
data class ScheduledTrip(
    @PrimaryKey val id: String,
    val tripNumber: String, // e.g. "SCH-2041"
    val patientName: String,
    val patientAgeGender: String,
    val transportReason: String, // e.g. "Scheduled Hemodialysis Transport"
    val pickupAddress: String,
    val pickupLat: Double,
    val pickupLng: Double,
    val dropoffHospital: String,
    val dropoffLat: Double,
    val dropoffLng: Double,
    val scheduledTimeFormatted: String, // e.g. "14:30 Today"
    val scheduledTimestamp: Long,
    val equipmentRequired: String, // e.g. "Stretcher, Continuous O2 (4L/min)"
    val specialPrecautions: String, // e.g. "Fall risk, wheelchair ramp required"
    val status: TripStatus = TripStatus.SCHEDULED,
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

@Entity(tableName = "driver_profile")
data class DriverProfile(
    @PrimaryKey val id: String = "primary_driver",
    val driverName: String = "Captain Alex Mercer, NRP",
    val badgeNumber: String = "MED-8842",
    val unitId: String = "RESCUE-07",
    val stationName: String = "Station 14 - Metro Central EMS",
    val paramedicLicense: String = "NRP-CA-992104-A",
    val certifications: String = "ACLS • PHTLS • BLS-I • EVOC Master",
    val phone: String = "+1 (555) 019-2834",
    val emergencyContact: String = "Dispatch Command #9 / (555) 911-0000",
    val shiftStartTime: Long = System.currentTimeMillis() - (3 * 3600 * 1000), // 3 hours ago
    val shiftStatus: ShiftStatus = ShiftStatus.ON_DUTY_AVAILABLE,
    val totalRunsToday: Int = 5,
    val avgResponseMinutes: Double = 4.2,
    val o2TankPsi: Int = 1850, // Normal ~2000 PSI
    val fuelPercent: Int = 88,
    val vehicleMileage: Int = 42890,
    // Driver Backend Rating & Evaluation
    val driverRating: Double = 4.92,
    val ratingDelta: Double = 0.14,
    val isRatingImproved: Boolean = true, // true = Improved, false = Deproved / Decreased
    val totalReviewsCount: Int = 184,
    val ratingCategorySafety: Int = 99, // Safe Driving & EVOC %
    val ratingCategoryPunctuality: Int = 98, // Rapid Response Time %
    val ratingCategoryPatientCare: Int = 97, // Patient Care & Bedside %
    val backendRatingSyncMessage: String = "Backend CAD evaluation: Commended for rapid STEMI Code-Red dispatch & zero-infraction EVOC driving.",
    // Ambulance Vehicle Health Score (Company Fleet Managed)
    val vehicleHealthScore: Int = 96, // 0 to 100
    val vehicleHealthGrade: String = "A+ (Excellent Fleet Condition)",
    val fleetInspectionStatus: String = "Certified Active Roadworthy",
    val engineHealthPercent: Int = 98,
    val brakesHealthPercent: Int = 94,
    val batteryInverterHealthPercent: Int = 97,
    val medicalSanitizationPercent: Int = 99,
    val lastFleetInspector: String = "Fleet Inspector Tech-402 (Admin Panel)",
    val lastInspectionDate: String = "Today, 06:00 AM",
    val companyFleetNotes: String = "All bio-medical auxiliary systems and EVOC drivetrain cleared for high-speed emergency response.",
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

@Entity(tableName = "gps_waypoints")
data class GpsWaypoint(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double,
    val longitude: Double,
    val speedKmh: Float,
    val heading: Float,
    val isSirenActive: Boolean,
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

@Entity(tableName = "sync_queue")
data class SyncQueueItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entityType: String, // "ALERT_UPDATE", "TRIP_STATUS", "GPS_BREADCRUMB", "PROFILE", "FLEET_HEALTH"
    val entityId: String,
    val action: String, // "UPDATE", "INSERT"
    val payloadJson: String,
    val createdAt: Long = System.currentTimeMillis(),
    val status: SyncStatus = SyncStatus.PENDING_SYNC,
    val retryCount: Int = 0
)
