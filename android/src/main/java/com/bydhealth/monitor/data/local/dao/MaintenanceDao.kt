package com.bydhealth.monitor.data.local.dao

import androidx.room.*
import com.bydhealth.monitor.data.local.entity.MaintenanceRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceDao {

    @Query("SELECT * FROM maintenance_records ORDER BY replacedAt DESC")
    fun observeAll(): Flow<List<MaintenanceRecord>>

    @Query("SELECT * FROM maintenance_records WHERE itemType = :type ORDER BY replacedAt DESC LIMIT 1")
    fun observeLatest(type: String): Flow<MaintenanceRecord?>

    @Query("SELECT * FROM maintenance_records WHERE itemType = :type ORDER BY replacedAt DESC")
    fun observeByType(type: String): Flow<List<MaintenanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: MaintenanceRecord): Long

    @Delete
    suspend fun delete(record: MaintenanceRecord)
}
