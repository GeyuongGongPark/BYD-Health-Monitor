package com.bydhealth.monitor.domain.model

data class ComponentStatus(
    val oilLevel: Int,        // EngineDevice.getOilLevel() — -1이면 미지원
    val coolantLevel: Int,    // EngineDevice.getEngineCoolantLevel()
    val brakeFluidLevel: Int, // GearboxDevice.getBrakeFluidLevel()
)
