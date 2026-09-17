package com.bydhealth.monitor.data.local

import com.bydhealth.monitor.data.local.dao.MaintenanceDao
import com.bydhealth.monitor.data.local.entity.MaintenanceRecord
import com.bydhealth.monitor.domain.maintenance.MaintenanceItemType
import com.bydhealth.monitor.domain.maintenance.MaintenanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MaintenanceRepositoryImpl @Inject constructor(
    private val dao: MaintenanceDao,
) : MaintenanceRepository {

    override fun observeAll(): Flow<List<MaintenanceRecord>> = dao.observeAll()

    override fun observeLatest(type: MaintenanceItemType): Flow<MaintenanceRecord?> =
        dao.observeLatest(type.name)

    override fun observeByType(type: MaintenanceItemType): Flow<List<MaintenanceRecord>> =
        dao.observeByType(type.name)

    override suspend fun insert(record: MaintenanceRecord) { dao.insert(record) }

    override suspend fun delete(record: MaintenanceRecord) { dao.delete(record) }
}
