package com.bydhealth.monitor.data.vehicle

import android.util.Log
import com.bydhealth.monitor.data.vehicle.mock.MockVehicleData
import com.bydhealth.monitor.domain.model.ComponentStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ComponentRepository @Inject constructor(
    private val apiLoader: BydApiLoader,
) {
    /**
     * 엔진 오일, 냉각수, 브레이크액 레벨을 10초 간격으로 폴링.
     * 소모품 레벨은 주행 중 빠르게 변하지 않으므로 폴링 간격을 길게 설정.
     *
     * 반환값: -1이면 해당 디바이스 미지원
     */
    fun observeComponentStatus(): Flow<ComponentStatus> = flow {
        while (true) {
            val engine = apiLoader.engineDevice
            val gearbox = apiLoader.gearboxDevice
            val status = if (engine != null || gearbox != null) {
                try {
                    ComponentStatus(
                        oilLevel = engine?.getOilLevel() ?: UNSUPPORTED,
                        coolantLevel = engine?.getEngineCoolantLevel() ?: UNSUPPORTED,
                        brakeFluidLevel = gearbox?.getBrakeFluidLevel() ?: UNSUPPORTED,
                    )
                } catch (e: SecurityException) {
                    Log.w(TAG, "BYDAUTO_ENGINE/GEARBOX 권한 없음, Mock 데이터 사용: ${e.message}")
                    MockVehicleData.componentStatus
                }
            } else {
                MockVehicleData.componentStatus
            }
            emit(status)
            delay(POLL_INTERVAL_MS)
        }
    }.flowOn(Dispatchers.IO)

    companion object {
        private const val TAG = "ComponentRepository"
        private const val POLL_INTERVAL_MS = 10_000L
        const val UNSUPPORTED = -1
    }
}
