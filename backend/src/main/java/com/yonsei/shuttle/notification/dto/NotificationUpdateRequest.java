package com.yonsei.shuttle.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationUpdateRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    private String content;
} 