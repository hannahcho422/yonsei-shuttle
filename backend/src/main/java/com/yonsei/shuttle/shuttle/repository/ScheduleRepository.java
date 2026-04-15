package com.yonsei.shuttle.shuttle.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsei.shuttle.shuttle.domain.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    /**
     * 노선별 시간표 조회 (출발 시각 순)
     */
    List<Schedule> findAllByRoute_RouteIdOrderByDepartureTimeAsc(Integer routeId);

    /**
     * 특정 요일 운행 시간표 조회
     */
    List<Schedule> findAllByRoute_RouteIdAndDayOfWeek(Integer routeId, String dayOfWeek);
}