# BYD Health Monitor 개발 TODO

> **컨셉**: 고장 코드 23종 + 타이어/소모품 상태를 사용자가 이해하기 쉽게 보여주는 차량 건강 앱
> **플랫폼**: Android (DiLink 전용, 스마트폰 연동은 Phase 7 이후 검토)
> **OpenAPI 접근 방식**: BYDLauncher와 동일한 DexClassLoader + Reflection 체인 (검증된 방식)

---

## Phase 1 — 프로젝트 셋업

### 1-1. Gradle 구성
- [x] settings.gradle.kts
- [x] build.gradle.kts (root)
- [x] gradle/libs.versions.toml (Compose, Hilt, Room, Coroutines)
- [x] gradle/wrapper/gradle-wrapper.properties
- [x] android/build.gradle.kts
- [x] android/proguard-rules.pro

### 1-2. 앱 기반 파일
- [x] android/src/main/AndroidManifest.xml
- [x] HealthMonitorApplication.kt (Hilt)
- [x] MainActivity.kt (전체화면, Landscape 고정)

### 1-3. 테마 / 디자인 시스템
- [x] ui/theme/Color.kt (BYD 다크 팔레트)
- [x] ui/theme/Theme.kt (Material3 다크 스킴)
- [x] ui/theme/Type.kt

---

## Phase 2 — BYD Auto OpenAPI 연동

> BYDLauncher의 VehicleViewModel DexClassLoader 방식을 그대로 참조.
> 필요 디바이스: Instrument, Tyre, Statistic, Engine, Gearbox, Charging, Setting

### 2-1. OpenAPI 브릿지
- [x] `data/vehicle/BydApiLoader.kt` — tryLoad + NoClassDefFoundError → Mock fallback
- [x] `bydauto-openapi.jar` → `:bydauto-stub` Gradle 모듈로 대체 (compileOnly)

### 2-2. 디바이스별 Repository
- [x] `data/vehicle/MalfunctionRepository.kt` — `InstrumentDevice.getMalfunctionInfo(type)` 23종 폴링, 활성 코드 Flow 방출
- [x] `data/vehicle/TyreRepository.kt` — `TyreDevice` 4개 타이어 압력/온도/누출/신호
- [x] `data/vehicle/StatisticRepository.kt` — `StatisticDevice` 총 주행거리, 배터리%, 전비
- [x] `data/vehicle/ComponentRepository.kt` — `EngineDevice.getOilLevel()`, `getEngineCoolantLevel()`, `GearboxDevice.getBrakeFluidLevel()`

### 2-3. 도메인 모델
- [x] `domain/model/Malfunction.kt` — Severity enum + MalfunctionInfo data class
- [x] `domain/model/TyreStatus.kt` — TyreData + TyreStatus
- [x] `domain/model/VehicleHealth.kt` — 주행 통계 집합체
- [x] `domain/model/ComponentStatus.kt` — 소모품 레벨 집합체
- [x] `data/vehicle/mock/MockVehicleData.kt` — 일반 기기/에뮬레이터용 Mock

---

## Phase 3 — 고장 코드 번역기 (핵심)

> `InstrumentDevice.getMalfunctionInfo(typeName)` 으로 23종 조회.
> 각 코드에 한국어 설명 + 긴급도 + 권장 조치를 매핑한 내부 DB 구축.

### 3-1. 고장 코드 매핑 데이터
- [x] `domain/malfunction/MalfunctionCatalog.kt` — 23종 상수 → 설명/긴급도/권장조치 매핑

  | 코드 | 긴급도 | 설명 | 권장 조치 |
  |------|--------|------|-----------|
  | MALFUNCTION_ENGINE | CRITICAL | 엔진 이상 감지 | 즉시 안전한 곳에 정차, 서비스센터 연락 |
  | MALFUNCTION_BATTERY | CRITICAL | 고전압 배터리 이상 | 즉시 BYD 서비스센터 연락 |
  | MALFUNCTION_SRS | CRITICAL | 에어백(SRS) 시스템 이상 | 즉시 점검 필요 |
  | MALFUNCTION_HIGH_BATTERY_TEMPERATURE | CRITICAL | 배터리 과열 | 즉시 정차 및 냉각 |
  | MALFUNCTION_HIGH_MOTOR_TEMPERATURE | CRITICAL | 모터 과열 | 즉시 정차 및 냉각 |
  | MALFUNCTION_POWER_SYSTEM | CRITICAL | 동력 시스템 이상 | 즉시 서비스센터 연락 |
  | MALFUNCTION_EV | CRITICAL | EV 시스템 이상 | 즉시 서비스센터 연락 |
  | MALFUNCTION_ABS_SYSTEM | WARNING | ABS 시스템 점검 필요 | 가까운 서비스센터 방문 |
  | MALFUNCTION_ESP | WARNING | ESP 시스템 점검 필요 | 가까운 서비스센터 방문 |
  | MALFUNCTION_EPS | WARNING | 전동 파워 스티어링 이상 | 주행 주의, 서비스센터 방문 |
  | MALFUNCTION_TYRE_PRESSURE | WARNING | 타이어 공기압 이상 | 주행 전 타이어 확인 |
  | MALFUNCTION_QUICK_AIR_LEAK | WARNING | 타이어 급속 공기 누출 | 즉시 정차 및 타이어 교체 |
  | MALFUNCTION_CHARGING_SYSTEM | WARNING | 충전 시스템 점검 필요 | 서비스센터 방문 |
  | MALFUNCTION_PARKING_BRAKE | WARNING | 파킹 브레이크 이상 | 주행 주의, 서비스센터 방문 |
  | MALFUNCTION_ELECTRIC_PARKING_BRAKE | WARNING | 전자식 파킹 브레이크 이상 | 서비스센터 방문 |
  | MALFUNCTION_HEV | WARNING | HEV 시스템 점검 필요 | 서비스센터 방문 |
  | MALFUNCTION_HIGH_WATER_TEMPERATURE | WARNING | 엔진 수온 과열 | 즉시 정차 및 냉각 확인 |
  | MALFUNCTION_MACHINE_OIL_LOW_PRESSURE | WARNING | 엔진 오일 압력 낮음 | 즉시 오일 레벨 확인 |
  | MALFUNCTION_SMART_KEY | INFO | 스마트키 배터리 부족 | 스마트키 배터리 교체 |
  | MALFUNCTION_FRONT_BELT | INFO | 안전벨트 미착용 감지 | 안전벨트를 착용하세요 |
  | MALFUNCTION_SVS | INFO | SVS 점검 필요 | 서비스센터 방문 |
  | MALFUNCTION_INSTRUMENT_DISPLAY | INFO | 계기판 디스플레이 이상 | 서비스센터 방문 |
  | MALFUNCTION_OK | - | 모든 시스템 정상 | - |

### 3-2. 고장 현황 ViewModel / UI
- [x] `ui/malfunction/MalfunctionViewModel.kt` — 23종 5초 간격 폴링, 활성 고장 필터링
- [x] `ui/malfunction/MalfunctionScreen.kt`
  - 활성 고장 없음: 초록 체크 + "모든 시스템 정상"
  - 고장 감지 시: 긴급도별 카드 리스트 (CRITICAL 빨강 / WARNING 주황 / INFO 노랑)
  - 각 카드: 경고등 아이콘 + 한국어 설명 + 권장 조치 + 긴급도 배지
- [x] 고장 감지 시 상태바 알림 (Notification) 발송

---

## Phase 4 — 타이어 대시보드

### 4-1. 데이터
- [x] `ui/tyre/TyreViewModel.kt` — 4개 타이어 실시간 상태 (3초 간격)

### 4-2. 타이어 UI
- [x] `ui/tyre/TyreScreen.kt`
  - 차량 조감도(SVG/Canvas) 위에 4개 타이어 위치별 상태 오버레이
  - 각 타이어 버블: 압력 수치(kPa) + 상태 색상 (정상/과압/저압)
  - 타이어 온도 표시 (정상 / 다소 높음 / 매우 높음)
  - 공기 누출 감지 시 경고 배지 (급속 / 완속)
  - TPMS 시스템 전체 상태 (정상 / 자가진단 중 / 신호 이상 / 고장)
- [x] `ui/tyre/TyreDetailCard.kt` — 개별 타이어 상세 컴포넌트

---

## Phase 5 — 소모품 관리 & 정비 이력

### 5-1. 로컬 DB (Room)
- [x] `data/local/MaintenanceDatabase.kt`
- [x] `data/local/entity/MaintenanceRecord.kt` — 항목, 교체 날짜, 교체 시 주행거리, 메모
- [x] `data/local/dao/MaintenanceDao.kt`
- [x] `data/local/MaintenanceRepositoryImpl.kt`

### 5-2. 소모품 교체 주기 기준
- [x] `domain/maintenance/MaintenanceItemType.kt` — 항목별 교체 주기 정의 (MaintenanceSchedule 통합)

  | 항목 | 교체 주기 | 실시간 상태 API |
  |------|-----------|-----------------|
  | 엔진 오일 | 10,000 km | `EngineDevice.getOilLevel()` |
  | 브레이크액 | 40,000 km | `GearboxDevice.getBrakeFluidLevel()` |
  | 냉각수 | 60,000 km | `EngineDevice.getEngineCoolantLevel()` |
  | 타이어 | 40,000 km | `TyreDevice` 누출 상태 참조 |
  | 에어컨 필터 | 15,000 km | 수동 입력 |
  | 브레이크 패드 | 30,000 km | 수동 입력 |

- [x] `domain/maintenance/MaintenanceRepository.kt` (인터페이스)

### 5-3. 소모품 UI
- [x] `ui/maintenance/MaintenanceViewModel.kt` — 현재 주행거리 기반 D-Day 계산
- [x] `ui/maintenance/MaintenanceScreen.kt`
  - 항목별 카드: 이름 + 마지막 교체 주행거리 + 남은 km / D-Day
  - 교체 임박(3,000km 이내) 주황, 초과 빨강, 여유 초록
  - "+ 교체 기록" → 날짜/주행거리 입력 다이얼로그
- [x] `ui/maintenance/MaintenanceHistoryScreen.kt` — 교체 이력 리스트

---

## Phase 6 — 메인 대시보드 & UX 통합

### 6-1. 메인 대시보드
- [x] `ui/dashboard/DashboardViewModel.kt` — 전체 건강 점수 집계
  - CRITICAL 고장 1개당 -40점 / WARNING -15점 / 소모품 교체 초과 -10점
- [x] `ui/dashboard/DashboardScreen.kt`
  - 차량 건강 점수 (0~100, 원형 게이지 + 색상)
  - 섹션 요약 카드: 고장 현황 / 타이어 / 소모품
  - 각 카드 탭 시 상세 화면 이동

### 6-2. 네비게이션
- [x] Bottom Navigation: 대시보드 / 고장 / 타이어 / 정비
- [x] NavHost + 각 화면 연결

### 6-3. UX 폴리싱
- [x] 큰 터치 타겟 (차량 디스플레이 최적화, min 48dp)
- [x] 건강 점수 게이지 카운트업 애니메이션
- [x] 빈 상태 / 로딩 / API 연결 실패 상태 처리

---

## Phase 7 — 스마트폰 연동 & 푸시 알림

> **아키텍처**: DiLink 앱 → Railway 서버 → Firebase FCM → iOS/Android 컴패니언 앱
> **레포 구조**: `android/` (기존) + `server/` (신규) + `companion/` (신규)
> BYD Status 레포의 Railway+FCM 구성 방식 참고, 이 레포에 신규 구성

### 7-1. DiLink 앱 (android/)
- [ ] `data/network/AlertDispatcher.kt`
  - 고장 감지 이벤트 → Railway 서버 POST /alert
  - 페이로드: `{malfunctionCode, severity, description, timestamp, vin}`
  - 네트워크 실패 시 최대 3회 retry (Exponential Backoff)
  - 동일 코드 1시간 내 재발송 억제
- [ ] `data/network/NetworkStateMonitor.kt`
  - 내장 SIM / 테더링 연결 상태 감지
  - 연결 복구 시 pending 알림 자동 전송
- [ ] `MalfunctionViewModel.kt` 수정 — CRITICAL 감지 시 AlertDispatcher 호출

### 7-2. Railway 서버 (server/)
- [ ] Node.js + Express 프로젝트 셋업
- [ ] `POST /alert` — DiLink로부터 고장 이벤트 수신 → FCM 발송 + DB 저장
- [ ] `GET /alerts` — 컴패니언 앱 알림 이력 조회
- [ ] Firebase Admin SDK 연동 (FCM 발송)
- [ ] 알림 이력 DB (PostgreSQL 또는 SQLite)
- [ ] Railway 배포 설정

### 7-3. 컴패니언 앱 (companion/)
- [ ] 프레임워크 선정 (React Native / Flutter / 네이티브 각각)
- [ ] Firebase FCM 푸시 수신 — 고장 내용 + 권장 조치 표시
- [ ] 알림 이력 조회 화면 (Railway GET /alerts)
- [ ] iOS / Android 동시 지원

---

## 검토 (완료 시 작성)

### Phase 1–6 완료 (2026-09-16)
- Phase 1~6 전 항목 구현 완료
- 실기기 없이 Mock 데이터로 전체 UI 동작 확인 가능
- `:bydauto-stub` 모듈로 컴파일 가능, 실기기 연결 시 jar 교체만으로 전환
