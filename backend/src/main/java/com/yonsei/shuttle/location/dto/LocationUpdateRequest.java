package com.yonsei.shuttle.location.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 시뮬레이터 → Backend 위치 수신 요청 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocationUpdateRequest {

    @NotNull(message = "셔틀 ID는 필수입니다")
    private Integer shuttleId;

    @NotNull(message = "위도는 필수입니다")
    private BigDecimal latitude;

    @NotNull(message = "경도는 필수입니다")
    private BigDecimal longitude;

    private BigDecimal heading;

    private BigDecimal speed;
}