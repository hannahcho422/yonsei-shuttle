package com.yonsei.shuttle.shuttle.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 시내 셔틀 (위치 시뮬레이션 대상)
 */
@Entity
@Table(name = "city_shuttle", schema = "yonsei_shuttle")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CityShuttle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_shuttle_id")
    private Integer cityShuttleId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shuttle_id", nullable = false, unique = true)
    private Shuttle shuttle;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal speed;

    @Builder
    private CityShuttle(Shuttle shuttle, BigDecimal speed) {
        this.shuttle = shuttle;
        this.speed = speed == null ? new BigDecimal("40.00") : speed;
    }
}