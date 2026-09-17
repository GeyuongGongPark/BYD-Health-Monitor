package com.bydhealth.monitor.ui.tyre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bydhealth.monitor.data.vehicle.TyreRepository
import com.bydhealth.monitor.domain.model.TyreStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class TyreViewModel @Inject constructor(
    tyreRepository: TyreRepository,
) : ViewModel() {

    val tyreStatus: StateFlow<TyreStatus?> = tyreRepository.observeTyreStatus()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
