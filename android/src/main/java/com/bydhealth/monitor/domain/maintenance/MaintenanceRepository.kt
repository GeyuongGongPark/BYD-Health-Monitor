package com.bydhealth.monitor.domain.maintenance

import com.bydhealth.monitor.data.local.entity.MaintenanceRecord
import kotlinx.coroutines.flow.Flow

interface MaintenanceRepository {
    fun observeAll(): Flow<List<MaintenanceRecord>>
    fun observeLatest(type: MaintenanceItemType): Flow<MaintenanceRecord?>
    fun observeByType(type: MaintenanceItemType): Flow<List<MaintenanceRecord>>
    suspend fun insert(record: MaintenanceRecord)
    suspend fun delete(record: MaintenanceRecord)
}
