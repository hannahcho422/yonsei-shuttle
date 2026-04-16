package com.yonsei.shuttle.location.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

/**
 * 정류장별 ETA 응답 DTO
 */
@Getter
@Builder
public class EtaResponse {

    private final Integer stopId;
    private final String stopName;
    private final Integer sequence;
    private final int estimatedMinutes;
    private final BigDecimal distanceKm;
}