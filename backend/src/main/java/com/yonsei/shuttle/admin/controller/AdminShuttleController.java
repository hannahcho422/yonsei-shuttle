package com.yonsei.shuttle.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yonsei.shuttle.common.response.ApiResponse;
import com.yonsei.shuttle.shuttle.dto.RouteCreateRequest;
import com.yonsei.shuttle.shuttle.dto.RouteResponse;
import com.yonsei.shuttle.shuttle.dto.ScheduleCreateRequest;
import com.yonsei.shuttle.shuttle.dto.ScheduleResponse;
import com.yonsei.shuttle.shuttle.dto.ShuttleCreateRequest;
import com.yonsei.shuttle.shuttle.dto.ShuttleResponse;
import com.yonsei.shuttle.shuttle.dto.StopCreateRequest;
import com.yonsei.shuttle.shuttle.dto.StopResponse;
import com.yonsei.shuttle.shuttle.service.ShuttleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 관리자용 셔틀 / 노선 / 정류장 / 시간표 CRUD API
 * /api/admin/** → SecurityConfig에서 ROLE_ADMIN 권한 필요
 */
@Tag(name = "Admin - Shuttle", description = "관리자 셔틀 관리 API")
@RestController
@RequestMapping("/api/admin/shuttles")
@RequiredArgsConstructor
public class AdminShuttleController {

    private final ShuttleService shuttleService;

    // ===== 셔틀 =====

    @Operation(summary = "[관리자] 셔틀 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<ShuttleResponse>> createShuttle(
            @Valid @RequestBody ShuttleCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(shuttleService.createShuttle(request)));
    }

    @Operation(summary = "[관리자] 셔틀 삭제")
    @DeleteMapping("/{shuttleId}")
    public ResponseEntity<ApiResponse<Void>> deleteShuttle(
            @PathVariable Integer shuttleId) {
        shuttleService.deleteShuttle(shuttleId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // ===== 노선 =====

    @Operation(summary = "[관리자] 노선 생성")
    @PostMapping("/routes")
    public ResponseEntity<ApiResponse<RouteResponse>> createRoute(
            @Valid @RequestBody RouteCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(shuttleService.createRoute(request)));
    }

    @Operation(summary = "[관리자] 노선 삭제")
    @DeleteMapping("/routes/{routeId}")
    public ResponseEntity<ApiResponse<Void>> deleteRoute(
            @PathVariable Integer routeId) {
        shuttleService.deleteRoute(routeId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // ===== 정류장 =====

    @Operation(summary = "[관리자] 정류장 생성")
    @PostMapping("/stops")
    public ResponseEntity<ApiResponse<StopResponse>> createStop(
            @Valid @RequestBody StopCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(shuttleService.createStop(request)));
    }

    @Operation(summary = "[관리자] 정류장 삭제")
    @DeleteMapping("/stops/{stopId}")
    public ResponseEntity<ApiResponse<Void>> deleteStop(
            @PathVariable Integer stopId) {
        shuttleService.deleteStop(stopId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // ===== 시간표 =====

    @Operation(summary = "[관리자] 시간표 생성")
    @PostMapping("/schedules")
    public ResponseEntity<ApiResponse<ScheduleResponse>> createSchedule(
            @Valid @RequestBody ScheduleCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(shuttleService.createSchedule(request)));
    }

    @Operation(summary = "[관리자] 시간표 삭제")
    @DeleteMapping("/schedules/{scheduleId}")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(
            @PathVariable Integer scheduleId) {
        shuttleService.deleteSchedule(scheduleId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}