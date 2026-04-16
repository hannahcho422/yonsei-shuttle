package com.yonsei.shuttle.notification.service;

import com.yonsei.shuttle.common.exception.CustomException;
import com.yonsei.shuttle.common.exception.ErrorCode;
import com.yonsei.shuttle.notification.domain.Notification;
import com.yonsei.shuttle.notification.dto.NotificationCreateRequest;
import com.yonsei.shuttle.notification.dto.NotificationResponse;
import com.yonsei.shuttle.notification.dto.NotificationUpdateRequest;
import com.yonsei.shuttle.notification.repository.NotificationRepository;
import com.yonsei.shuttle.user.domain.Admin;
import com.yonsei.shuttle.user.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 공지사항 비즈니스 로직
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AdminRepository adminRepository;

    /**
     * 전체 공지사항 목록 (최신순)
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(NotificationResponse::from)
                .toList();
    }

    /**
     * 공지사항 단건 조회
     */
    @Transactional(readOnly = true)
    public NotificationResponse getNotification(Integer notificationId) {
        Notification notification = findNotificationOrThrow(notificationId);
        return NotificationResponse.from(notification);
    }

    /**
     * 공지사항 작성 (관리자)
     */
    @Transactional
    public NotificationResponse createNotification(Integer userId, NotificationCreateRequest request) {
        Admin admin = adminRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN));

        Notification notification = Notification.builder()
                .admin(admin)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("공지사항 작성: id={}, title={}", saved.getNotificationId(), saved.getTitle());
        return NotificationResponse.from(saved);
    }

    /**
     * 공지사항 수정 (관리자)
     */
    @Transactional
    public NotificationResponse updateNotification(Integer notificationId, NotificationUpdateRequest request) {
        Notification notification = findNotificationOrThrow(notificationId);
        notification.update(request.getTitle(), request.getContent());
        log.info("공지사항 수정: id={}, title={}", notificationId, request.getTitle());
        return NotificationResponse.from(notification);
    }

    /**
     * 공지사항 삭제 (관리자)
     */
    @Transactional
    public void deleteNotification(Integer notificationId) {
        Notification notification = findNotificationOrThrow(notificationId);
        notificationRepository.delete(notification);
        log.info("공지사항 삭제: id={}", notificationId);
    }

    private Notification findNotificationOrThrow(Integer notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));
    }
}