package com.yonsei.shuttle.shuttle.dto;

import java.time.LocalTime;

import com.yonsei.shuttle.shuttle.domain.Schedule;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScheduleResponse {

    private final Integer scheduleId;
    private final Integer routeId;
    private final String routeName;
    private final LocalTime departureTime;
    private final String dayOfWeek;

    public static ScheduleResponse from(Schedule schedule) {
        return ScheduleResponse.builder()
                .scheduleId(schedule.getScheduleId())
                .routeId(schedule.getRoute().getRouteId())
                .routeName(schedule.getRoute().getRouteName())
                .departureTime(schedule.getDepartureTime())
                .dayOfWeek(schedule.getDayOfWeek())
                .build();
    }
}