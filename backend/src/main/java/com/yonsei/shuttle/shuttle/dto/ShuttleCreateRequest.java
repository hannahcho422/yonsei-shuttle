package com.yonsei.shuttle.shuttle.dto;

import com.yonsei.shuttle.shuttle.domain.ShuttleType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShuttleCreateRequest {

    @NotBlank(message = "셔틀 이름은 필수입니다")
    @Size(max = 100)
    private String name;

    @NotNull(message = "셔틀 타입은 필수입니다 (CITY 또는 INTERCITY)")
    private ShuttleType type;

    @Positive(message = "좌석 수는 양수여야 합니다")
    private Integer capacity;
}