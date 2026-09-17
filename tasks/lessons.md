# BYD Health Monitor — 개발 교훈

> Phase 1–6 구현 과정에서 얻은 기술적 결정과 교훈 정리

---

## 1. BYD OpenAPI — Stub 모듈 전략

### 문제
`bydauto-openapi.jar`를 BYD로부터 공식 제공받을 수 없었음.
실기기(DiLink)에서 ADB로 추출하거나 시스템 APK에서 분리해야 하는데, 개발 단계에서 실기기 없음.

### 해결
`:bydauto-stub` Android Library 모듈을 직접 작성:
- `bydauto-stub/build.gradle.kts` — `compileOnly` 대상 Android Library
- 20개 디바이스 패키지 전체를 Java stub으로 구현 (`throw RuntimeException("Stub!")`)
- `android/build.gradle.kts`에서 `compileOnly(project(":bydauto-stub"))`

### 실기기 전환 방법
1. 실기기에서 ADB로 jar 추출: `adb pull /system/...`
2. `android/libs/bydauto-openapi.jar` 배치
3. `build.gradle.kts`에서 `compileOnly(project(":bydauto-stub"))` → `compileOnly(files("libs/bydauto-openapi.jar"))` 한 줄 교체

### 핵심 원칙
Stub의 패키지명(`android.hardware.bydauto.*`)은 실제 API와 완전히 일치해야 함.
`compileOnly`이므로 APK에는 포함되지 않으며, 런타임에는 시스템이 제공하는 클래스가 사용됨.

---

## 2. Mock Fallback 패턴

### 패턴
```kotlin
// BydApiLoader.kt
private fun <T> tryLoad(name: String, block: () -> T): T? =
    try { block() }
    catch (e: NoClassDefFoundError) { null }
    catch (e: Throwable) { null }

val instrumentDevice: BYDAutoInstrumentDevice? by lazy {
    tryLoad("InstrumentDevice") { BYDAutoInstrumentDevice.getInstance(context) }
}
```

Repository에서 `device == null`이면 `MockVehicleData`를 반환:
```kotlin
if (loader.instrumentDevice == null) {
    emit(MockVehicleData.activeMalfunctionCodes)
} else { ... }
```

### 효과
- 에뮬레이터/일반 Android 기기에서 전체 UI 개발 및 테스트 가능
- 실기기 확보 전에 Phase 1–6 완전 구현 가능
- `isRealDevice` 프로퍼티로 UI에서 Mock/Real 분기 표시 가능

---

## 3. 폴링 패턴 (Listener 대신)

### 선택 이유
BYD OpenAPI는 Listener 콜백(`IBYDAutoListener`)을 제공하지만, 실기기 검증 없이 콜백 타이밍을 신뢰할 수 없음.
폴링이 더 단순하고 Mock 데이터와 동일한 코드 경로를 공유할 수 있음.

### 구현
```kotlin
flow {
    while (true) {
        emit(fetchData())
        delay(3_000L)
    }
}.flowOn(Dispatchers.IO)
```

### 폴링 주기
| Repository | 주기 | 이유 |
|---|---|---|
| TyreRepository | 3초 | 주행 중 빠른 변화 가능 |
| MalfunctionRepository | 5초 | 고장 코드 변화 느림 |
| StatisticRepository | 5초 | 주행거리/배터리 변화 느림 |
| ComponentRepository | 10초 | 소모품 레벨 거의 안 변함 |

---

## 4. DiLink 환경 특이사항

- **화면 방향**: `screenOrientation="landscape"` 고정, `sensorLandscape` 아님
- **시스템 바**: `WindowInsetsControllerCompat`으로 완전 숨김 필수 (전체화면 모드)
- **화면 ON 유지**: `FLAG_KEEP_SCREEN_ON` 필수 (주차 중 꺼지면 안 됨)
- **GMS 없음**: Firebase, Google Maps 등 GMS 의존 라이브러리 사용 불가 (Phase 7 서버 측에서 FCM 처리, DiLink 앱 자체는 HTTP POST만)
- **ProGuard**: `android.hardware.bydauto.**` keep 필수, 없으면 Reflection 체인 실패

---

## 5. 건강 점수 산정 공식

```
점수 = 100 - (CRITICAL 수 × 40) - (WARNING 수 × 15) - (소모품 초과 수 × 10)
      clamp(0, 100)
```

- CRITICAL 1개만으로도 60점 → 심각성 강조 의도
- 소모품은 OVERDUE만 감점 (WARNING은 경고 표시만)

---

## 6. Hilt ViewModel 공유

`MaintenanceViewModel`은 `MaintenanceScreen`과 `MaintenanceHistoryScreen`이 공유함.
두 화면이 `NavHost` 내에서 같은 `hiltViewModel()`을 호출하면 각자 별도 인스턴스를 받게 됨.

실제로는 둘 다 Room Flow를 직접 구독하므로 데이터 일관성에 문제 없음.
상태를 공유해야 하는 경우라면 `NavBackStackEntry`를 통한 ViewModel 공유 패턴 필요.

---

## 7. Room `fallbackToDestructiveMigration`

개발 중 스키마 변경 시 Migration 코드 없이 DB를 재생성하도록 설정.
**프로덕션 배포 전 실제 Migration으로 교체 필요**.

```kotlin
// MaintenanceDatabase.kt
.fallbackToDestructiveMigration()  // ← 프로덕션 전 addMigrations()로 교체
```

---

## 8. Phase 7 설계 결정

### DiLink → 서버 직접 통신
DiLink에 내장 SIM이 있고 테더링도 가능하므로 브로커 없이 HTTP 직접 전송 가능.
단, 연결 불안정 구간 대비 retry + pending 큐 필요.

### FCM 발송은 서버에서
DiLink 앱(Android)이 직접 FCM을 발송할 수 없음 (GMS 없음).
반드시 Railway 서버(Firebase Admin SDK)를 경유해야 함.

### 컴패니언 앱 FCM
컴패니언 앱(스마트폰)은 GMS가 있으므로 FCM 직접 수신 가능.
