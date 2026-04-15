package com.yonsei.shuttle.auth.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 토큰 응답 DTO (로그인 성공 시)
 */
@Getter
@Builder
public class TokenResponse {

    private final String accessToken;
    private final String refreshToken;
    private final String tokenType;

    // ===== 정적 팩토리 =====
    public static TokenResponse of(String accessToken, String refreshToken) {
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }
}