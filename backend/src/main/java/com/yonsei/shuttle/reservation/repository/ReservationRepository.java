package com.yonsei.shuttle.reservation.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsei.shuttle.reservation.domain.Reservation;
import com.yonsei.shuttle.reservation.domain.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    /**
     * 사용자의 예약 내역 (최신순)
     */
    List<Reservation> findAllByUser_UserIdOrderByReservedAtDesc(Integer userId);

    /**
     * 특정 스케줄 + 날짜의 예약 좌석 (좌석 가용 여부 판단용)
     */
    List<Reservation> findAllBySchedule_ScheduleIdAndTravelDateAndStatus(
            Integer scheduleId, LocalDate travelDate, ReservationStatus status
    );

    /**
     * 중복 예약 체크 (좌석 + 스케줄 + 날짜 + 상태)
     */
    boolean existsBySeat_SeatIdAndSchedule_ScheduleIdAndTravelDateAndStatus(
            Integer seatId, Integer scheduleId, LocalDate travelDate, ReservationStatus status
    );
}