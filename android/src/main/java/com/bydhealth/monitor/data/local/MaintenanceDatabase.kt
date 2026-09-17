package com.bydhealth.monitor.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.bydhealth.monitor.data.local.dao.MaintenanceDao
import com.bydhealth.monitor.data.local.entity.MaintenanceRecord

@Database(entities = [MaintenanceRecord::class], version = 1, exportSchema = false)
abstract class MaintenanceDatabase : RoomDatabase() {
    abstract fun maintenanceDao(): MaintenanceDao

    companion object {
        const val DB_NAME = "byd_health_monitor.db"

        fun create(context: Context): MaintenanceDatabase =
            Room.databaseBuilder(context, MaintenanceDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }
}
