package com.yonsei.shuttle.reservation.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 예약 상태
 * DB 저장값: '예약' | '취소' (PostgreSQL ENUM)
 */
@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
    RESERVED("예약"),
    CANCELLED("취소");

    private final String dbValue;

    public static ReservationStatus fromDbValue(String dbValue) {
        for (ReservationStatus status : values()) {
            if (status.dbValue.equals(dbValue)) return status;
        }
        throw new IllegalArgumentException("Unknown reservation_status: " + dbValue);
    }
}