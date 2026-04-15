package com.yonsei.shuttle.shuttle.domain;

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
 * 시외 셔틀 (예약 대상)
 */
@Entity
@Table(name = "intercity_shuttle", schema = "yonsei_shuttle")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IntercityShuttle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "intercity_shuttle_id")
    private Integer intercityShuttleId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shuttle_id", nullable = false, unique = true)
    private Shuttle shuttle;

    @Builder
    private IntercityShuttle(Shuttle shuttle) {
        this.shuttle = shuttle;
    }
}