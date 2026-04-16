package com.yonsei.shuttle.notification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yonsei.shuttle.common.response.ApiResponse;
import com.yonsei.shuttle.notification.dto.NotificationResponse;
import com.yonsei.shuttle.notification.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 공지사항 조회 API (사용자용)
 */
@Tag(name = "Notification", description = "공지사항 API")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "전체 공지사항 목록 (최신순)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getAllNotifications() {
        return ResponseEntity.ok(
                ApiResponse.success(notificationService.getAllNotifications()));
    }

    @Operation(summary = "공지사항 단건 조회")
    @GetMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotification(
            @PathVariable Integer notificationId) {
        return ResponseEntity.ok(
                ApiResponse.success(notificationService.getNotification(notificationId)));
    }
}