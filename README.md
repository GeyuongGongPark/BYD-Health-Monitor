# BYD Health Monitor

BYD 전기차(DiLink 탑재 모델)용 차량 건강 모니터링 시스템.
차량 내장 디스플레이 앱과 iOS / Android 컴패니언 앱으로 구성됩니다.

---

## 동작 방식

```
┌─────────────────────────────┐
│  BYD DiLink (차량 헤드유닛)     │
│  android/ 앱                 │
│  · HAL 센서 실시간 감지         │
│  · 고장 코드 23종 분석          │
│  · CRITICAL 감지 시 전송       │
└────────────┬────────────────┘
             │  HTTP POST /alert
             ▼
┌─────────────────────────────┐
│  Railway 서버                │
│  server/                    │
│  · 알림 이력 저장 (PostgreSQL) │
│  · iOS  → APNs push         │
│  · Android → FCM push       │
└────────┬──────────┬─────────┘
         │          │
         ▼          ▼
   📱 iPhone    📱 Android 폰
   companion/   companion/
   APNs 알림    FCM 알림
   이력 조회    이력 조회
```

---

## 앱 구성

### 1. 차량용 앱 (`android/`)

BYD DiLink 헤드유닛에 설치하는 메인 앱.


| 기능          | 설명                                         |
| ----------- | ------------------------------------------ |
| 고장 현황       | 23종 고장 코드 한국어 번역, CRITICAL/WARNING/INFO 분류 |
| 타이어 대시보드    | 4개 타이어 압력(kPa)/온도/TPMS 실시간 표시              |
| 소모품 관리      | 6종 소모품 교체 주기 추적 및 이력 기록                    |
| 차량 건강 점수    | 0\~100점 원형 게이지                             |
| CRITICAL 알림 | 긴급 고장 감지 시 서버로 push 요청 전송                  |


**기술 스택**: Kotlin · Jetpack Compose · Hilt · Room · Coroutines + Flow

### 2. 중계 서버 (`server/`)

Railway에 배포된 Node.js/Express 서버.


| 엔드포인트                | 역할                                       |
| -------------------- | ---------------------------------------- |
| `POST /alert`        | DiLink로부터 고장 알림 수신 후 iOS/Android push 전송 |
| `GET /alerts`        | 컴패니언 앱용 알림 이력 조회                         |
| `POST /api/register` | FCM/APNs 토큰 등록                           |
| `GET /health`        | 헬스체크                                     |


**기술 스택**: Node.js · Express · PostgreSQL · Firebase Admin SDK · http2 (APNs JWT)
**배포**: `https://byd-server-production.up.railway.app`

### 3. 컴패니언 앱 (`companion/`)

운전자 스마트폰에 설치하는 Flutter 앱 (iOS / Android).


| 기능         | 설명                      |
| ---------- | ----------------------- |
| Push 알림 수신 | CRITICAL 고장 발생 시 즉시 알림  |
| 알림 이력 조회   | 서버에서 과거 알림 이력 확인        |
| iOS        | APNs 기반 push (백그라운드 포함) |
| Android    | FCM 기반 push (GMS 필요)    |


**기술 스택**: Flutter · firebase\_messaging · flutter\_local\_notifications · Riverpod

> **주의**: BYD DiLink는 GMS 미탑재 환경입니다. 컴패니언 앱은 운전자의 **개인 스마트폰**에 설치해야 push를 수신할 수 있습니다.

---

## 프로젝트 구조

```
BYD-Health-Monitor/
├── android/                        — DiLink 차량용 앱
│   └── src/main/java/com/bydhealth/monitor/
│       ├── data/
│       │   ├── local/              — Room DB (정비 이력)
│       │   ├── network/            — AlertDispatcher, NetworkStateMonitor
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
├── server/                         — Railway 중계 서버
│   └── src/
│       ├── index.js                — Express 라우터
│       ├── db.js                   — PostgreSQL (device_tokens, alert_history)
│       ├── apns.js                 — HTTP/2 JWT APNs
│       └── fcm.js                  — Firebase Admin FCM
├── companion/                      — Flutter 컴패니언 앱 (iOS/Android)
│   └── lib/
│       ├── main.dart               — Firebase 초기화 + 토큰 등록
│       ├── models/alert.dart       — Alert 모델
│       ├── screens/                — AlertListScreen
│       └── services/               — ApiService, NotificationService
├── docs/                           — GitHub Pages 랜딩 페이지
└── tasks/                          — 개발 문서 (todo.md, lessons.md)
```

---

## BYD OpenAPI 연동

실기기(DiLink)에서는 시스템이 `android.hardware.bydauto.*` 클래스를 제공함.
개발 환경에서는 `:bydauto-stub` 모듈로 컴파일하고, `NoClassDefFoundError` 발생 시 Mock 데이터로 자동 전환.

**실기기 전환 방법:**

```kotlin
// android/build.gradle.kts
compileOnly(files("libs/bydauto-openapi.jar"))  // stub 대신 실제 jar
```

---

## 설치

### 차량용 앱 (DiLink)

```bash
git clone https://github.com/GeyuongGongPark/BYD-Health-Monitor.git
cd BYD-Health-Monitor

# Wi-Fi ADB 연결 후 설치
adb connect <차량_IP>:5555
./gradlew :android:installDebug
```

### 컴패니언 앱 (스마트폰)

```bash
cd companion

# iOS (Xcode 서명 설정 필요)
flutter run -d <iPhone_기기_ID>

# Android
flutter build apk --release
adb install build/app/outputs/flutter-apk/app-release.apk
```

---

## 플랫폼 요구사항


| 구성 요소        | 요구 사항                                         |
| ------------ | --------------------------------------------- |
| DiLink 앱     | BYD DiLink 탑재 차량, Android API 23+, GMS 불필요    |
| 서버           | Node.js 18+, PostgreSQL                       |
| iOS 컴패니언     | iOS 15+, Apple Developer 계정 (APNs)            |
| Android 컴패니언 | Android API 23+, GMS(Google Play Services) 필요 |


