package com.yonsei.shuttle.shuttle.domain;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 셔틀 버스 (시내 / 시외 공통)
 */
@Entity
@Table(name = "shuttle", schema = "yonsei_shuttle")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shuttle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shuttle_id")
    private Integer shuttleId;

    @Column(nullable = false, length = 100)
    private String name;

    // ShuttleType은 ShuttleTypeConverter(autoApply) 때문에 자동 변환됨
    // @JdbcTypeCode(SqlTypes.VARCHAR) + columnDefinition = "shuttle_type" 로 ENUM 타입 힌트 줌
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, columnDefinition = "shuttle_type")
    private ShuttleType type;

    @Column(nullable = false)
    private Integer capacity;

    @Builder
    private Shuttle(String name, ShuttleType type, Integer capacity) {
        this.name = name;
        this.type = type;
        this.capacity = capacity == null ? 45 : capacity;
    }
}