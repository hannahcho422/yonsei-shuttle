package com.yonsei.shuttle.location.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yonsei.shuttle.common.response.ApiResponse;
import com.yonsei.shuttle.location.dto.EtaResponse;
import com.yonsei.shuttle.location.dto.LocationResponse;
import com.yonsei.shuttle.location.service.LocationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 셔틀 실시간 위치 조회 API (사용자용)
 */
@Tag(name = "Location", description = "셔틀 실시간 위치 API")
@RestController
@RequestMapping("/api/shuttles")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @Operation(summary = "모든 운행 중인 셔틀 위치 조회")
    @GetMapping("/locations")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getAllLocations() {
        return ResponseEntity.ok(
                ApiResponse.success(locationService.getAllCurrentLocations()));
    }

    @Operation(summary = "셔틀 현재 위치 조회")
    @GetMapping("/{shuttleId}/location")
    public ResponseEntity<ApiResponse<LocationResponse>> getLocation(
            @PathVariable Integer shuttleId) {
        return ResponseEntity.ok(
                ApiResponse.success(locationService.getCurrentLocation(shuttleId)));
    }

    @Operation(summary = "셔틀 ETA 조회 (노선별 정류장까지 예상 소요 시간)")
    @GetMapping("/{shuttleId}/eta")
    public ResponseEntity<ApiResponse<List<EtaResponse>>> getEta(
            @PathVariable Integer shuttleId,
            @RequestParam Integer routeId) {
        return ResponseEntity.ok(
                ApiResponse.success(locationService.calculateEta(shuttleId, routeId)));
    }
}