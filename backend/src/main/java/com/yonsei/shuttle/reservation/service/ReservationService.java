package com.yonsei.shuttle.reservation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonsei.shuttle.common.exception.CustomException;
import com.yonsei.shuttle.common.exception.ErrorCode;
import com.yonsei.shuttle.reservation.domain.Reservation;
import com.yonsei.shuttle.reservation.domain.ReservationStatus;
import com.yonsei.shuttle.reservation.domain.Seat;
import com.yonsei.shuttle.reservation.dto.ReservationCreateRequest;
import com.yonsei.shuttle.reservation.dto.ReservationResponse;
import com.yonsei.shuttle.reservation.dto.SeatResponse;
import com.yonsei.shuttle.reservation.repository.ReservationRepository;
import com.yonsei.shuttle.reservation.repository.SeatRepository;
import com.yonsei.shuttle.shuttle.domain.IntercityShuttle;
import com.yonsei.shuttle.shuttle.domain.Schedule;
import com.yonsei.shuttle.shuttle.repository.IntercityShuttleRepository;
import com.yonsei.shuttle.shuttle.repository.ScheduleRepository;
import com.yonsei.shuttle.user.domain.User;
import com.yonsei.shuttle.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 시외 셔틀 예약 비즈니스 로직
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final IntercityShuttleRepository intercityShuttleRepository;
    private final ScheduleRepository scheduleRepository;

    /**
     * 예약 생성
     */
    @Transactional
    public ReservationResponse createReservation(Integer userId, ReservationCreateRequest request) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 시외 셔틀 조회
        IntercityShuttle intercityShuttle = intercityShuttleRepository.findById(request.getIntercityShuttleId())
                .orElseThrow(() -> new CustomException(ErrorCode.SHUTTLE_NOT_FOUND));

        // 좌석 조회
        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));

        // 시간표 조회
        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 중복 예약 체크 (같은 좌석 + 같은 스케줄 + 같은 날짜에 '예약' 상태가 이미 있는지)
        boolean alreadyReserved = reservationRepository
                .existsBySeat_SeatIdAndSchedule_ScheduleIdAndTravelDateAndStatus(
                        seat.getSeatId(),
                        schedule.getScheduleId(),
                        request.getTravelDate(),
                        ReservationStatus.RESERVED
                );

        if (alreadyReserved) {
            throw new CustomException(ErrorCode.SEAT_ALREADY_RESERVED);
        }

        // 예약 생성
        Reservation reservation = Reservation.builder()
                .user(user)
                .intercityShuttle(intercityShuttle)
                .seat(seat)
                .schedule(schedule)
                .travelDate(request.getTravelDate())
                .build();

        Reservation saved = reservationRepository.save(reservation);
        log.info("예약 생성: id={}, userId={}, seatId={}, travelDate={}",
                saved.getReservationId(), userId, seat.getSeatId(), request.getTravelDate());

        return ReservationResponse.from(saved);
    }

    /**
     * 내 예약 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ReservationResponse> getMyReservations(Integer userId) {
        return reservationRepository.findAllByUser_UserIdOrderByReservedAtDesc(userId).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    /**
     * 예약 단건 조회
     */
    @Transactional(readOnly = true)
    public ReservationResponse getReservation(Integer userId, Integer reservationId) {
        Reservation reservation = findReservationOrThrow(reservationId);

        // 본인 예약만 접근 가능
        if (!reservation.isOwnedBy(userId)) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_OWNED);
        }

        return ReservationResponse.from(reservation);
    }

    /**
     * 예약 취소
     */
    @Transactional
    public ReservationResponse cancelReservation(Integer userId, Integer reservationId) {
        Reservation reservation = findReservationOrThrow(reservationId);

        // 본인 예약만 취소 가능
        if (!reservation.isOwnedBy(userId)) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_OWNED);
        }

        // 도메인 메서드 호출 (이미 취소된 경우 예외 발생)
        reservation.cancel();
        log.info("예약 취소: id={}, userId={}", reservationId, userId);

        return ReservationResponse.from(reservation);
    }

    /**
     * 특정 스케줄 + 날짜의 좌석 가용 현황 조회
     * 모든 좌석과 예약 여부를 반환
     */
    @Transactional(readOnly = true)
    public List<SeatResponse> getAvailableSeats(Integer intercityShuttleId,
                                                 Integer scheduleId,
                                                 LocalDate travelDate) {
        // 해당 셔틀의 모든 좌석
        List<Seat> allSeats = seatRepository
                .findAllByIntercityShuttle_IntercityShuttleIdOrderBySeatNumAsc(intercityShuttleId);

        // 해당 스케줄 + 날짜에 이미 예약된 좌석 ID Set
        Set<Integer> reservedSeatIds = reservationRepository
                .findAllBySchedule_ScheduleIdAndTravelDateAndStatus(scheduleId, travelDate, ReservationStatus.RESERVED)
                .stream()
                .map(r -> r.getSeat().getSeatId())
                .collect(Collectors.toSet());

        // 각 좌석별 예약 여부 포함하여 반환
        return allSeats.stream()
                .map(seat -> SeatResponse.of(seat, reservedSeatIds.contains(seat.getSeatId())))
                .toList();
    }

    /**
     * 특정 스케줄 + 날짜의 잔여 좌석 수
     */
    @Transactional(readOnly = true)
    public long getRemainingSeats(Integer intercityShuttleId,
                                  Integer scheduleId,
                                  LocalDate travelDate) {
        List<SeatResponse> seats = getAvailableSeats(intercityShuttleId, scheduleId, travelDate);
        return seats.stream().filter(s -> !s.isReserved()).count();
    }

    // =========================================================
    // 헬퍼
    // =========================================================

    private Reservation findReservationOrThrow(Integer reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
    }
}