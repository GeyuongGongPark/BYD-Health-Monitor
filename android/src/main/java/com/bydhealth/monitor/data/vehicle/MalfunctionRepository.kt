package com.bydhealth.monitor.data.vehicle

import com.bydhealth.monitor.data.vehicle.mock.MockVehicleData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MalfunctionRepository @Inject constructor(
    private val apiLoader: BydApiLoader,
) {
    /**
     * 23종 고장 코드를 5초 간격으로 폴링하여 활성화된 코드 목록을 방출.
     *
     * TODO: 실기기에서 getMalfunctionInfo() 반환값 의미 검증 필요
     *       현재는 0 = 정상, 그 외 = 고장으로 가정
     *
     * Phase 3에서 MalfunctionCatalog를 통해 List<MalfunctionInfo>로 변환.
     */
    fun observeActiveCodes(): Flow<List<Int>> = flow {
        while (true) {
            val device = apiLoader.instrumentDevice
            val activeCodes = if (device != null) {
                ALL_CODES.filter { code -> device.getMalfunctionInfo(code) != 0 }
            } else {
                MockVehicleData.activeMalfunctionCodes
            }
            emit(activeCodes)
            delay(POLL_INTERVAL_MS)
        }
    }.flowOn(Dispatchers.IO)

    companion object {
        private const val POLL_INTERVAL_MS = 5_000L
        val ALL_CODES = (1..23).toList()
    }
}
