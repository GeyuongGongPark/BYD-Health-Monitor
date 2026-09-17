package com.bydhealth.monitor.domain.maintenance

enum class MaintenanceItemType(
    val displayName: String,
    val intervalKm: Int,
    val warningKm: Int = 3_000,   // 교체 임박 경고 기준 (남은 km)
    val isAutoTracked: Boolean,   // 실시간 API로 레벨 확인 가능 여부
) {
    ENGINE_OIL("엔진 오일",    10_000, isAutoTracked = true),
    BRAKE_FLUID("브레이크액",  40_000, isAutoTracked = true),
    COOLANT("냉각수",          60_000, isAutoTracked = true),
    TYRE("타이어",             40_000, isAutoTracked = false),
    AC_FILTER("에어컨 필터",   15_000, isAutoTracked = false),
    BRAKE_PAD("브레이크 패드", 30_000, isAutoTracked = false),
}

/** 교체 후 남은 km 기반 상태 */
enum class MaintenanceStatus { OVERDUE, WARNING, NORMAL }

data class MaintenanceItemStatus(
    val type: MaintenanceItemType,
    val lastReplacedMileage: Int,     // 마지막 교체 시 주행거리
    val currentMileage: Int,          // 현재 주행거리
    val lastReplacedAt: Long,         // epoch ms, 0이면 기록 없음
) {
    val drivenSinceReplacement: Int get() = currentMileage - lastReplacedMileage
    val remainingKm: Int get() = type.intervalKm - drivenSinceReplacement
    val status: MaintenanceStatus get() = when {
        remainingKm <= 0                -> MaintenanceStatus.OVERDUE
        remainingKm <= type.warningKm  -> MaintenanceStatus.WARNING
        else                           -> MaintenanceStatus.NORMAL
    }
}
