package com.bydhealth.monitor.ui.maintenance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bydhealth.monitor.data.local.MaintenanceRepositoryImpl
import com.bydhealth.monitor.data.local.entity.MaintenanceRecord
import com.bydhealth.monitor.data.vehicle.StatisticRepository
import com.bydhealth.monitor.domain.maintenance.MaintenanceItemStatus
import com.bydhealth.monitor.domain.maintenance.MaintenanceItemType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MaintenanceUiState(
    val items: List<MaintenanceItemStatus> = emptyList(),
    val currentMileage: Int = 0,
    val isLoading: Boolean = true,
)

@HiltViewModel
class MaintenanceViewModel @Inject constructor(
    private val maintenanceRepo: MaintenanceRepositoryImpl,
    private val statisticRepo: StatisticRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaintenanceUiState())
    val uiState: StateFlow<MaintenanceUiState> = _uiState.asStateFlow()

    val history: StateFlow<List<MaintenanceRecord>> = maintenanceRepo.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            combine(
                statisticRepo.observeVehicleHealth(),
                // 각 항목별 최신 기록을 합성
                combine(MaintenanceItemType.entries.map { type ->
                    maintenanceRepo.observeLatest(type).map { record -> type to record }
                }) { pairs -> pairs.toMap() },
            ) { health, latestMap ->
                val currentMileage = health.totalMileage
                val items = MaintenanceItemType.entries.map { type ->
                    val record = latestMap[type]
                    MaintenanceItemStatus(
                        type = type,
                        lastReplacedMileage = record?.mileageAtReplacement ?: 0,
                        currentMileage = currentMileage,
                        lastReplacedAt = record?.replacedAt ?: 0L,
                    )
                }
                MaintenanceUiState(items = items, currentMileage = currentMileage, isLoading = false)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun addRecord(type: MaintenanceItemType, mileage: Int, dateMs: Long, note: String = "") {
        viewModelScope.launch {
            maintenanceRepo.insert(
                MaintenanceRecord(
                    itemType = type.name,
                    replacedAt = dateMs,
                    mileageAtReplacement = mileage,
                    note = note,
                )
            )
        }
    }

    fun deleteRecord(record: MaintenanceRecord) {
        viewModelScope.launch { maintenanceRepo.delete(record) }
    }
}
