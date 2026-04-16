package com.yonsei.shuttle.shuttle.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonsei.shuttle.common.exception.CustomException;
import com.yonsei.shuttle.common.exception.ErrorCode;
import com.yonsei.shuttle.shuttle.domain.CityShuttle;
import com.yonsei.shuttle.shuttle.domain.IntercityShuttle;
import com.yonsei.shuttle.shuttle.domain.Route;
import com.yonsei.shuttle.shuttle.domain.Schedule;
import com.yonsei.shuttle.shuttle.domain.Shuttle;
import com.yonsei.shuttle.shuttle.domain.ShuttleType;
import com.yonsei.shuttle.shuttle.domain.Stop;
import com.yonsei.shuttle.shuttle.dto.RouteCreateRequest;
import com.yonsei.shuttle.shuttle.dto.RouteResponse;
import com.yonsei.shuttle.shuttle.dto.RouteStopResponse;
import com.yonsei.shuttle.shuttle.dto.ScheduleCreateRequest;
import com.yonsei.shuttle.shuttle.dto.ScheduleResponse;
import com.yonsei.shuttle.shuttle.dto.ShuttleCreateRequest;
import com.yonsei.shuttle.shuttle.dto.ShuttleResponse;
import com.yonsei.shuttle.shuttle.dto.StopCreateRequest;
import com.yonsei.shuttle.shuttle.dto.StopResponse;
import com.yonsei.shuttle.shuttle.repository.CityShuttleRepository;
import com.yonsei.shuttle.shuttle.repository.IntercityShuttleRepository;
import com.yonsei.shuttle.shuttle.repository.RouteRepository;
import com.yonsei.shuttle.shuttle.repository.RouteStopRepository;
import com.yonsei.shuttle.shuttle.repository.ScheduleRepository;
import com.yonsei.shuttle.shuttle.repository.ShuttleRepository;
import com.yonsei.shuttle.shuttle.repository.StopRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 셔틀 / 노선 / 정류장 / 시간표 비즈니스 로직
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShuttleService {

    private final ShuttleRepository shuttleRepository;
    private final CityShuttleRepository cityShuttleRepository;
    private final IntercityShuttleRepository intercityShuttleRepository;
    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;
    private final RouteStopRepository routeStopRepository;
    private final ScheduleRepository scheduleRepository;

    // =========================================================
    // 셔틀 (Shuttle)
    // =========================================================

    /**
     * 전체 셔틀 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ShuttleResponse> getAllShuttles() {
        return shuttleRepository.findAll().stream()
                .map(ShuttleResponse::from)
                .toList();
    }

    /**
     * 타입별 셔틀 목록 조회 (시내 / 시외)
     */
    @Transactional(readOnly = true)
    public List<ShuttleResponse> getShuttlesByType(ShuttleType type) {
        return shuttleRepository.findAllByType(type).stream()
                .map(ShuttleResponse::from)
                .toList();
    }

    /**
     * 셔틀 단건 조회
     */
    @Transactional(readOnly = true)
    public ShuttleResponse getShuttle(Integer shuttleId) {
        Shuttle shuttle = findShuttleOrThrow(shuttleId);
        return ShuttleResponse.from(shuttle);
    }

    /**
     * 셔틀 생성 (관리자)
     * 타입에 따라 city_shuttle 또는 intercity_shuttle도 함께 생성
     */
    @Transactional
    public ShuttleResponse createShuttle(ShuttleCreateRequest request) {
        Shuttle shuttle = Shuttle.builder()
                .name(request.getName())
                .type(request.getType())
                .capacity(request.getCapacity())
                .build();

        Shuttle saved = shuttleRepository.save(shuttle);

        // 타입별 확장 테이블 자동 생성
        if (request.getType() == ShuttleType.CITY) {
            CityShuttle cityShuttle = CityShuttle.builder()
                    .shuttle(saved)
                    .build();
            cityShuttleRepository.save(cityShuttle);
        } else {
            IntercityShuttle intercityShuttle = IntercityShuttle.builder()
                    .shuttle(saved)
                    .build();
            intercityShuttleRepository.save(intercityShuttle);
        }

        log.info("셔틀 생성: id={}, name={}, type={}", saved.getShuttleId(), saved.getName(), saved.getType());
        return ShuttleResponse.from(saved);
    }

    /**
     * 셔틀 삭제 (관리자) — CASCADE로 연관 데이터 자동 삭제
     */
    @Transactional
    public void deleteShuttle(Integer shuttleId) {
        Shuttle shuttle = findShuttleOrThrow(shuttleId);
        shuttleRepository.delete(shuttle);
        log.info("셔틀 삭제: id={}, name={}", shuttleId, shuttle.getName());
    }

    // =========================================================
    // 노선 (Route)
    // =========================================================

    /**
     * 전체 노선 목록 조회
     */
    @Transactional(readOnly = true)
    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(RouteResponse::from)
                .toList();
    }

    /**
     * 셔틀별 노선 목록 조회
     */
    @Transactional(readOnly = true)
    public List<RouteResponse> getRoutesByShuttle(Integer shuttleId) {
        return routeRepository.findAllByShuttle_ShuttleId(shuttleId).stream()
                .map(RouteResponse::from)
                .toList();
    }

    /**
     * 노선 단건 조회
     */
    @Transactional(readOnly = true)
    public RouteResponse getRoute(Integer routeId) {
        Route route = findRouteOrThrow(routeId);
        return RouteResponse.from(route);
    }

    /**
     * 노선 생성 (관리자)
     */
    @Transactional
    public RouteResponse createRoute(RouteCreateRequest request) {
        Shuttle shuttle = findShuttleOrThrow(request.getShuttleId());

        Route route = Route.builder()
                .shuttle(shuttle)
                .routeName(request.getRouteName())
                .direction(request.getDirection())
                .build();

        Route saved = routeRepository.save(route);
        log.info("노선 생성: id={}, name={}", saved.getRouteId(), saved.getRouteName());
        return RouteResponse.from(saved);
    }

    /**
     * 노선 삭제 (관리자)
     */
    @Transactional
    public void deleteRoute(Integer routeId) {
        Route route = findRouteOrThrow(routeId);
        routeRepository.delete(route);
        log.info("노선 삭제: id={}, name={}", routeId, route.getRouteName());
    }

    // =========================================================
    // 정류장 (Stop)
    // =========================================================

    /**
     * 전체 정류장 목록 조회
     */
    @Transactional(readOnly = true)
    public List<StopResponse> getAllStops() {
        return stopRepository.findAll().stream()
                .map(StopResponse::from)
                .toList();
    }

    /**
     * 정류장 단건 조회
     */
    @Transactional(readOnly = true)
    public StopResponse getStop(Integer stopId) {
        Stop stop = findStopOrThrow(stopId);
        return StopResponse.from(stop);
    }

    /**
     * 노선별 정류장 목록 (순서 포함)
     */
    @Transactional(readOnly = true)
    public List<RouteStopResponse> getStopsByRoute(Integer routeId) {
        findRouteOrThrow(routeId); // 노선 존재 확인
        return routeStopRepository.findAllByRoute_RouteIdOrderBySequenceAsc(routeId).stream()
                .map(RouteStopResponse::from)
                .toList();
    }

    /**
     * 정류장 생성 (관리자)
     */
    @Transactional
    public StopResponse createStop(StopCreateRequest request) {
        Stop stop = Stop.builder()
                .stopName(request.getStopName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .imagePath(request.getImagePath())
                .build();

        Stop saved = stopRepository.save(stop);
        log.info("정류장 생성: id={}, name={}", saved.getStopId(), saved.getStopName());
        return StopResponse.from(saved);
    }

    /**
     * 정류장 삭제 (관리자)
     */
    @Transactional
    public void deleteStop(Integer stopId) {
        Stop stop = findStopOrThrow(stopId);
        stopRepository.delete(stop);
        log.info("정류장 삭제: id={}, name={}", stopId, stop.getStopName());
    }

    // =========================================================
    // 시간표 (Schedule)
    // =========================================================

    /**
     * 노선별 시간표 조회
     */
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getSchedulesByRoute(Integer routeId) {
        findRouteOrThrow(routeId);
        return scheduleRepository.findAllByRoute_RouteIdOrderByDepartureTimeAsc(routeId).stream()
                .map(ScheduleResponse::from)
                .toList();
    }

    /**
     * 노선별 + 요일별 시간표 조회
     */
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getSchedulesByRouteAndDay(Integer routeId, String dayOfWeek) {
        findRouteOrThrow(routeId);
        return scheduleRepository.findAllByRoute_RouteIdAndDayOfWeek(routeId, dayOfWeek).stream()
                .map(ScheduleResponse::from)
                .toList();
    }

    /**
     * 시간표 생성 (관리자)
     */
    @Transactional
    public ScheduleResponse createSchedule(ScheduleCreateRequest request) {
        Route route = findRouteOrThrow(request.getRouteId());

        Schedule schedule = Schedule.builder()
                .route(route)
                .departureTime(request.getDepartureTime())
                .dayOfWeek(request.getDayOfWeek())
                .build();

        Schedule saved = scheduleRepository.save(schedule);
        log.info("시간표 생성: id={}, route={}, time={}", saved.getScheduleId(), route.getRouteName(), saved.getDepartureTime());
        return ScheduleResponse.from(saved);
    }

    /**
     * 시간표 삭제 (관리자)
     */
    @Transactional
    public void deleteSchedule(Integer scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));
        scheduleRepository.delete(schedule);
        log.info("시간표 삭제: id={}", scheduleId);
    }

    // =========================================================
    // 공통 헬퍼 메서드 (private)
    // =========================================================

    private Shuttle findShuttleOrThrow(Integer shuttleId) {
        return shuttleRepository.findById(shuttleId)
                .orElseThrow(() -> new CustomException(ErrorCode.SHUTTLE_NOT_FOUND));
    }

    private Route findRouteOrThrow(Integer routeId) {
        return routeRepository.findById(routeId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROUTE_NOT_FOUND));
    }

    private Stop findStopOrThrow(Integer stopId) {
        return stopRepository.findById(stopId)
                .orElseThrow(() -> new CustomException(ErrorCode.STOP_NOT_FOUND));
    }
}