package com.yonsei.shuttle.user.domain;

/**
 * 사용자 권한
 * DB 저장값: 'USER' | 'ADMIN' (PostgreSQL ENUM, Java Enum 이름과 동일)
 */
public enum UserRole {
    USER,
    ADMIN
}