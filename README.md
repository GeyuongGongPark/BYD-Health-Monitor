# BYD Health Monitor

BYD 전기차(DiLink 탑재 모델)용 차량 건강 모니터링 앱.
고장 코드 23종, 타이어 상태, 소모품 관리를 한눈에 확인할 수 있는 차량 내장 디스플레이 전용 앱.

---

## 주요 기능

| 기능 | 설명 |
|------|------|
| 고장 현황 | 23종 고장 코드를 한국어로 번역, 긴급도(CRITICAL/WARNING/INFO)별 분류 |
| 타이어 대시보드 | 4개 타이어 압력(kPa)/온도/공기누출/TPMS 상태 실시간 표시 |
| 소모품 관리 | 엔진오일·브레이크액 등 6종 교체 주기 추적, 교체 이력 기록 |
| 차량 건강 점수 | 0~100점 원형 게이지 (CRITICAL -40 / WARNING -15 / 소모품초과 -10) |
| 알림 | CRITICAL 고장 감지 시 상태바 푸시 알림 |

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
| BYD OpenAPI | compileOnly stub 모듈 + NoClassDefFoundError Mock fallback |

---

## 프로젝트 구조

```
BYD-Health-Monitor/
├── android/                        — DiLink 앱 (Phase 1–6, 완료)
│   └── src/main/java/com/bydhealth/monitor/
│       ├── data/
│       │   ├── local/              — Room DB (정비 이력)
│       │   └── vehicle/            — BYD OpenAPI Repository + Mock
│       ├── domain/
│       │   ├── malfunction/        — 고장 코드 카탈로그
│       │   ├── maintenance/        — 소모품 교체 주기 정의
│       │   └── model/              — 도메인 모델
│       └── ui/
│           ├── dashboard/          — 메인 대시보드
│           ├── malfunction/        — 고장 현황
│           ├── tyre/               — 타이어 대시보드
│           ├── maintenance/        — 소모품 관리
│           ├── navigation/         — NavHost + Bottom Navigation
│           └── theme/              — BYD 다크 팔레트
├── bydauto-stub/                   — BYD OpenAPI compileOnly stub 모듈
├── server/                         — Railway 서버 (Phase 7, 예정)
├── companion/                      — iOS/Android 컴패니언 앱 (Phase 7, 예정)
└── tasks/                          — 개발 문서 (plan.md, todo.md, lessons.md)
```

---

## BYD OpenAPI 연동

실기기(DiLink)에서는 시스템이 `android.hardware.bydauto.*` 클래스를 제공함.
개발 환경에서는 `:bydauto-stub` 모듈로 컴파일하고, `NoClassDefFoundError` 발생 시 Mock 데이터로 자동 전환.

**실기기 전환 방법:**
1. ADB로 실기기에서 jar 추출 후 `android/libs/bydauto-openapi.jar` 배치
2. `android/build.gradle.kts`에서 한 줄 교체:
   ```kotlin
   // 변경 전
   compileOnly(project(":bydauto-stub"))
   // 변경 후
   compileOnly(files("libs/bydauto-openapi.jar"))
   ```

---

## 플랫폼 요구사항

- **대상**: BYD DiLink (차량 내장 Android 헤드유닛)
- **화면**: 가로 고정 (Landscape), 전체화면
- **GMS**: 미탑재 환경 지원 (Firebase 등 GMS 의존 라이브러리 미사용)
- **최소 SDK**: API 23

---

## Phase 7 — 스마트폰 연동 (예정)

```
DiLink 앱 ──HTTP POST──▶ Railway 서버 ──▶ Firebase FCM ──▶ iOS / Android 컴패니언 앱
```

- 고장 감지 시 스마트폰으로 푸시 알림 전송
- 컴패니언 앱에서 알림 이력 및 정비 이력 조회
- DiLink에 GMS가 없으므로 FCM 발송은 Railway 서버(Firebase Admin SDK)에서 처리
