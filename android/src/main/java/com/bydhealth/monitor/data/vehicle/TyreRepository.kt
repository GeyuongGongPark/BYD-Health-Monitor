package com.bydhealth.monitor.data.vehicle

import android.hardware.bydauto.tyre.BYDAutoTyreDevice
import android.util.Log
import com.bydhealth.monitor.data.vehicle.mock.MockVehicleData
import com.bydhealth.monitor.domain.model.TyreData
import com.bydhealth.monitor.domain.model.TyreStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TyreRepository @Inject constructor(
    private val apiLoader: BydApiLoader,
) {
    /** 4개 타이어 상태를 3초 간격으로 폴링 */
    fun observeTyreStatus(): Flow<TyreStatus> = flow {
        while (true) {
            val device = apiLoader.tyreDevice
            val status = if (device != null) {
                try {
                    TyreStatus(
                        leftFront = device.readTyreData(BYDAutoTyreDevice.TYRE_COMMAND_AREA_LEFT_FRONT),
                        rightFront = device.readTyreData(BYDAutoTyreDevice.TYRE_COMMAND_AREA_RIGHT_FRONT),
                        leftRear = device.readTyreData(BYDAutoTyreDevice.TYRE_COMMAND_AREA_LEFT_REAR),
                        rightRear = device.readTyreData(BYDAutoTyreDevice.TYRE_COMMAND_AREA_RIGHT_REAR),
                        systemState = device.getTyreSystemState(),
                    )
                } catch (e: SecurityException) {
                    Log.w(TAG, "BYDAUTO_TYRE 권한 없음, Mock 데이터 사용: ${e.message}")
                    MockVehicleData.tyreStatus
                }
            } else {
                MockVehicleData.tyreStatus
            }
            emit(status)
            delay(POLL_INTERVAL_MS)
        }
    }.flowOn(Dispatchers.IO)

    private fun BYDAutoTyreDevice.readTyreData(area: Int) = TyreData(
        area = area,
        pressureValue = getTyrePressureValue(area),
        pressureState = getTyrePressureState(area),
        temperatureState = getTyreTemperatureState(),
        airLeakState = getTyreAirLeakState(area),
        signalState = getTyreSignalState(area),
    )

    companion object {
        private const val TAG = "TyreRepository"
        private const val POLL_INTERVAL_MS = 3_000L
    }
}
