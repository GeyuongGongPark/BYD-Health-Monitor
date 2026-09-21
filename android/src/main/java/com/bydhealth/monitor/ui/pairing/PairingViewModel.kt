package com.bydhealth.monitor.ui.pairing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bydhealth.monitor.data.network.PairingCode
import com.bydhealth.monitor.data.network.PairingRepository
import com.bydhealth.monitor.data.vehicle.VehicleIdentityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PairingState {
    object Loading : PairingState()
    data class Ready(val pairing: PairingCode) : PairingState()
    data class Error(val message: String) : PairingState()
}

@HiltViewModel
class PairingViewModel @Inject constructor(
    private val pairingRepository: PairingRepository,
    private val vehicleIdentityRepository: VehicleIdentityRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<PairingState>(PairingState.Loading)
    val state: StateFlow<PairingState> = _state

    init {
        loadCode()
    }

    fun refresh() {
        loadCode(forceRefresh = true)
    }

    private fun loadCode(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _state.value = PairingState.Loading

            if (!forceRefresh) {
                val cached = pairingRepository.getCachedCode()
                if (cached != null) {
                    _state.value = PairingState.Ready(cached)
                    return@launch
                }
            }

            val vin = vehicleIdentityRepository.getVin()
            val result = pairingRepository.generateCode(vin)
            _state.value = result.fold(
                onSuccess = { PairingState.Ready(it) },
                onFailure = { PairingState.Error(it.message ?: "코드 발급 실패") },
            )
        }
    }
}
