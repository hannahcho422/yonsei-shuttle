package com.yonsei.shuttle.shuttle.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleCreateRequest {

    @NotNull(message = "노선 ID는 필수입니다")
    private Integer routeId;

    @NotNull(message = "출발 시각은 필수입니다")
    private LocalTime departureTime;

    @Size(max = 10)
    private String dayOfWeek;
}