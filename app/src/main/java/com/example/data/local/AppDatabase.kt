package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.DriverProfile
import com.example.data.model.EmergencyAlert
import com.example.data.model.GpsWaypoint
import com.example.data.model.ScheduledTrip
import com.example.data.model.SyncQueueItem

@Database(
    entities = [
        EmergencyAlert::class,
        ScheduledTrip::class,
        DriverProfile::class,
        GpsWaypoint::class,
        SyncQueueItem::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun emergencyAlertDao(): EmergencyAlertDao
    abstract fun scheduledTripDao(): ScheduledTripDao
    abstract fun driverProfileDao(): DriverProfileDao
    abstract fun gpsWaypointDao(): GpsWaypointDao
    abstract fun syncQueueDao(): SyncQueueDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ambulance_driver_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
