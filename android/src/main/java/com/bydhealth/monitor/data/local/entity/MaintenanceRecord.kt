package com.bydhealth.monitor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance_records")
data class MaintenanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemType: String,              // MaintenanceItemType.name
    val replacedAt: Long,              // 교체 날짜 (epoch ms)
    val mileageAtReplacement: Int,     // 교체 시 주행거리 (km)
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)
