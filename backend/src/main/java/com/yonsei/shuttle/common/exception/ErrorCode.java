package com.yonsei.shuttle.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 애플리케이션 전역 에러 코드
 * 도메인별로 접두어 구분:
 *   C : Common
 *   A : Auth
 *   U : User
 *   S : Shuttle
 *   R : Reservation
 *   L : Location
 *   N : Notification
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ===== Common (C) =====
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "서버 내부 오류가 발생했습니다"),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C002", "입력값이 올바르지 않습니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C003", "허용되지 않은 HTTP 메서드입니다"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C004", "요청한 리소스를 찾을 수 없습니다"),

    // ===== Auth (A) =====
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "A001", "이메일 또는 비밀번호가 올바르지 않습니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "유효하지 않은 토큰입니다"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "만료된 토큰입니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A004", "인증이 필요합니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "A005", "접근 권한이 없습니다"),

    // ===== User (U) =====
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U002", "이미 등록된 이메일입니다"),

    // ===== Shuttle (S) =====
    SHUTTLE_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "셔틀을 찾을 수 없습니다"),
    ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "S002", "노선을 찾을 수 없습니다"),
    STOP_NOT_FOUND(HttpStatus.NOT_FOUND, "S003", "정류장을 찾을 수 없습니다"),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "S004", "운행 시간표를 찾을 수 없습니다"),

    // ===== Reservation (R) =====
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "예약을 찾을 수 없습니다"),
    SEAT_ALREADY_RESERVED(HttpStatus.CONFLICT, "R002", "이미 예약된 좌석입니다"),
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "R003", "좌석을 찾을 수 없습니다"),
    RESERVATION_NOT_OWNED(HttpStatus.FORBIDDEN, "R004", "본인의 예약만 접근할 수 있습니다"),
    RESERVATION_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "R005", "이미 취소된 예약입니다"),

    // ===== Location (L) =====
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "L001", "위치 정보를 찾을 수 없습니다"),

    // ===== Notification (N) =====
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "N001", "공지사항을 찾을 수 없습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}