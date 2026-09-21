package com.bydhealth.monitor.ui.malfunction

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bydhealth.monitor.data.network.AlertDispatcher
import com.bydhealth.monitor.data.vehicle.MalfunctionRepository
import com.bydhealth.monitor.data.vehicle.VehicleIdentityRepository
import com.bydhealth.monitor.domain.malfunction.MalfunctionCatalog
import com.bydhealth.monitor.domain.model.MalfunctionInfo
import com.bydhealth.monitor.domain.model.Severity
import com.bydhealth.monitor.ui.common.MalfunctionNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MalfunctionUiState(
    val isLoading: Boolean = true,
    val activeMalfunctions: List<MalfunctionInfo> = emptyList(),
) {
    val isAllNormal: Boolean get() = !isLoading && activeMalfunctions.isEmpty()
    val hasCritical: Boolean get() = activeMalfunctions.any { it.severity == Severity.CRITICAL }
}

@HiltViewModel
class MalfunctionViewModel @Inject constructor(
    private val repository: MalfunctionRepository,
    private val alertDispatcher: AlertDispatcher,
    private val vehicleIdentityRepository: VehicleIdentityRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MalfunctionUiState())
    val uiState: StateFlow<MalfunctionUiState> = _uiState.asStateFlow()

    private var lastNotifiedCritical = emptySet<Int>()

    init {
        viewModelScope.launch {
            repository.observeActiveCodes().collect { codes ->
                val malfunctions = codes
                    .mapNotNull { MalfunctionCatalog.resolve(it) }
                    .sortedBy { it.severity.ordinal }
                _uiState.update { it.copy(isLoading = false, activeMalfunctions = malfunctions) }
                maybeNotify(malfunctions)
            }
        }
    }

    private fun maybeNotify(malfunctions: List<MalfunctionInfo>) {
        val currentCritical = malfunctions
            .filter { it.severity == Severity.CRITICAL }
            .map { it.code }.toSet()
        if (currentCritical != lastNotifiedCritical) {
            MalfunctionNotification.notify(context, malfunctions)
            lastNotifiedCritical = currentCritical
            // 새 CRITICAL 발생 시 Railway 서버로 알림 전송 (FCM → 컴패니언 앱)
            viewModelScope.launch {
                alertDispatcher.dispatch(malfunctions, vehicleIdentityRepository.getVin())
            }
        }
    }
}
