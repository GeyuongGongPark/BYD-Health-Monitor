package com.bydhealth.monitor.data.vehicle

import android.content.Context
import android.hardware.bydauto.engine.BYDAutoEngineDevice
import android.hardware.bydauto.gearbox.BYDAutoGearboxDevice
import android.hardware.bydauto.instrument.BYDAutoInstrumentDevice
import android.hardware.bydauto.statistic.BYDAutoStatisticDevice
import android.hardware.bydauto.tyre.BYDAutoTyreDevice
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * BYD Auto OpenAPI 디바이스 로더.
 *
 * BYD DiLink 환경: 시스템 ClassLoader에 실제 구현이 있어 정상 로드됨.
 * 일반 기기/에뮬레이터: NoClassDefFoundError → null 반환 → Repository가 Mock 데이터 사용.
 *
 * 실기기(bydauto-openapi.jar) 확보 시:
 * android/build.gradle.kts에서 compileOnly(project(":bydauto-stub"))을
 * compileOnly(files("libs/bydauto-openapi.jar"))로 교체.
 */
@Singleton
class BydApiLoader @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    val instrumentDevice: BYDAutoInstrumentDevice? by lazy {
        tryLoad("InstrumentDevice") { BYDAutoInstrumentDevice.getInstance(context) }
    }

    val tyreDevice: BYDAutoTyreDevice? by lazy {
        tryLoad("TyreDevice") { BYDAutoTyreDevice.getInstance(context) }
    }

    val statisticDevice: BYDAutoStatisticDevice? by lazy {
        tryLoad("StatisticDevice") { BYDAutoStatisticDevice.getInstance(context) }
    }

    val engineDevice: BYDAutoEngineDevice? by lazy {
        tryLoad("EngineDevice") { BYDAutoEngineDevice.getInstance(context) }
    }

    val gearboxDevice: BYDAutoGearboxDevice? by lazy {
        tryLoad("GearboxDevice") { BYDAutoGearboxDevice.getInstance(context) }
    }

    /** true면 실기기(BYD DiLink), false면 Mock 모드 */
    val isRealDevice: Boolean get() = instrumentDevice != null

    private fun <T> tryLoad(name: String, block: () -> T): T? = try {
        block()
    } catch (e: NoClassDefFoundError) {
        Log.i(TAG, "$name: BYD HAL 미탑재 환경 — Mock 모드로 동작")
        null
    } catch (e: Throwable) {
        Log.w(TAG, "$name 로드 실패: ${e.message}")
        null
    }

    companion object {
        private const val TAG = "BydApiLoader"
    }
}
