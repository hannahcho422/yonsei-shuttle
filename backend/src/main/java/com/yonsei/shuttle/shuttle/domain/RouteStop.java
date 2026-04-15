package com.yonsei.shuttle.shuttle.domain;

import java.time.Duration;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 노선별 경유 정류장 (+ 순서 + 이전/다음 정류장 linked list)
 * PostgreSQL INTERVAL 타입은 Hibernate 6.2+ 에서 java.time.Duration으로 자동 매핑됨
 */
@Entity
@Table(
        name = "route_stop",
        schema = "yonsei_shuttle",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_route_stop", columnNames = {"route_id", "stop_id"}),
                @UniqueConstraint(name = "uq_route_seq",  columnNames = {"route_id", "sequence"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RouteStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_stop_id")
    private Integer routeStopId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_id", nullable = false)
    private Stop stop;

    @Column(nullable = false)
    private Integer sequence;

    /**
     * 출발지 대비 소요 시간 (PostgreSQL INTERVAL ↔ Java Duration)
     */
    @Column(name = "arrival_time", nullable = false, columnDefinition = "interval")
    @JdbcTypeCode(SqlTypes.INTERVAL_SECOND)
    private Duration arrivalTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prev_stop_id")
    private Stop prevStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_stop_id")
    private Stop nextStop;

    @Builder
    private RouteStop(Route route, Stop stop, Integer sequence, Duration arrivalTime,
                      Stop prevStop, Stop nextStop) {
        this.route = route;
        this.stop = stop;
        this.sequence = sequence;
        this.arrivalTime = arrivalTime == null ? Duration.ZERO : arrivalTime;
        this.prevStop = prevStop;
        this.nextStop = nextStop;
    }

    // ===== 비즈니스 메서드 =====
    public void updateSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public void updateArrivalTime(Duration arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void updateNeighbors(Stop prevStop, Stop nextStop) {
        this.prevStop = prevStop;
        this.nextStop = nextStop;
    }
}