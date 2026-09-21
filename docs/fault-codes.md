# BYD Health Monitor — 고장 코드 목록

BYD HAL OpenAPI(`BYDAutoInstrumentDevice`) 기반으로 감지하는 22종 고장 코드입니다.
소스: `android/src/main/java/com/bydhealth/monitor/domain/malfunction/MalfunctionCatalog.kt`

---

## 심각도 기준

| 심각도 | 설명 | 건강 점수 감점 |
|---|---|---|
| 🔴 CRITICAL | 즉시 조치 필요. 주행 중단 권장 | **−40점** |
| 🟠 WARNING | 조기 점검 필요 | **−15점** |
| 🔵 INFO | 참고 수준 알림 | 감점 없음 |

---

## 전체 코드 목록

| # | BYDAutoInstrumentDevice 상수 | 한국어 이름 | 심각도 | 권장 조치 |
|---|---|---|---|---|
| 1 | `MALFUNCTION_ENGINE` | 엔진 이상 감지 | 🔴 CRITICAL | 즉시 안전한 곳에 정차, 서비스센터 연락 |
| 2 | `MALFUNCTION_SRS` | 에어백(SRS) 시스템 이상 | 🔴 CRITICAL | 즉시 점검 필요 |
| 3 | `MALFUNCTION_HIGH_MOTOR_TEMPERATURE` | 모터 과열 | 🔴 CRITICAL | 즉시 정차 및 냉각 |
| 4 | `MALFUNCTION_BATTERY` | 고전압 배터리 이상 | 🔴 CRITICAL | 즉시 BYD 서비스센터 연락 |
| 5 | `MALFUNCTION_HIGH_BATTERY_TEMPERATURE` | 배터리 과열 | 🔴 CRITICAL | 즉시 정차 및 냉각 |
| 6 | `MALFUNCTION_POWER_SYSTEM` | 동력 시스템 이상 | 🔴 CRITICAL | 즉시 서비스센터 연락 |
| 7 | `MALFUNCTION_EV` | EV 시스템 이상 | 🔴 CRITICAL | 즉시 서비스센터 연락 |
| 8 | `MALFUNCTION_MACHINE_OIL_LOW_PRESSURE` | 엔진 오일 압력 낮음 | 🟠 WARNING | 즉시 오일 레벨 확인 |
| 9 | `MALFUNCTION_PARKING_BRAKE` | 파킹 브레이크 이상 | 🟠 WARNING | 주행 주의, 서비스센터 방문 |
| 10 | `MALFUNCTION_CHARGING_SYSTEM` | 충전 시스템 점검 필요 | 🟠 WARNING | 서비스센터 방문 |
| 11 | `MALFUNCTION_ABS_SYSTEM` | ABS 시스템 점검 필요 | 🟠 WARNING | 가까운 서비스센터 방문 |
| 12 | `MALFUNCTION_ESP` | ESP 시스템 점검 필요 | 🟠 WARNING | 가까운 서비스센터 방문 |
| 13 | `MALFUNCTION_QUICK_AIR_LEAK` | 타이어 급속 공기 누출 | 🟠 WARNING | 즉시 정차 및 타이어 교체 |
| 14 | `MALFUNCTION_HIGH_WATER_TEMPERATURE` | 엔진 수온 과열 | 🟠 WARNING | 즉시 정차 및 냉각 확인 |
| 15 | `MALFUNCTION_ELECTRIC_PARKING_BRAKE` | 전자식 파킹 브레이크 이상 | 🟠 WARNING | 서비스센터 방문 |
| 16 | `MALFUNCTION_EPS` | 전동 파워 스티어링 이상 | 🟠 WARNING | 주행 주의, 서비스센터 방문 |
| 17 | `MALFUNCTION_TYRE_PRESSURE` | 타이어 공기압 이상 | 🟠 WARNING | 주행 전 타이어 확인 |
| 18 | `MALFUNCTION_HEV` | HEV 시스템 점검 필요 | 🟠 WARNING | 서비스센터 방문 |
| 19 | `MALFUNCTION_INSTRUMENT_DISPLAY` | 계기판 디스플레이 이상 | 🔵 INFO | 서비스센터 방문 |
| 20 | `MALFUNCTION_SVS` | SVS 점검 필요 | 🔵 INFO | 서비스센터 방문 |
| 21 | `MALFUNCTION_SMART_KEY` | 스마트키 배터리 부족 | 🔵 INFO | 스마트키 배터리 교체 |
| 22 | `MALFUNCTION_FRONT_BELT` | 안전벨트 미착용 감지 | 🔵 INFO | 안전벨트를 착용하세요 |

---

## 심각도별 요약

| 심각도 | 코드 수 |
|---|---|
| 🔴 CRITICAL | 7개 |
| 🟠 WARNING | 11개 |
| 🔵 INFO | 4개 |
| **합계** | **22개** |

---

> **참고**: 랜딩 페이지에 "23종"으로 표기되어 있으나 실제 구현은 22종입니다. 추가 코드 확보 시 업데이트 예정.
