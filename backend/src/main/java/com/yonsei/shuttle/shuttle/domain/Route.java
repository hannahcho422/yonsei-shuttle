package com.yonsei.shuttle.shuttle.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 셔틀 노선
 */
@Entity
@Table(name = "route", schema = "yonsei_shuttle")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Integer routeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shuttle_id", nullable = false)
    private Shuttle shuttle;

    @Column(name = "route_name", nullable = false, length = 255)
    private String routeName;

    @Column(length = 50)
    private String direction;

    @Builder
    private Route(Shuttle shuttle, String routeName, String direction) {
        this.shuttle = shuttle;
        this.routeName = routeName;
        this.direction = direction;
    }

    // ===== 비즈니스 메서드 =====
    public void update(String routeName, String direction) {
        this.routeName = routeName;
        this.direction = direction;
    }
}