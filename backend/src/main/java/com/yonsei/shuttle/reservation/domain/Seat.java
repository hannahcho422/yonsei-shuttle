package com.yonsei.shuttle.reservation.domain;

import com.yonsei.shuttle.shuttle.domain.IntercityShuttle;

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
 * 시외 셔틀 좌석
 */
@Entity
@Table(
        name = "seat",
        schema = "yonsei_shuttle",
        uniqueConstraints = @UniqueConstraint(name = "uq_seat", columnNames = {"intercity_shuttle_id", "seat_num"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Integer seatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intercity_shuttle_id", nullable = false)
    private IntercityShuttle intercityShuttle;

    @Column(name = "seat_num", nullable = false)
    private Integer seatNum;

    @Builder
    private Seat(IntercityShuttle intercityShuttle, Integer seatNum) {
        this.intercityShuttle = intercityShuttle;
        this.seatNum = seatNum;
    }
}