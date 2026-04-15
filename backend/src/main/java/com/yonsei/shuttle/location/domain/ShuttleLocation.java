package com.yonsei.shuttle.location.domain;

import com.yonsei.shuttle.shuttle.domain.Shuttle;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * 셔틀 실시간 위치 (시뮬레이터가 주기적으로 INSERT)
 */
@Entity
@Table(name = "shuttle_location", schema = "yonsei_shuttle")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShuttleLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Integer locationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shuttle_id", nullable = false)
    private Shuttle shuttle;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(precision = 5, scale = 2)
    private BigDecimal heading;

    @Column(precision = 5, scale = 2)
    private BigDecimal speed;

    @Column(name = "updated_at", nullable = false, updatable = false)
    private OffsetDateTime updatedAt;

    @Builder
    private ShuttleLocation(Shuttle shuttle, BigDecimal latitude, BigDecimal longitude,
                            BigDecimal heading, BigDecimal speed, OffsetDateTime updatedAt) {
        this.shuttle = shuttle;
        this.latitude = latitude;
        this.longitude = longitude;
        this.heading = heading;
        this.speed = speed;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.updatedAt == null) this.updatedAt = OffsetDateTime.now();
    }
}