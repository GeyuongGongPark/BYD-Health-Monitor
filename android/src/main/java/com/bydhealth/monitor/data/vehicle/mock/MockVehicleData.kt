package com.bydhealth.monitor.data.vehicle.mock

import android.hardware.bydauto.tyre.BYDAutoTyreDevice
import com.bydhealth.monitor.domain.model.ComponentStatus
import com.bydhealth.monitor.domain.model.TyreData
import com.bydhealth.monitor.domain.model.TyreStatus
import com.bydhealth.monitor.domain.model.VehicleHealth

object MockVehicleData {

    /** 고장 없음 — 활성 고장 코드 없음 */
    val activeMalfunctionCodes: List<Int> = emptyList()

    val tyreStatus = TyreStatus(
        leftFront = TyreData(
            area = BYDAutoTyreDevice.TYRE_COMMAND_AREA_LEFT_FRONT,
            pressureValue = 240,
            pressureState = BYDAutoTyreDevice.TYRE_PRESSURE_STATE_NORMAL,
            temperatureState = BYDAutoTyreDevice.TYRE_TEMPERATURE_STATE_NORMAL,
            airLeakState = BYDAutoTyreDevice.TYRE_AIR_LEAK_STATE_NORMAL,
            signalState = 1,
        ),
        rightFront = TyreData(
            area = BYDAutoTyreDevice.TYRE_COMMAND_AREA_RIGHT_FRONT,
            pressureValue = 238,
            pressureState = BYDAutoTyreDevice.TYRE_PRESSURE_STATE_NORMAL,
            temperatureState = BYDAutoTyreDevice.TYRE_TEMPERATURE_STATE_NORMAL,
            airLeakState = BYDAutoTyreDevice.TYRE_AIR_LEAK_STATE_NORMAL,
            signalState = 1,
        ),
        leftRear = TyreData(
            area = BYDAutoTyreDevice.TYRE_COMMAND_AREA_LEFT_REAR,
            pressureValue = 235,
            pressureState = BYDAutoTyreDevice.TYRE_PRESSURE_STATE_NORMAL,
            temperatureState = BYDAutoTyreDevice.TYRE_TEMPERATURE_STATE_NORMAL,
            airLeakState = BYDAutoTyreDevice.TYRE_AIR_LEAK_STATE_NORMAL,
            signalState = 1,
        ),
        rightRear = TyreData(
            area = BYDAutoTyreDevice.TYRE_COMMAND_AREA_RIGHT_REAR,
            pressureValue = 242,
            pressureState = BYDAutoTyreDevice.TYRE_PRESSURE_STATE_NORMAL,
            temperatureState = BYDAutoTyreDevice.TYRE_TEMPERATURE_STATE_NORMAL,
            airLeakState = BYDAutoTyreDevice.TYRE_AIR_LEAK_STATE_NORMAL,
            signalState = 1,
        ),
        systemState = BYDAutoTyreDevice.TYRE_SYSTEM_STATE_NORMAL,
    )

    val vehicleHealth = VehicleHealth(
        totalMileage = 15_000,
        elecPercentage = 80.0,
        elecDrivingRange = 320,
        fuelPercentage = 0,
        fuelDrivingRange = 0,
        waterTemperature = 88,
        energyMode = 1, // ENERGY_MODE_EV
        instantElecCon = 14.5,
        instantFuelCon = 0.0,
    )

    val componentStatus = ComponentStatus(
        oilLevel = 1,
        coolantLevel = 1,
        brakeFluidLevel = 1,
    )
}
