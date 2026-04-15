package com.yonsei.shuttle.reservation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsei.shuttle.reservation.domain.Seat;

public interface SeatRepository extends JpaRepository<Seat, Integer> {

    /**
     * 시외 셔틀의 모든 좌석 (좌석번호 순)
     */
    List<Seat> findAllByIntercityShuttle_IntercityShuttleIdOrderBySeatNumAsc(Integer intercityShuttleId);
}