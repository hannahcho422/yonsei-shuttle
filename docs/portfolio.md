# 연세 셔틀 (Yonsei Shuttle)

연세대학교 미래캠퍼스 셔틀버스 웹 서비스  
개인 프로젝트 · 백엔드 및 인프라 전 구간 단독 설계·구현

---

## 프로젝트 개요

- 연세대학교 미래캠퍼스 시내/시외 셔틀버스 운행 정보를 관리하고, 예약 및 시뮬레이션 기반 위치 데이터를 제공하는 풀스택 웹 서비스
- 기존 수업 과제로 하드코딩하여 구현했던 프론트엔드 프로젝트를, 실제 동작 가능한 백엔드 API·DB·인프라까지 포함한 풀스택 시스템으로 확장
- 백엔드(Spring Boot), 프론트엔드(Vue.js), 위치 시뮬레이터(Python), 인프라(Docker/Raspberry Pi/Cloudflare Tunnel) 전 영역 단독 수행
- Raspberry Pi 4에 자체 호스팅하여 Cloudflare Tunnel을 통한 외부 HTTPS 서비스 배포까지 완료

---

## 기술 스택 & 선택 이유

**Backend — Java 21, Spring Boot 4**
- Spring Security + JWT: 사용자/관리자 역할 분리와 stateless 인증을 위해 JWT 기반 무상태 인증 설계
- Spring Data JPA (Hibernate): 13개 도메인 간 복잡한 관계 매핑을 최소한의 보일러플레이트로 표현
- Spring WebSocket (STOMP): 셔틀 실시간 위치를 push 방식으로 전달하기 위한 양방향 통신

**Database — PostgreSQL 16**
- MySQL 대비 ENUM, INTERVAL, TIMESTAMPTZ, VIEW 등 표현력 있는 타입을 제공해 스키마 설계 자유도 확보
- `v_shuttle_current_location` VIEW로 복잡한 최신 위치 조회 로직 단순화

**Cache — Redis 7**
- 셔틀 위치가 수 초 단위로 갱신되는 휘발성 데이터 특성에 맞춰 TTL 5분 설정
- DB 직접 조회 대비 ~1ms 수준의 조회 지연으로 WebSocket push 성능 확보

**Simulator — Python 3.12**
- 실제 GPS 연동 한계를 해결하기 위한 가상 위치 생성기로, 스크립팅과 수학 연산에 적합한 Python 선택

**Infra — Docker Compose, Nginx, Raspberry Pi 4, Cloudflare Tunnel**
- 6개 컨테이너(Nginx/Frontend/Backend/Simulator/Postgres/Redis)를 `docker-compose up` 한 번으로 실행하는 재현 가능한 배포 환경 구성
- 클라우드 비용 없이 실 서비스 운영을 경험하기 위해 Raspberry Pi 4(Ubuntu Server 22.04) 자체 호스팅
- Cloudflare Tunnel로 공유기 포트포워딩 없이 HTTPS 외부 공개 + DDoS 방어 + 0개 외부 포트 노출 보안 구성

---

## 시스템 아키텍처

- **배포 아키텍처**
  - 외부 사용자 → Cloudflare (HTTPS + DDoS 방어) → Cloudflare Tunnel (암호화 터널) → Raspberry Pi 4
  - Pi 내부에서 cloudflared 데몬이 Nginx(80) 로 트래픽 전달, 외부에 노출되는 포트 0개
  - Docker Compose 네트워크 내부: Nginx → Frontend / Backend, Backend → Postgres / Redis, Simulator → Backend

- **Backend 레이어드 구조**
  - Presentation(Controller) → Service → Repository(Spring Data JPA) → Domain(Entity)
  - 횡단 관심사: SecurityConfig(JWT Filter), GlobalExceptionHandler, RedisConfig, WebSocketConfig, CorsConfig, SwaggerConfig

- **실시간 위치 데이터 흐름**
  - Python Simulator → `POST /api/internal/location` (매 3초) → Backend
  - Backend: Redis 캐싱(TTL 5분) + PostgreSQL 이력 저장 + WebSocket STOMP push(`/topic/shuttle-location/{id}`)
  - 사용자 Vue 클라이언트는 해당 토픽을 구독하여 Leaflet 지도 위 실시간 마커 갱신

---

## 백엔드 구조 설계

- **도메인 모델링 (13개 엔티티)**
  - User/Admin: 공통 필드를 `user`에 두고 관리자 전용 정보는 `admin` 테이블로 1:1 확장
  - Shuttle 서브타입: 공통 `shuttle` 테이블 + `city_shuttle` / `intercity_shuttle` 확장(Table Inheritance) 구조로 타입별 추가 속성(평균 속도 등) 분리
  - Route - RouteStop - Stop: 노선별 경유 정류장의 순서(`sequence`)와 출발지 대비 소요 시간(`arrival_time INTERVAL`)을 명시적으로 모델링
  - Reservation: `travel_date` 컬럼을 분리해 반복 운행 시간표에 대해 날짜별 예약이 가능하도록 설계, `UNIQUE (seat_id, schedule_id, travel_date)` 제약으로 중복 예약을 DB 레벨에서 방지

- **API 설계**
  - REST API 약 40개를 Auth / Shuttle / Reservation / Location / Notification / Admin / Internal 7개 도메인으로 분리
  - `/api/admin/**` 은 ROLE_ADMIN 전용, `/api/internal/**` 은 시뮬레이터용 비인증 경로, 그 외는 JWT 필요한 형태로 SecurityConfig에서 일괄 제어
  - Swagger(OpenAPI) 연동으로 모든 엔드포인트 자동 문서화

- **인증/인가**
  - JWT Access Token(1시간) + Refresh Token(7일) 분리 발급
  - `JwtAuthenticationFilter` 를 `UsernamePasswordAuthenticationFilter` 앞에 등록하여 모든 보호 경로에서 토큰 검증
  - `@AuthenticationPrincipal CustomUserDetails` 로 컨트롤러에서 사용자 정보를 자동 주입

- **예외 처리**
  - `ErrorCode` Enum(도메인별 접두어 C/A/U/S/R/L/N)으로 애플리케이션 전역 에러 코드 일원화
  - `@RestControllerAdvice` 기반 `GlobalExceptionHandler` 로 `CustomException`, `@Valid` 검증 실패, 예기치 못한 예외를 통일된 `ApiResponse` 포맷으로 응답

---

## 핵심 기능 설명

- **사용자/관리자 분리 구조의 회원가입·로그인·인증 시스템**
  - BCrypt 비밀번호 해싱 + JWT 기반 stateless 인증
  - Refresh Token을 이용한 Access Token 재발급 API 구현

- **시외 셔틀 예약 도메인**
  - 날짜·스케줄·좌석 조합으로 잔여 좌석을 실시간 조회하는 가용 좌석 API
  - 예약 생성 시 중복 예약 검증(애플리케이션 + DB UNIQUE 제약 이중 방어)
  - 예약 내역 조회·취소 API(본인 소유 검증, 이미 취소된 예약에 대한 재취소 방지)

- **시내/시외 셔틀 노선·시간표·정류장 CRUD**
  - 셔틀 생성 시 타입(CITY/INTERCITY)에 따라 `city_shuttle` 또는 `intercity_shuttle` 확장 테이블을 자동 생성하는 서비스 로직
  - 노선별 정류장을 sequence 오름차순으로 반환하는 조회 API
  - 요일(MON-FRI/SAT/SUN)별 운행 시간표 조회

- **관리자 권한 기반 셔틀 운행 정보 및 공지사항 관리**
  - Spring Security 경로 기반 권한 제어로 `/api/admin/**` 은 ADMIN 역할만 접근
  - 공지사항 CRUD와 셔틀·노선·정류장·시간표 CRUD 분리 구현

- **Python 시뮬레이터 기반 위치 데이터 연동**
  - Backend API를 호출해 시내 셔틀의 노선·정류장 데이터를 로드하고, 두 정류장 사이의 좌표를 시간 기준 선형 보간으로 생성
  - 주기적으로 `POST /api/internal/location` 호출하여 Backend로 좌표 전송
  - 환경변수(`SIMULATION_SPEED_MULTIPLIER`)로 시뮬레이션 시간 가속 지원(테스트 환경에서 10배속 재현)

- **Redis + WebSocket 기반 실시간 위치 서비스**
  - 수신된 좌표를 Redis Hash에 TTL 5분으로 저장하여 조회 부하 최소화, PostgreSQL에는 이력 보존
  - STOMP 프로토콜로 구독 중인 모든 클라이언트에 push

- **정류장별 ETA 계산**
  - Redis에서 현재 좌표 조회 → Haversine 공식으로 노선상 누적 거리 계산 → city_shuttle의 평균 속도(km/h)로 분 단위 ETA 산출

---

## 기술적 고민 & 해결

- **반복 운행 시간표에서 특정 날짜 예약을 어떻게 구분할 것인가**
  - 초기 ERD에는 `reservation.schedule_id` 만 존재하여, 월-금 반복 운행 시간표에서 "이번 주 월요일 예약" 과 "다음 주 월요일 예약" 이 구분되지 않는 논리적 결함 존재
  - `travel_date DATE NOT NULL` 컬럼 추가 및 `UNIQUE (seat_id, schedule_id, travel_date)` 제약 도입으로 동일 좌석·스케줄·날짜의 중복 예약을 DB 레벨에서 차단
  - 변경 사항을 schema.sql, schema.dbml, Mermaid ERD 이미지에 모두 동기화하여 문서 정합성 유지

- **PostgreSQL 네이티브 ENUM과 Java Enum 간 매핑 실패**
  - DB는 `CREATE TYPE shuttle_type AS ENUM ('시내', '시외')` 형태의 한글 ENUM, Java는 영문 Enum(`CITY`, `INTERCITY`)을 사용하고자 하여 `column "type" is of type shuttle_type but expression is of type character varying` 에러 발생
  - 해결: (1) `AttributeConverter` 로 Java Enum ↔ 한글 문자열 변환, (2) `@JdbcTypeCode(SqlTypes.VARCHAR)` + `columnDefinition = "shuttle_type"` 로 스키마 타입 명시, (3) JDBC URL에 `stringtype=unspecified` 추가로 드라이버 레벨 자동 캐스팅 허용
  - 동일 패턴을 `ReservationStatus` 에도 재사용하여 일관성 확보

- **Hibernate 6의 `Duration` 자동 매핑 이슈**
  - PostgreSQL `INTERVAL` 컬럼이 기본적으로 `numeric(21,0)` 으로 매핑되어 스키마 검증 실패
  - `@JdbcTypeCode(SqlTypes.INTERVAL_SECOND)` + `columnDefinition = "interval"` 명시로 INTERVAL 타입 직접 지정

- **Cloudflare Tunnel 배포 후 외부 접속 시 403 Forbidden**
  - 라즈베리파이 배포 후 `https://xxx.trycloudflare.com` 로 접속 시 로그인 API 가 403 응답
  - 원인: `CorsConfig.setAllowedOrigins()` 에 `localhost` 만 등록되어 있었고, 매 재시작마다 URL이 바뀌는 Quick Tunnel 특성으로 정적 매칭 불가
  - 해결: `setAllowedOrigins` → `setAllowedOriginPatterns` 전환 후 `https://*.trycloudflare.com` 패턴 추가, WebSocketConfig 에도 동일 적용. 이후 환경별 CORS 설정을 `application-dev.yml` / `application-prod.yml` 로 분리 설계

- **Docker Compose 기반 재현 가능한 배포 환경 구축**
  - 6개 컨테이너(Nginx, Frontend, Backend, Simulator, PostgreSQL, Redis)를 단일 `docker-compose.yml` 로 오케스트레이션
  - 서비스 간 통신은 서비스명(`postgres`, `redis`, `backend`)을 호스트명으로 사용하여 환경별 URL 하드코딩 제거
  - 외부 공개 포트는 Nginx(80) 1개로 제한하고 나머지는 `expose` 로 내부 네트워크에서만 접근 가능하게 구성

- **Raspberry Pi 자체 호스팅과 외부 공개의 보안 균형**
  - 초기에는 공유기 포트포워딩을 고려했으나 포트 노출로 인한 보안 리스크 존재
  - Cloudflare Tunnel 도입으로 Pi는 outbound 연결만 수립, 외부 진입 포트 0개 유지
  - systemd 서비스 등록(`cloudflared.service`)으로 재부팅 시 Tunnel + Docker Compose 자동 시작
