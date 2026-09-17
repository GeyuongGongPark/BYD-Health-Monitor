package com.bydhealth.monitor.domain.model

data class VehicleHealth(
    val totalMileage: Int,       // km
    val elecPercentage: Double,  // 0.0-100.0 %
    val elecDrivingRange: Int,   // km
    val fuelPercentage: Int,     // 0-100 %
    val fuelDrivingRange: Int,   // km
    val waterTemperature: Int,   // °C
    val energyMode: Int,         // ENERGY_MODE_*
    val instantElecCon: Double,  // kWh/100km
    val instantFuelCon: Double,  // L/100km
)
