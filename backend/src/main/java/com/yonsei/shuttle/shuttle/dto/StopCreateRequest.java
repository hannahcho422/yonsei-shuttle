package com.yonsei.shuttle.shuttle.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StopCreateRequest {

    @NotBlank(message = "정류장 이름은 필수입니다")
    @Size(max = 255)
    private String stopName;

    @NotNull(message = "위도는 필수입니다")
    private BigDecimal latitude;

    @NotNull(message = "경도는 필수입니다")
    private BigDecimal longitude;

    private String imagePath;
}