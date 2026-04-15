package com.yonsei.shuttle.shuttle.domain;

import java.time.LocalTime;

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
 * 운행 시간표
 */
@Entity
@Table(name = "schedule", schema = "yonsei_shuttle")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Integer scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @Column(name = "day_of_week", nullable = false, length = 10)
    private String dayOfWeek;

    @Builder
    private Schedule(Route route, LocalTime departureTime, String dayOfWeek) {
        this.route = route;
        this.departureTime = departureTime;
        this.dayOfWeek = dayOfWeek == null ? "MON-FRI" : dayOfWeek;
    }

    // ===== 비즈니스 메서드 =====
    public void update(LocalTime departureTime, String dayOfWeek) {
        this.departureTime = departureTime;
        this.dayOfWeek = dayOfWeek;
    }
}