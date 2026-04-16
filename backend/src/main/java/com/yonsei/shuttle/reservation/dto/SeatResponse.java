package com.yonsei.shuttle.reservation.dto;

import com.yonsei.shuttle.reservation.domain.Seat;

import lombok.Builder;
import lombok.Getter;

/**
 * 좌석 응답 DTO
 */
@Getter
@Builder
public class SeatResponse {

    private final Integer seatId;
    private final Integer seatNum;
    private final boolean reserved;

    public static SeatResponse of(Seat seat, boolean reserved) {
        return SeatResponse.builder()
                .seatId(seat.getSeatId())
                .seatNum(seat.getSeatNum())
                .reserved(reserved)
                .build();
    }
}