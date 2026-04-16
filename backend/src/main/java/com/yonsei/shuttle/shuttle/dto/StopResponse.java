package com.yonsei.shuttle.shuttle.dto;

import java.math.BigDecimal;

import com.yonsei.shuttle.shuttle.domain.Stop;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StopResponse {

    private final Integer stopId;
    private final String stopName;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final String imagePath;

    public static StopResponse from(Stop stop) {
        return StopResponse.builder()
                .stopId(stop.getStopId())
                .stopName(stop.getStopName())
                .latitude(stop.getLatitude())
                .longitude(stop.getLongitude())
                .imagePath(stop.getImagePath())
                .build();
    }
}