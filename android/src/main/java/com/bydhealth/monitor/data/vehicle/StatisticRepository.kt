package com.bydhealth.monitor.data.vehicle

import android.util.Log
import com.bydhealth.monitor.data.vehicle.mock.MockVehicleData
import com.bydhealth.monitor.domain.model.VehicleHealth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticRepository @Inject constructor(
    private val apiLoader: BydApiLoader,
) {
    /** 차량 주행 통계 및 배터리 상태를 5초 간격으로 폴링 */
    fun observeVehicleHealth(): Flow<VehicleHealth> = flow {
        while (true) {
            val device = apiLoader.statisticDevice
            val health = if (device != null) {
                try {
                    VehicleHealth(
                        totalMileage = device.getTotalMileageValue(),
                        elecPercentage = device.getElecPercentageValue(),
                        elecDrivingRange = device.getElecDrivingRangeValue(),
                        fuelPercentage = device.getFuelPercentageValue(),
                        fuelDrivingRange = device.getFuelDrivingRangeValue(),
                        waterTemperature = device.getWaterTemperature(),
                        energyMode = 0, // TODO: EnergyDevice 별도 연동 시 채움
                        instantElecCon = device.getInstantElecConValue(),
                        instantFuelCon = device.getInstantFuelConValue(),
                    )
                } catch (e: SecurityException) {
                    Log.w(TAG, "BYDAUTO_STATISTIC 권한 없음, Mock 데이터 사용: ${e.message}")
                    MockVehicleData.vehicleHealth
                }
            } else {
                MockVehicleData.vehicleHealth
            }
            emit(health)
            delay(POLL_INTERVAL_MS)
        }
    }.flowOn(Dispatchers.IO)

    companion object {
        private const val TAG = "StatisticRepository"
        private const val POLL_INTERVAL_MS = 5_000L
    }
}
