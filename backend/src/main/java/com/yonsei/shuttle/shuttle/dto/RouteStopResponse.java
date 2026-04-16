package com.yonsei.shuttle.shuttle.dto;

import java.math.BigDecimal;

import com.yonsei.shuttle.shuttle.domain.RouteStop;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RouteStopResponse {

    private final Integer routeStopId;
    private final Integer sequence;
    private final long arrivalTimeMinutes;
    private final Integer stopId;
    private final String stopName;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final String imagePath;

    public static RouteStopResponse from(RouteStop routeStop) {
        return RouteStopResponse.builder()
                .routeStopId(routeStop.getRouteStopId())
                .sequence(routeStop.getSequence())
                .arrivalTimeMinutes(routeStop.getArrivalTime().toMinutes())
                .stopId(routeStop.getStop().getStopId())
                .stopName(routeStop.getStop().getStopName())
                .latitude(routeStop.getStop().getLatitude())
                .longitude(routeStop.getStop().getLongitude())
                .imagePath(routeStop.getStop().getImagePath())
                .build();
    }
}