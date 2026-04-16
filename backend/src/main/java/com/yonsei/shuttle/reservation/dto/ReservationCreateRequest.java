package com.yonsei.shuttle.reservation.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 예약 요청 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationCreateRequest {

    @NotNull(message = "시외 셔틀 ID는 필수입니다")
    private Integer intercityShuttleId;

    @NotNull(message = "좌석 ID는 필수입니다")
    private Integer seatId;

    @NotNull(message = "시간표 ID는 필수입니다")
    private Integer scheduleId;

    @NotNull(message = "탑승 날짜는 필수입니다")
    private LocalDate travelDate;
}