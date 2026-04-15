package com.yonsei.shuttle.user.domain;

import java.time.OffsetDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 (일반 + 관리자 통합)
 */
@Entity
@Table(name = "user", schema = "yonsei_shuttle")    // user는 PostgreSQL 예약어지만 스키마를 지정하면 OK. 쿼리에서는 JPA가 자동 이스케이프
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA는 기본 생성자 필요하지만, 외부에서 new로 못 만들게 PROTECTED
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // PostgreSQL SERIAL과 매핑
    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    // PostgreSQL ENUM과 Java Enum을 VARCHAR로 매핑
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, columnDefinition = "user_role")
    private UserRole role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Builder
    private User(String name, String email, String password, UserRole role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role == null ? UserRole.USER : role;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = OffsetDateTime.now();
    }

    // ===== 비즈니스 메서드 =====
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void promoteToAdmin() {
        this.role = UserRole.ADMIN;
    }
}