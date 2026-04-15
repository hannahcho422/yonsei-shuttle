package com.yonsei.shuttle.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsei.shuttle.notification.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    /**
     * 전체 공지사항 (최신순)
     */
    List<Notification> findAllByOrderByCreatedAtDesc();
}