package com.yonsei.shuttle.shuttle.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yonsei.shuttle.common.response.ApiResponse;
import com.yonsei.shuttle.shuttle.domain.ShuttleType;
import com.yonsei.shuttle.shuttle.dto.RouteResponse;
import com.yonsei.shuttle.shuttle.dto.RouteStopResponse;
import com.yonsei.shuttle.shuttle.dto.ScheduleResponse;
import com.yonsei.shuttle.shuttle.dto.ShuttleResponse;
import com.yonsei.shuttle.shuttle.dto.StopResponse;
import com.yonsei.shuttle.shuttle.service.ShuttleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 셔틀 / 노선 / 정류장 / 시간표 조회 API (사용자용)
 */
@Tag(name = "Shuttle", description = "셔틀 조회 API")
@RestController
@RequestMapping("/api/shuttles")
@RequiredArgsConstructor
public class ShuttleController {

    private final ShuttleService shuttleService;

    // ===== 셔틀 =====

    @Operation(summary = "전체 셔틀 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ShuttleResponse>>> getAllShuttles() {
        return ResponseEntity.ok(ApiResponse.success(shuttleService.getAllShuttles()));
    }

    @Operation(summary = "타입별 셔틀 목록 조회 (CITY / INTERCITY)")
    @GetMapping(params = "type")
    public ResponseEntity<ApiResponse<List<ShuttleResponse>>> getShuttlesByType(
            @RequestParam ShuttleType type) {
        return ResponseEntity.ok(ApiResponse.success(shuttleService.getShuttlesByType(type)));
    }

    @Operation(summary = "셔틀 단건 조회")
    @GetMapping("/{shuttleId}")
    public ResponseEntity<ApiResponse<ShuttleResponse>> getShuttle(
            @PathVariable Integer shuttleId) {
        return ResponseEntity.ok(ApiResponse.success(shuttleService.getShuttle(shuttleId)));
    }

    // ===== 노선 =====

    @Operation(summary = "전체 노선 목록 조회")
    @GetMapping("/routes")
    public ResponseEntity<ApiResponse<List<RouteResponse>>> getAllRoutes() {
        return ResponseEntity.ok(ApiResponse.success(shuttleService.getAllRoutes()));
    }

    @Operation(summary = "셔틀별 노선 목록 조회")
    @GetMapping("/{shuttleId}/routes")
    public ResponseEntity<ApiResponse<List<RouteResponse>>> getRoutesByShuttle(
            @PathVariable Integer shuttleId) {
        return ResponseEntity.ok(ApiResponse.success(shuttleService.getRoutesByShuttle(shuttleId)));
    }

    @Operation(summary = "노선 단건 조회")
    @GetMapping("/routes/{routeId}")
    public ResponseEntity<ApiResponse<RouteResponse>> getRoute(
            @PathVariable Integer routeId) {
        return ResponseEntity.ok(ApiResponse.success(shuttleService.getRoute(routeId)));
    }

    // ===== 정류장 =====

    @Operation(summary = "전체 정류장 목록 조회")
    @GetMapping("/stops")
    public ResponseEntity<ApiResponse<List<StopResponse>>> getAllStops() {
        return ResponseEntity.ok(ApiResponse.success(shuttleService.getAllStops()));
    }

    @Operation(summary = "정류장 단건 조회")
    @GetMapping("/stops/{stopId}")
    public ResponseEntity<ApiResponse<StopResponse>> getStop(
            @PathVariable Integer stopId) {
        return ResponseEntity.ok(ApiResponse.success(shuttleService.getStop(stopId)));
    }

    @Operation(summary = "노선별 정류장 목록 조회 (순서 포함)")
    @GetMapping("/routes/{routeId}/stops")
    public ResponseEntity<ApiResponse<List<RouteStopResponse>>> getStopsByRoute(
            @PathVariable Integer routeId) {
        return ResponseEntity.ok(ApiResponse.success(shuttleService.getStopsByRoute(routeId)));
    }

    // ===== 시간표 =====

    @Operation(summary = "노선별 시간표 조회")
    @GetMapping("/routes/{routeId}/schedules")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getSchedulesByRoute(
            @PathVariable Integer routeId) {
        return ResponseEntity.ok(ApiResponse.success(shuttleService.getSchedulesByRoute(routeId)));
    }

    @Operation(summary = "노선별 + 요일별 시간표 조회")
    @GetMapping("/routes/{routeId}/schedules/{dayOfWeek}")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getSchedulesByRouteAndDay(
            @PathVariable Integer routeId,
            @PathVariable String dayOfWeek) {
        return ResponseEntity.ok(ApiResponse.success(shuttleService.getSchedulesByRouteAndDay(routeId, dayOfWeek)));
    }

    @Operation(summary = "시외 셔틀 내부 ID 조회 (예약용)")
    @GetMapping("/{shuttleId}/intercity-id")
    public ResponseEntity<ApiResponse<Integer>> getIntercityShuttleId(
            @PathVariable Integer shuttleId) {
        return ResponseEntity.ok(
                ApiResponse.success(shuttleService.getIntercityShuttleIdByShuttleId(shuttleId)));
    }
}