package com.yonsei.shuttle.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yonsei.shuttle.auth.security.CustomUserDetails;
import com.yonsei.shuttle.common.response.ApiResponse;
import com.yonsei.shuttle.notification.dto.NotificationCreateRequest;
import com.yonsei.shuttle.notification.dto.NotificationResponse;
import com.yonsei.shuttle.notification.dto.NotificationUpdateRequest;
import com.yonsei.shuttle.notification.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 관리자용 공지사항 CRUD API
 */
@Tag(name = "Admin - Notification", description = "관리자 공지사항 관리 API")
@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "[관리자] 공지사항 작성")
    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody NotificationCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        notificationService.createNotification(userDetails.getUserId(), request)));
    }

    @Operation(summary = "[관리자] 공지사항 수정")
    @PutMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<NotificationResponse>> updateNotification(
            @PathVariable Integer notificationId,
            @Valid @RequestBody NotificationUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        notificationService.updateNotification(notificationId, request)));
    }

    @Operation(summary = "[관리자] 공지사항 삭제")
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @PathVariable Integer notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}