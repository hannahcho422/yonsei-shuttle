-- ============================================================
-- 연세 셔틀 (Yonsei Shuttle) - PostgreSQL ERD
-- 기존 MySQL ERD에서 마이그레이션 + 개선 사항 반영
-- ============================================================

-- 기존 스키마 정리
DROP SCHEMA IF EXISTS yonsei_shuttle CASCADE;
CREATE SCHEMA yonsei_shuttle;
SET search_path TO yonsei_shuttle;

-- ============================================================
-- ENUM 타입 정의
-- ============================================================
CREATE TYPE shuttle_type      AS ENUM ('시내', '시외');
CREATE TYPE reservation_status AS ENUM ('예약', '취소');
CREATE TYPE user_role          AS ENUM ('USER', 'ADMIN');

-- ============================================================
-- 1. user (사용자)
-- [개선] Admin_admin_id 순환 참조 제거
-- [개선] is_admin TINYINT → role ENUM('USER','ADMIN') 으로 변경
-- ============================================================
CREATE TABLE "user" (
    user_id    SERIAL       PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       user_role    NOT NULL DEFAULT 'USER',
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

COMMENT ON TABLE  "user"          IS '사용자 (일반 + 관리자 통합)';
COMMENT ON COLUMN "user".user_id  IS '사용자 고유 ID';
COMMENT ON COLUMN "user".name     IS '사용자 이름';
COMMENT ON COLUMN "user".email    IS '사용자 이메일 (로그인 ID)';
COMMENT ON COLUMN "user".password IS '암호화된 비밀번호 (BCrypt)';
COMMENT ON COLUMN "user".role     IS 'USER: 일반 사용자, ADMIN: 관리자';

-- ============================================================
-- 2. admin (관리자 확장 정보)
-- [개선] user → admin 단방향 참조만 유지
-- ============================================================
CREATE TABLE admin (
    admin_id   SERIAL  PRIMARY KEY,
    user_id    INT     NOT NULL UNIQUE,

    CONSTRAINT fk_admin_user
        FOREIGN KEY (user_id) REFERENCES "user" (user_id)
        ON DELETE CASCADE
);

COMMENT ON TABLE  admin         IS '관리자 확장 정보 (user 1:1)';
COMMENT ON COLUMN admin.user_id IS '연결된 사용자 ID';

-- ============================================================
-- 3. notification (공지사항)
-- [개선] admin_id UNIQUE 제거 → 관리자 1명이 여러 공지 작성 가능 (1:N)
-- [개선] MEDIUMTEXT → TEXT
-- ============================================================
CREATE TABLE notification (
    notification_id SERIAL       PRIMARY KEY,
    admin_id        INT          NOT NULL,
    title           VARCHAR(255) NOT NULL,
    content         TEXT         NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_notification_admin
        FOREIGN KEY (admin_id) REFERENCES admin (admin_id)
        ON DELETE CASCADE
);

COMMENT ON TABLE  notification              IS '공지사항';
COMMENT ON COLUMN notification.title        IS '공지 제목';
COMMENT ON COLUMN notification.content      IS '공지 내용';
COMMENT ON COLUMN notification.created_at   IS '작성일시';

CREATE INDEX idx_notification_admin    ON notification (admin_id);
CREATE INDEX idx_notification_created  ON notification (created_at DESC);

-- ============================================================
-- 4. shuttle (셔틀 버스)
-- ============================================================
CREATE TABLE shuttle (
    shuttle_id SERIAL       PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    type       shuttle_type NOT NULL,
    capacity   INT          NOT NULL DEFAULT 45
);

COMMENT ON TABLE  shuttle          IS '셔틀 버스';
COMMENT ON COLUMN shuttle.type     IS '시내 / 시외 구분';
COMMENT ON COLUMN shuttle.capacity IS '좌석 수';

-- ============================================================
-- 5. city_shuttle (시내 셔틀 확장)
-- ============================================================
CREATE TABLE city_shuttle (
    city_shuttle_id SERIAL        PRIMARY KEY,
    shuttle_id      INT           NOT NULL UNIQUE,
    speed           NUMERIC(5,2)  NOT NULL DEFAULT 40.00,

    CONSTRAINT fk_city_shuttle
        FOREIGN KEY (shuttle_id) REFERENCES shuttle (shuttle_id)
        ON DELETE CASCADE
);

COMMENT ON TABLE  city_shuttle       IS '시내 셔틀 (위치 시뮬레이션 대상)';
COMMENT ON COLUMN city_shuttle.speed IS '평균 운행 속도 (km/h)';

-- ============================================================
-- 6. intercity_shuttle (시외 셔틀 확장)
-- ============================================================
CREATE TABLE intercity_shuttle (
    intercity_shuttle_id SERIAL PRIMARY KEY,
    shuttle_id           INT    NOT NULL UNIQUE,

    CONSTRAINT fk_intercity_shuttle
        FOREIGN KEY (shuttle_id) REFERENCES shuttle (shuttle_id)
        ON DELETE CASCADE
);

COMMENT ON TABLE intercity_shuttle IS '시외 셔틀 (예약 대상)';

-- ============================================================
-- 7. route (노선)
-- [개선] city_shuttle 전용 → shuttle 전체에서 참조 가능하도록 변경
-- ============================================================
CREATE TABLE route (
    route_id   SERIAL       PRIMARY KEY,
    shuttle_id INT          NOT NULL,
    route_name VARCHAR(255) NOT NULL,
    direction  VARCHAR(50),

    CONSTRAINT fk_route_shuttle
        FOREIGN KEY (shuttle_id) REFERENCES shuttle (shuttle_id)
        ON DELETE CASCADE
);

COMMENT ON TABLE  route            IS '셔틀 노선';
COMMENT ON COLUMN route.route_name IS '노선 이름 (예: 원주역 순환)';
COMMENT ON COLUMN route.direction  IS '방향 (왕복 구분: 등교/하교 등)';

CREATE INDEX idx_route_shuttle ON route (shuttle_id);

-- ============================================================
-- 8. stop (정류장)
-- [개선] time FK 제거 → 정류장은 독립 엔티티로 분리
-- ============================================================
CREATE TABLE stop (
    stop_id    SERIAL         PRIMARY KEY,
    stop_name  VARCHAR(255)   NOT NULL,
    latitude   NUMERIC(10,7)  NOT NULL,
    longitude  NUMERIC(10,7)  NOT NULL,
    image_path VARCHAR(500)
);

COMMENT ON TABLE  stop            IS '정류장';
COMMENT ON COLUMN stop.stop_name  IS '정류장 이름';
COMMENT ON COLUMN stop.latitude   IS '위도';
COMMENT ON COLUMN stop.longitude  IS '경도';
COMMENT ON COLUMN stop.image_path IS '정류장 이미지 경로';

-- ============================================================
-- 9. route_stop (노선-정류장 매핑)
-- [개선] PK 추가 (route_stop_id)
-- [개선] sequence 컬럼 추가 (정렬 순서)
-- [개선] arrival_time INT → INTERVAL 으로 변경
-- ============================================================
CREATE TABLE route_stop (
    route_stop_id SERIAL  PRIMARY KEY,
    route_id      INT     NOT NULL,
    stop_id       INT     NOT NULL,
    sequence      INT     NOT NULL,
    arrival_time  INTERVAL NOT NULL DEFAULT '0 minutes',
    prev_stop_id  INT,
    next_stop_id  INT,

    CONSTRAINT fk_rs_route FOREIGN KEY (route_id)
        REFERENCES route (route_id) ON DELETE CASCADE,
    CONSTRAINT fk_rs_stop FOREIGN KEY (stop_id)
        REFERENCES stop (stop_id) ON DELETE CASCADE,
    CONSTRAINT fk_rs_prev FOREIGN KEY (prev_stop_id)
        REFERENCES stop (stop_id) ON DELETE SET NULL,
    CONSTRAINT fk_rs_next FOREIGN KEY (next_stop_id)
        REFERENCES stop (stop_id) ON DELETE SET NULL,

    CONSTRAINT uq_route_stop UNIQUE (route_id, stop_id),
    CONSTRAINT uq_route_seq  UNIQUE (route_id, sequence)
);

COMMENT ON TABLE  route_stop              IS '노선별 경유 정류장';
COMMENT ON COLUMN route_stop.sequence     IS '정류장 순서 (1, 2, 3...)';
COMMENT ON COLUMN route_stop.arrival_time IS '출발지 대비 소요 시간';

CREATE INDEX idx_rs_route ON route_stop (route_id, sequence);

-- ============================================================
-- 10. schedule (운행 시간표)
-- [개선] departure_time INT → TIME 타입으로 변경
-- [개선] day_of_week 추가 (요일별 운행 구분)
-- ============================================================
CREATE TABLE schedule (
    schedule_id    SERIAL      PRIMARY KEY,
    route_id       INT         NOT NULL,
    departure_time TIME        NOT NULL,
    day_of_week    VARCHAR(10) NOT NULL DEFAULT 'MON-FRI',

    CONSTRAINT fk_schedule_route
        FOREIGN KEY (route_id) REFERENCES route (route_id)
        ON DELETE CASCADE
);

COMMENT ON TABLE  schedule                IS '운행 시간표';
COMMENT ON COLUMN schedule.departure_time IS '출발 시각';
COMMENT ON COLUMN schedule.day_of_week    IS '운행 요일 (MON-FRI, SAT, SUN 등)';

CREATE INDEX idx_schedule_route ON schedule (route_id);
CREATE INDEX idx_schedule_time  ON schedule (departure_time);

-- ============================================================
-- 11. seat (좌석 - 시외 셔틀 전용)
-- ============================================================
CREATE TABLE seat (
    seat_id              SERIAL PRIMARY KEY,
    intercity_shuttle_id INT    NOT NULL,
    seat_num             INT    NOT NULL,

    CONSTRAINT fk_seat_intercity
        FOREIGN KEY (intercity_shuttle_id)
        REFERENCES intercity_shuttle (intercity_shuttle_id)
        ON DELETE CASCADE,

    CONSTRAINT uq_seat UNIQUE (intercity_shuttle_id, seat_num)
);

COMMENT ON TABLE  seat          IS '시외 셔틀 좌석';
COMMENT ON COLUMN seat.seat_num IS '좌석 번호';

-- ============================================================
-- 12. reservation (예약 - 시외 셔틀 전용)
-- [개선] user_id UNIQUE 제거 → 사용자 여러 건 예약 가능
-- [개선] cancel_date NOT NULL 제거 → 취소 시에만 기록
-- ============================================================
CREATE TABLE reservation (
    reservation_id       SERIAL             PRIMARY KEY,
    user_id              INT                NOT NULL,
    intercity_shuttle_id INT                NOT NULL,
    seat_id              INT                NOT NULL,
    schedule_id          INT                NOT NULL,
    status               reservation_status NOT NULL DEFAULT '예약',
    reserved_at          TIMESTAMPTZ        NOT NULL DEFAULT now(),
    cancelled_at         TIMESTAMPTZ,

    CONSTRAINT fk_reservation_user FOREIGN KEY (user_id)
        REFERENCES "user" (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_reservation_intercity FOREIGN KEY (intercity_shuttle_id)
        REFERENCES intercity_shuttle (intercity_shuttle_id) ON DELETE CASCADE,
    CONSTRAINT fk_reservation_seat FOREIGN KEY (seat_id)
        REFERENCES seat (seat_id) ON DELETE CASCADE,
    CONSTRAINT fk_reservation_schedule FOREIGN KEY (schedule_id)
        REFERENCES schedule (schedule_id) ON DELETE CASCADE
);

COMMENT ON TABLE  reservation               IS '시외 셔틀 예약';
COMMENT ON COLUMN reservation.status        IS '예약 / 취소';
COMMENT ON COLUMN reservation.reserved_at   IS '예약 일시';
COMMENT ON COLUMN reservation.cancelled_at  IS '취소 일시 (취소 시에만 기록)';

CREATE INDEX idx_reservation_user     ON reservation (user_id);
CREATE INDEX idx_reservation_shuttle  ON reservation (intercity_shuttle_id);
CREATE INDEX idx_reservation_schedule ON reservation (schedule_id);
CREATE INDEX idx_reservation_status   ON reservation (status);

-- ============================================================
-- 13. shuttle_location (셔틀 실시간 위치)
-- [개선] 기존 location 테이블을 셔틀과 직접 연결
-- [추가] updated_at으로 최신 위치 추적
-- ============================================================
CREATE TABLE shuttle_location (
    location_id SERIAL        PRIMARY KEY,
    shuttle_id  INT           NOT NULL,
    latitude    NUMERIC(10,7) NOT NULL,
    longitude   NUMERIC(10,7) NOT NULL,
    heading     NUMERIC(5,2),
    speed       NUMERIC(5,2),
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT fk_location_shuttle
        FOREIGN KEY (shuttle_id) REFERENCES shuttle (shuttle_id)
        ON DELETE CASCADE
);

COMMENT ON TABLE  shuttle_location            IS '셔틀 실시간 위치 (시뮬레이터가 주기적 INSERT)';
COMMENT ON COLUMN shuttle_location.heading    IS '진행 방향 (0~360도)';
COMMENT ON COLUMN shuttle_location.speed      IS '현재 속도 (km/h)';
COMMENT ON COLUMN shuttle_location.updated_at IS '좌표 기록 시각';

-- 최신 위치 조회 최적화 인덱스
CREATE INDEX idx_location_shuttle_time ON shuttle_location (shuttle_id, updated_at DESC);

-- ============================================================
-- 최신 위치 조회용 VIEW
-- ============================================================
CREATE VIEW v_shuttle_current_location AS
SELECT DISTINCT ON (shuttle_id)
    location_id,
    shuttle_id,
    latitude,
    longitude,
    heading,
    speed,
    updated_at
FROM shuttle_location
ORDER BY shuttle_id, updated_at DESC;

COMMENT ON VIEW v_shuttle_current_location IS '셔틀별 최신 위치 (1건씩)';

-- ============================================================
-- 변경 이력 요약
-- ============================================================
-- 1. [순환 참조 제거]  user.Admin_admin_id 컬럼 삭제, admin→user 단방향 참조
-- 2. [ENUM 통합]       is_admin TINYINT → role ENUM('USER','ADMIN')
-- 3. [1:N 복원]        notification.admin_id UNIQUE 제거 (관리자 다건 공지 허용)
-- 4. [타입 변환]       departure_time INT → TIME, TINYINT → BOOLEAN/ENUM
-- 5. [PK 추가]         route_stop에 route_stop_id SERIAL PK 추가
-- 6. [순서 컬럼]       route_stop.sequence 추가 (명시적 정류장 순서)
-- 7. [예약 다건 허용]  reservation.user_id UNIQUE 제거
-- 8. [취소일 NULL]     reservation.cancel_date NOT NULL → cancelled_at NULLABLE
-- 9. [위치 테이블]     location → shuttle_location으로 개선, 셔틀 직접 연결
-- 10. [PostgreSQL 전환] AUTO_INCREMENT→SERIAL, MEDIUMTEXT→TEXT, ENGINE 제거
-- 11. [인덱스 추가]    조회 빈도 높은 컬럼에 인덱스 설계
-- 12. [VIEW 추가]      v_shuttle_current_location (최신 위치 빠른 조회)
