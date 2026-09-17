package com.bydhealth.monitor.domain.model

data class TyreData(
    val area: Int,
    val pressureValue: Int,     // kPa
    val pressureState: Int,     // TYRE_PRESSURE_STATE_*
    val temperatureState: Int,  // TYRE_TEMPERATURE_STATE_*
    val airLeakState: Int,      // TYRE_AIR_LEAK_STATE_*
    val signalState: Int,
)

data class TyreStatus(
    val leftFront: TyreData,
    val rightFront: TyreData,
    val leftRear: TyreData,
    val rightRear: TyreData,
    val systemState: Int,       // TYRE_SYSTEM_STATE_*
)
