package com.yonsei.shuttle.shuttle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RouteCreateRequest {

    @NotNull(message = "셔틀 ID는 필수입니다")
    private Integer shuttleId;

    @NotBlank(message = "노선 이름은 필수입니다")
    @Size(max = 255)
    private String routeName;

    private String direction;
}