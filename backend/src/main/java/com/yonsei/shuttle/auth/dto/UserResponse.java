package com.yonsei.shuttle.auth.dto;

import java.time.OffsetDateTime;

import com.yonsei.shuttle.user.domain.User;
import com.yonsei.shuttle.user.domain.UserRole;

import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 정보 응답 DTO (회원가입 성공 / 내 정보 조회)
 */
@Getter
@Builder
public class UserResponse {

    private final Integer userId;
    private final String name;
    private final String email;
    private final UserRole role;
    private final OffsetDateTime createdAt;

    // ===== 정적 팩토리 =====
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}