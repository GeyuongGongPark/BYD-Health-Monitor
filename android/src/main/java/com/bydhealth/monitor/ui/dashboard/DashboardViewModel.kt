package com.bydhealth.monitor.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bydhealth.monitor.data.local.MaintenanceRepositoryImpl
import com.bydhealth.monitor.data.vehicle.MalfunctionRepository
import com.bydhealth.monitor.data.vehicle.StatisticRepository
import com.bydhealth.monitor.data.vehicle.TyreRepository
import com.bydhealth.monitor.domain.maintenance.MaintenanceItemStatus
import com.bydhealth.monitor.domain.maintenance.MaintenanceItemType
import com.bydhealth.monitor.domain.maintenance.MaintenanceStatus
import com.bydhealth.monitor.domain.malfunction.MalfunctionCatalog
import com.bydhealth.monitor.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class DashboardUiState(
    val healthScore: Int = 100,
    val activeMalfunctions: List<MalfunctionInfo> = emptyList(),
    val tyreStatus: TyreStatus? = null,
    val vehicleHealth: VehicleHealth? = null,
    val maintenanceItems: List<MaintenanceItemStatus> = emptyList(),
    val isLoading: Boolean = true,
) {
    val criticalCount: Int get() = activeMalfunctions.count { it.severity == Severity.CRITICAL }
    val warningCount: Int  get() = activeMalfunctions.count { it.severity == Severity.WARNING }
    val overdueCount: Int  get() = maintenanceItems.count { it.status == MaintenanceStatus.OVERDUE }
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val malfunctionRepo: MalfunctionRepository,
    private val tyreRepo: TyreRepository,
    private val statisticRepo: StatisticRepository,
    private val maintenanceRepo: MaintenanceRepositoryImpl,
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        malfunctionRepo.observeActiveCodes(),
        tyreRepo.observeTyreStatus(),
        statisticRepo.observeVehicleHealth(),
        combine(MaintenanceItemType.entries.map { type ->
            maintenanceRepo.observeLatest(type).map { record -> type to record }
        }) { it.toMap() },
    ) { codes, tyre, health, latestMap ->
        val malfunctions = codes.mapNotNull { MalfunctionCatalog.resolve(it) }
        val maintenanceItems = MaintenanceItemType.entries.map { type ->
            val record = latestMap[type]
            MaintenanceItemStatus(
                type = type,
                lastReplacedMileage = record?.mileageAtReplacement ?: 0,
                currentMileage = health.totalMileage,
                lastReplacedAt = record?.replacedAt ?: 0L,
            )
        }
        val score = calculateScore(malfunctions, maintenanceItems)
        DashboardUiState(
            healthScore = score,
            activeMalfunctions = malfunctions,
            tyreStatus = tyre,
            vehicleHealth = health,
            maintenanceItems = maintenanceItems,
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    private fun calculateScore(
        malfunctions: List<MalfunctionInfo>,
        maintenance: List<MaintenanceItemStatus>,
    ): Int {
        var score = 100
        score -= malfunctions.count { it.severity == Severity.CRITICAL } * 40
        score -= malfunctions.count { it.severity == Severity.WARNING  } * 15
        score -= maintenance.count  { it.status == MaintenanceStatus.OVERDUE } * 10
        return score.coerceIn(0, 100)
    }
}
