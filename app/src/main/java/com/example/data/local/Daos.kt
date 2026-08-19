package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AlertStatus
import com.example.data.model.DriverProfile
import com.example.data.model.EmergencyAlert
import com.example.data.model.GpsWaypoint
import com.example.data.model.ShiftStatus
import com.example.data.model.ScheduledTrip
import com.example.data.model.SyncQueueItem
import com.example.data.model.SyncStatus
import com.example.data.model.TripStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyAlertDao {
    @Query("SELECT * FROM emergency_alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<EmergencyAlert>>

    @Query("SELECT * FROM emergency_alerts WHERE status IN ('PENDING', 'ACCEPTED', 'EN_ROUTE', 'ON_SCENE', 'PATIENT_LOADED', 'TRANSPORTING') ORDER BY CASE priorityLevel WHEN 'CODE_RED_CRITICAL' THEN 1 WHEN 'CODE_AMBER_URGENT' THEN 2 WHEN 'CODE_YELLOW_MEDIUM' THEN 3 ELSE 4 END, timestamp DESC LIMIT 1")
    fun getActiveEmergencyAlert(): Flow<EmergencyAlert?>

    @Query("SELECT * FROM emergency_alerts WHERE id = :id LIMIT 1")
    suspend fun getAlertById(id: String): EmergencyAlert?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(alerts: List<EmergencyAlert>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: EmergencyAlert)

    @Update
    suspend fun updateAlert(alert: EmergencyAlert)

    @Query("UPDATE emergency_alerts SET status = :status, syncStatus = :syncStatus WHERE id = :id")
    suspend fun updateAlertStatus(id: String, status: AlertStatus, syncStatus: SyncStatus)

    @Query("DELETE FROM emergency_alerts WHERE id = :id")
    suspend fun deleteAlert(id: String)
}

@Dao
interface ScheduledTripDao {
    @Query("SELECT * FROM scheduled_trips ORDER BY scheduledTimestamp ASC")
    fun getAllTrips(): Flow<List<ScheduledTrip>>

    @Query("SELECT * FROM scheduled_trips WHERE id = :id LIMIT 1")
    suspend fun getTripById(id: String): ScheduledTrip?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrips(trips: List<ScheduledTrip>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: ScheduledTrip)

    @Update
    suspend fun updateTrip(trip: ScheduledTrip)

    @Query("UPDATE scheduled_trips SET status = :status, syncStatus = :syncStatus WHERE id = :id")
    suspend fun updateTripStatus(id: String, status: TripStatus, syncStatus: SyncStatus)

    @Query("DELETE FROM scheduled_trips WHERE id = :id")
    suspend fun deleteTrip(id: String)
}

@Dao
interface DriverProfileDao {
    @Query("SELECT * FROM driver_profile WHERE id = 'primary_driver' LIMIT 1")
    fun getProfile(): Flow<DriverProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: DriverProfile)

    @Query("UPDATE driver_profile SET shiftStatus = :status, syncStatus = :syncStatus WHERE id = 'primary_driver'")
    suspend fun updateShiftStatus(status: ShiftStatus, syncStatus: SyncStatus)

    @Query("UPDATE driver_profile SET o2TankPsi = :o2, fuelPercent = :fuel, syncStatus = :syncStatus WHERE id = 'primary_driver'")
    suspend fun updateVehicleTelemetry(o2: Int, fuel: Int, syncStatus: SyncStatus)
}

@Dao
interface GpsWaypointDao {
    @Query("SELECT * FROM gps_waypoints ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentWaypoints(limit: Int): Flow<List<GpsWaypoint>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(waypoint: GpsWaypoint)

    @Query("SELECT * FROM gps_waypoints WHERE syncStatus = 'PENDING_SYNC'")
    suspend fun getPendingWaypoints(): List<GpsWaypoint>

    @Query("UPDATE gps_waypoints SET syncStatus = 'SYNCED' WHERE id IN (:ids)")
    suspend fun markWaypointsSynced(ids: List<Long>)
}

@Dao
interface SyncQueueDao {
    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING_SYNC' ORDER BY createdAt ASC")
    fun getPendingItems(): Flow<List<SyncQueueItem>>

    @Query("SELECT COUNT(*) FROM sync_queue WHERE status = 'PENDING_SYNC'")
    fun getPendingCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SyncQueueItem): Long

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE sync_queue SET status = 'SYNCED' WHERE id = :id")
    suspend fun markSynced(id: Long)

    @Query("DELETE FROM sync_queue WHERE status = 'SYNCED'")
    suspend fun clearSynced()
}
