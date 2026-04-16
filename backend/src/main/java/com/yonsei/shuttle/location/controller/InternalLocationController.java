package com.yonsei.shuttle.location.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yonsei.shuttle.common.response.ApiResponse;
import com.yonsei.shuttle.location.dto.LocationResponse;
import com.yonsei.shuttle.location.dto.LocationUpdateRequest;
import com.yonsei.shuttle.location.service.LocationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 시뮬레이터 → Backend 위치 수신 API (인증 없음)
 */
@Tag(name = "Internal", description = "내부 시스템 API (시뮬레이터용)")
@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class InternalLocationController {

    private final LocationService locationService;

    @Operation(summary = "[시뮬레이터] 셔틀 위치 업데이트")
    @PostMapping("/location")
    public ResponseEntity<ApiResponse<LocationResponse>> updateLocation(
            @Valid @RequestBody LocationUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(locationService.updateLocation(request)));
    }
}