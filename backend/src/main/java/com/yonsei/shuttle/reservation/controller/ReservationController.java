package com.yonsei.shuttle.reservation.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yonsei.shuttle.auth.security.CustomUserDetails;
import com.yonsei.shuttle.common.response.ApiResponse;
import com.yonsei.shuttle.reservation.dto.ReservationCreateRequest;
import com.yonsei.shuttle.reservation.dto.ReservationResponse;
import com.yonsei.shuttle.reservation.dto.SeatResponse;
import com.yonsei.shuttle.reservation.service.ReservationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 시외 셔틀 예약 API (사용자용)
 */
@Tag(name = "Reservation", description = "시외 셔틀 예약 API")
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "예약 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> createReservation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReservationCreateRequest request) {
        ReservationResponse response = reservationService.createReservation(
                userDetails.getUserId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @Operation(summary = "내 예약 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getMyReservations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success(reservationService.getMyReservations(userDetails.getUserId())));
    }

    @Operation(summary = "예약 단건 조회")
    @GetMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationResponse>> getReservation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer reservationId) {
        return ResponseEntity.ok(
                ApiResponse.success(reservationService.getReservation(
                        userDetails.getUserId(), reservationId)));
    }

    @Operation(summary = "예약 취소")
    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<ApiResponse<ReservationResponse>> cancelReservation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer reservationId) {
        return ResponseEntity.ok(
                ApiResponse.success(reservationService.cancelReservation(
                        userDetails.getUserId(), reservationId)));
    }

    @Operation(summary = "좌석 가용 현황 조회 (예약 전 좌석 선택용)")
    @GetMapping("/seats")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getAvailableSeats(
            @RequestParam Integer intercityShuttleId,
            @RequestParam Integer scheduleId,
            @RequestParam LocalDate travelDate) {
        return ResponseEntity.ok(
                ApiResponse.success(reservationService.getAvailableSeats(
                        intercityShuttleId, scheduleId, travelDate)));
    }
}