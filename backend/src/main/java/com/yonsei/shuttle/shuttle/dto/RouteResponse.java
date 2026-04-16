package com.yonsei.shuttle.shuttle.dto;

import com.yonsei.shuttle.shuttle.domain.Route;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RouteResponse {

    private final Integer routeId;
    private final Integer shuttleId;
    private final String shuttleName;
    private final String routeName;
    private final String direction;

    public static RouteResponse from(Route route) {
        return RouteResponse.builder()
                .routeId(route.getRouteId())
                .shuttleId(route.getShuttle().getShuttleId())
                .shuttleName(route.getShuttle().getName())
                .routeName(route.getRouteName())
                .direction(route.getDirection())
                .build();
    }
}