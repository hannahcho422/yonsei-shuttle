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
import com.yonsei.shuttle.shuttle.service.ShuttleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 내부 시스템 (시뮬레이터) 전용 셔틀 조회 API
 * /api/internal/** 는 SecurityConfig에서 permitAll
 */
@Tag(name = "Internal - Shuttle", description = "시뮬레이터용 셔틀 조회 API")
@RestController
@RequestMapping("/api/internal/shuttles")
@RequiredArgsConstructor
public class InternalShuttleController {

    private final ShuttleService shuttleService;

    @Operation(summary = "[시뮬레이터] 셔틀 목록")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ShuttleResponse>>> getAllShuttles(
            @RequestParam(required = false) ShuttleType type) {
        if (type != null) {
            return ResponseEntity.ok(ApiResponse.success(shuttleService.getShuttlesByType(type)));
        }
        return ResponseEntity.ok(ApiResponse.success(shuttleService.getAllShuttles()));
    }

    @Operation(summary = "[시뮬레이터] 셔틀별 노선")
    @GetMapping("/{shuttleId}/routes")
    public ResponseEntity<ApiResponse<List<RouteResponse>>> getRoutes(
            @PathVariable Integer shuttleId) {
        return ResponseEntity.ok(ApiResponse.success(shuttleService.getRoutesByShuttle(shuttleId)));
    }

    @Operation(summary = "[시뮬레이터] 노선별 정류장 (순서 포함)")
    @GetMapping("/routes/{routeId}/stops")
    public ResponseEntity<ApiResponse<List<RouteStopResponse>>> getStops(
            @PathVariable Integer routeId) {
        return ResponseEntity.ok(ApiResponse.success(shuttleService.getStopsByRoute(routeId)));
    }

    @Operation(summary = "[시뮬레이터] 노선별 시간표")
    @GetMapping("/routes/{routeId}/schedules")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getSchedules(
            @PathVariable Integer routeId) {
        return ResponseEntity.ok(ApiResponse.success(shuttleService.getSchedulesByRoute(routeId)));
    }
}