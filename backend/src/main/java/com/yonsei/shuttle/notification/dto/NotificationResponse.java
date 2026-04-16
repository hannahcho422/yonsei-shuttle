package com.yonsei.shuttle.notification.dto;

import java.time.OffsetDateTime;

import com.yonsei.shuttle.notification.domain.Notification;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {

    private final Integer notificationId;
    private final String adminName;
    private final String title;
    private final String content;
    private final OffsetDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .adminName(notification.getAdmin().getUser().getName())
                .title(notification.getTitle())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}