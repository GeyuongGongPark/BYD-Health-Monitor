# BYD Health Monitor — 구현 플랜

> 작성일: 2026-09-16
> 현황: 코드 없음, 문서만 존재 (CLAUDE.md, todo.md, BYD_OpenAPI.md)
> 시작점: Phase 1 — 프로젝트 셋업부터 전체 신규 구현

---

## 기술 스택

| 영역 | 선택 |
|------|------|
| 언어 | Kotlin |
| UI | Jetpack Compose (Material3, 다크 스킴) |
| DI | Hilt |
| DB | Room |
| 비동기 | Coroutines + Flow |
| 아키텍처 | Clean Architecture (data / domain / ui) |
| 빌드 | Gradle Kotlin DSL + Version Catalog |
| OpenAPI | DexClassLoader + Reflection (compileOnly stub 병행) |

---

## Phase 1 — 프로젝트 셋업

```
1. gradle/libs.versions.toml (Compose BOM, Hilt, Room, Coroutines 버전 고정)
2. settings.gradle.kts / build.gradle.kts (root)
3. app/build.gradle.kts (compileOnly bydauto-openapi.jar 포함)
4. app/proguard-rules.pro (Reflection 대상 클래스 keep 규칙 필수)
5. AndroidManifest.xml (Landscape 고정, 필요 권한 선언)
6. HealthMonitorApplication.kt + MainActivity.kt
7. ui/theme/Color.kt / Theme.kt / Type.kt
```

> ⚠️ proguard에서 `android.hardware.bydauto.**` keep 필수 — 없으면 Reflection 실패

---

## Phase 2 — BYD OpenAPI 연동 (가장 위험 구간)

```
1. libs/bydauto-openapi.jar 배치
2. BydApiLoader.kt
   - DexClassLoader로 시스템 APK 로드 (경로: /system/app/BYDAutoService/, 3.0/5.0 분기)
   - getInstance(Context) Reflection 체인
   - 실패 시 MockData fallback (에뮬레이터/일반 기기 테스트용)
3. Repository 4종
   - MalfunctionRepository  → InstrumentDevice.getMalfunctionInfo()
   - TyreRepository         → TyreDevice 4개 타이어
   - StatisticRepository    → StatisticDevice (주행거리, 배터리%)
   - ComponentRepository    → EngineDevice + GearboxDevice 소모품 레벨
4. 도메인 모델 3종: MalfunctionInfo, TyreStatus, VehicleHealth
```

> ⚠️ 실기기 없이는 검증 불가 → BydApiLoader에 Mock 분기 처음부터 내장

---

## Phase 3 — 고장 코드 번역기

```
1. MalfunctionCatalog.kt — 23종 상수 → 설명/긴급도/권장조치 매핑
2. MalfunctionViewModel.kt — 5초 간격 폴링, StateFlow로 활성 고장 필터링
3. MalfunctionScreen.kt
   - 정상 상태: 초록 체크 카드
   - 고장 시: CRITICAL(빨강) / WARNING(주황) / INFO(노랑) 카드 리스트
   - 각 카드: 아이콘 + 한국어 설명 + 권장 조치 + 긴급도 배지
4. Notification — 고장 감지 시 상태바 알림
```

---

## Phase 4 — 타이어 대시보드

```
1. TyreViewModel.kt — 3초 간격, 4개 타이어 StateFlow
2. TyreScreen.kt
   - Canvas로 차량 조감도 드로잉
   - 타이어 위치별 버블 오버레이 (압력 kPa + 상태색)
   - 온도 / 공기 누출 / TPMS 상태 표시
3. TyreDetailCard.kt — 개별 타이어 상세 컴포넌트
```

---

## Phase 5 — 소모품 관리

```
1. Room 셋업: MaintenanceDatabase, MaintenanceRecord entity, MaintenanceDao
2. MaintenanceRepositoryImpl.kt (실시간 레벨 API + Room 이력 통합)
3. MaintenanceSchedule.kt — 항목별 교체 주기 정의
4. MaintenanceViewModel.kt — 현재 주행거리 기반 D-Day 계산
5. MaintenanceScreen.kt — 항목 카드 (남은 km / 색상 상태)
6. 교체 기록 입력 다이얼로그
7. MaintenanceHistoryScreen.kt
```

---

## Phase 6 — 메인 대시보드 & 네비게이션

```
1. DashboardViewModel.kt — 건강 점수 집계 (CRITICAL -40 / WARNING -15 / 소모품초과 -10)
2. DashboardScreen.kt
   - 원형 게이지 (건강 점수 0~100, 카운트업 애니메이션)
   - 섹션 요약 카드 3종 (고장 / 타이어 / 소모품)
3. NavHost + Bottom Navigation (대시보드 / 고장 / 타이어 / 정비)
4. UX 폴리싱: 터치타겟 48dp+, 빈 상태 / 로딩 / API 실패 처리
```

---

## Phase 7 — 스마트폰 연동 & 푸시 알림

### 확정된 아키텍처

```
DiLink 앱 ──HTTP POST──▶ Railway 서버 ──▶ Firebase FCM ──▶ iOS / Android 컴패니언 앱
           (이 레포)      (이 레포, 신규 구성)                  (이 레포)
                          BYD Status 방식 참고
```

- **인터넷 연결**: 내장 SIM + 테더링 둘 다 지원 (연결 상태 감지 후 전송)
- **서버**: 이 레포에 Railway 서버 신규 구성 (BYD Status 구성 방식만 참고)
- **푸시**: Firebase FCM (프로젝트 신규 생성)
- **컴패니언 앱**: iOS + Android, 이 레포에서 함께 개발

### 레포 구조 (전체)
```
BYD-Health-Monitor/
├── android/          — DiLink 앱 (Phase 1~6)
├── server/           — Railway 서버 (Phase 7)
└── companion/        — 컴패니언 앱 iOS + Android (Phase 7)
```

### DiLink 앱 구현 항목 (android/)
```
- AlertDispatcher.kt — 고장 감지 이벤트 → Railway 서버 HTTP POST
  - 전송 페이로드: {malfunctionCode, severity, description, timestamp, vin}
  - 재시도 로직: 네트워크 실패 시 최대 3회 retry (Exponential Backoff)
  - 중복 알림 방지: 동일 코드 1시간 내 재발송 억제
- NetworkStateMonitor.kt — 내장 SIM / 테더링 연결 상태 감지
```

### Railway 서버 (server/)
```
- POST /alert — DiLink 앱으로부터 고장 이벤트 수신
  - Firebase Admin SDK로 FCM 발송
  - 알림 이력 DB 저장 (컴패니언 앱 조회용)
- GET /alerts — 컴패니언 앱 알림 이력 조회
```

### 컴패니언 앱 (companion/)
```
- iOS + Android 동시 지원
- FCM 푸시 수신 → 고장 내용 + 권장 조치 표시
- 정비 이력 / 알림 이력 조회 (Railway API 연동)
```

---

## 구현 순서 원칙

1. **Phase 1 → 2 → 3 직렬 진행** — OpenAPI 연동 검증 후 상위 레이어 구현
2. **BydApiLoader Mock 먼저** — 실기기 없이 UI 개발 가능하도록
3. **Phase 4, 5는 Phase 3 완료 후 병렬 가능**
4. **Phase 6은 최후** — 각 화면 완성 후 대시보드 집계 가능
