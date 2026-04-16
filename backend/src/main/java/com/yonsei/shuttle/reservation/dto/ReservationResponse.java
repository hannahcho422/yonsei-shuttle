package com.yonsei.shuttle.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

import com.yonsei.shuttle.reservation.domain.Reservation;
import com.yonsei.shuttle.reservation.domain.ReservationStatus;

import lombok.Builder;
import lombok.Getter;

/**
 * 예약 응답 DTO
 */
@Getter
@Builder
public class ReservationResponse {

    private final Integer reservationId;
    private final Integer userId;
    private final String userName;
    private final Integer intercityShuttleId;
    private final String shuttleName;
    private final Integer seatId;
    private final Integer seatNum;
    private final Integer scheduleId;
    private final String routeName;
    private final LocalTime departureTime;
    private final String dayOfWeek;
    private final LocalDate travelDate;
    private final ReservationStatus status;
    private final OffsetDateTime reservedAt;
    private final OffsetDateTime cancelledAt;

    public static ReservationResponse from(Reservation reservation) {
        return ReservationResponse.builder()
                .reservationId(reservation.getReservationId())
                .userId(reservation.getUser().getUserId())
                .userName(reservation.getUser().getName())
                .intercityShuttleId(reservation.getIntercityShuttle().getIntercityShuttleId())
                .shuttleName(reservation.getIntercityShuttle().getShuttle().getName())
                .seatId(reservation.getSeat().getSeatId())
                .seatNum(reservation.getSeat().getSeatNum())
                .scheduleId(reservation.getSchedule().getScheduleId())
                .routeName(reservation.getSchedule().getRoute().getRouteName())
                .departureTime(reservation.getSchedule().getDepartureTime())
                .dayOfWeek(reservation.getSchedule().getDayOfWeek())
                .travelDate(reservation.getTravelDate())
                .status(reservation.getStatus())
                .reservedAt(reservation.getReservedAt())
                .cancelledAt(reservation.getCancelledAt())
                .build();
    }
}