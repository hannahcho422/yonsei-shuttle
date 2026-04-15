package com.yonsei.shuttle.shuttle.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsei.shuttle.shuttle.domain.RouteStop;

public interface RouteStopRepository extends JpaRepository<RouteStop, Integer> {

    /**
     * 노선별 정류장을 sequence 순으로 조회
     */
    List<RouteStop> findAllByRoute_RouteIdOrderBySequenceAsc(Integer routeId);
}