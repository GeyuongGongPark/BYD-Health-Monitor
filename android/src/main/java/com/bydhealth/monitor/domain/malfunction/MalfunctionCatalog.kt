package com.bydhealth.monitor.domain.malfunction

import android.hardware.bydauto.instrument.BYDAutoInstrumentDevice
import com.bydhealth.monitor.domain.model.MalfunctionInfo
import com.bydhealth.monitor.domain.model.Severity

object MalfunctionCatalog {

    private data class Entry(val name: String, val severity: Severity, val action: String)

    private val catalog = mapOf(
        BYDAutoInstrumentDevice.MALFUNCTION_INSTRUMENT_DISPLAY      to Entry("계기판 디스플레이 이상",       Severity.INFO,     "서비스센터 방문"),
        BYDAutoInstrumentDevice.MALFUNCTION_MACHINE_OIL_LOW_PRESSURE to Entry("엔진 오일 압력 낮음",         Severity.WARNING,  "즉시 오일 레벨 확인"),
        BYDAutoInstrumentDevice.MALFUNCTION_PARKING_BRAKE            to Entry("파킹 브레이크 이상",           Severity.WARNING,  "주행 주의, 서비스센터 방문"),
        BYDAutoInstrumentDevice.MALFUNCTION_CHARGING_SYSTEM          to Entry("충전 시스템 점검 필요",         Severity.WARNING,  "서비스센터 방문"),
        BYDAutoInstrumentDevice.MALFUNCTION_ENGINE                   to Entry("엔진 이상 감지",               Severity.CRITICAL, "즉시 안전한 곳에 정차, 서비스센터 연락"),
        BYDAutoInstrumentDevice.MALFUNCTION_ABS_SYSTEM               to Entry("ABS 시스템 점검 필요",          Severity.WARNING,  "가까운 서비스센터 방문"),
        BYDAutoInstrumentDevice.MALFUNCTION_ESP                      to Entry("ESP 시스템 점검 필요",          Severity.WARNING,  "가까운 서비스센터 방문"),
        BYDAutoInstrumentDevice.MALFUNCTION_QUICK_AIR_LEAK           to Entry("타이어 급속 공기 누출",          Severity.WARNING,  "즉시 정차 및 타이어 교체"),
        BYDAutoInstrumentDevice.MALFUNCTION_HIGH_WATER_TEMPERATURE   to Entry("엔진 수온 과열",               Severity.WARNING,  "즉시 정차 및 냉각 확인"),
        BYDAutoInstrumentDevice.MALFUNCTION_ELECTRIC_PARKING_BRAKE   to Entry("전자식 파킹 브레이크 이상",      Severity.WARNING,  "서비스센터 방문"),
        BYDAutoInstrumentDevice.MALFUNCTION_SRS                      to Entry("에어백(SRS) 시스템 이상",       Severity.CRITICAL, "즉시 점검 필요"),
        BYDAutoInstrumentDevice.MALFUNCTION_EPS                      to Entry("전동 파워 스티어링 이상",        Severity.WARNING,  "주행 주의, 서비스센터 방문"),
        BYDAutoInstrumentDevice.MALFUNCTION_TYRE_PRESSURE            to Entry("타이어 공기압 이상",            Severity.WARNING,  "주행 전 타이어 확인"),
        BYDAutoInstrumentDevice.MALFUNCTION_SVS                      to Entry("SVS 점검 필요",                Severity.INFO,     "서비스센터 방문"),
        BYDAutoInstrumentDevice.MALFUNCTION_HIGH_MOTOR_TEMPERATURE   to Entry("모터 과열",                    Severity.CRITICAL, "즉시 정차 및 냉각"),
        BYDAutoInstrumentDevice.MALFUNCTION_BATTERY                  to Entry("고전압 배터리 이상",            Severity.CRITICAL, "즉시 BYD 서비스센터 연락"),
        BYDAutoInstrumentDevice.MALFUNCTION_HIGH_BATTERY_TEMPERATURE to Entry("배터리 과열",                  Severity.CRITICAL, "즉시 정차 및 냉각"),
        BYDAutoInstrumentDevice.MALFUNCTION_POWER_SYSTEM             to Entry("동력 시스템 이상",              Severity.CRITICAL, "즉시 서비스센터 연락"),
        BYDAutoInstrumentDevice.MALFUNCTION_EV                       to Entry("EV 시스템 이상",               Severity.CRITICAL, "즉시 서비스센터 연락"),
        BYDAutoInstrumentDevice.MALFUNCTION_HEV                      to Entry("HEV 시스템 점검 필요",          Severity.WARNING,  "서비스센터 방문"),
        BYDAutoInstrumentDevice.MALFUNCTION_SMART_KEY                to Entry("스마트키 배터리 부족",           Severity.INFO,     "스마트키 배터리 교체"),
        BYDAutoInstrumentDevice.MALFUNCTION_FRONT_BELT               to Entry("안전벨트 미착용 감지",           Severity.INFO,     "안전벨트를 착용하세요"),
    )

    fun resolve(code: Int, isActive: Boolean = true): MalfunctionInfo? =
        catalog[code]?.let { entry ->
            MalfunctionInfo(
                code = code,
                name = entry.name,
                description = entry.name,
                severity = entry.severity,
                action = entry.action,
                isActive = isActive,
            )
        }
}
