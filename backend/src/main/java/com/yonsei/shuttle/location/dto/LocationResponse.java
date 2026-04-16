package com.yonsei.shuttle.location.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.yonsei.shuttle.location.domain.ShuttleLocation;

import lombok.Builder;
import lombok.Getter;

/**
 * 셔틀 위치 응답 DTO
 */
@Getter
@Builder
public class LocationResponse {

    private final Integer shuttleId;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final BigDecimal heading;
    private final BigDecimal speed;
    private final OffsetDateTime updatedAt;

    public static LocationResponse from(ShuttleLocation location) {
        return LocationResponse.builder()
                .shuttleId(location.getShuttle().getShuttleId())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .heading(location.getHeading())
                .speed(location.getSpeed())
                .updatedAt(location.getUpdatedAt())
                .build();
    }

    /**
     * Redis 캐시에서 복원할 때 사용
     */
    public static LocationResponse of(Integer shuttleId, BigDecimal latitude, BigDecimal longitude,
                                       BigDecimal heading, BigDecimal speed, OffsetDateTime updatedAt) {
        return LocationResponse.builder()
                .shuttleId(shuttleId)
                .latitude(latitude)
                .longitude(longitude)
                .heading(heading)
                .speed(speed)
                .updatedAt(updatedAt)
                .build();
    }
}