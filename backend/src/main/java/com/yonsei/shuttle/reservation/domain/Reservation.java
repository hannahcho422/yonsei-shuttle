package com.yonsei.shuttle.reservation.domain;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.yonsei.shuttle.common.exception.CustomException;
import com.yonsei.shuttle.common.exception.ErrorCode;
import com.yonsei.shuttle.shuttle.domain.IntercityShuttle;
import com.yonsei.shuttle.shuttle.domain.Schedule;
import com.yonsei.shuttle.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 시외 셔틀 예약
 */
@Entity
@Table(
        name = "reservation",
        schema = "yonsei_shuttle",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_reservation_seat",
                columnNames = {"seat_id", "schedule_id", "travel_date"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Integer reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intercity_shuttle_id", nullable = false)
    private IntercityShuttle intercityShuttle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(name = "travel_date", nullable = false)
    private LocalDate travelDate;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, columnDefinition = "reservation_status")
    private ReservationStatus status;

    @Column(name = "reserved_at", nullable = false, updatable = false)
    private OffsetDateTime reservedAt;

    @Column(name = "cancelled_at")
    private OffsetDateTime cancelledAt;

    @Builder
    private Reservation(User user, IntercityShuttle intercityShuttle, Seat seat,
                        Schedule schedule, LocalDate travelDate) {
        this.user = user;
        this.intercityShuttle = intercityShuttle;
        this.seat = seat;
        this.schedule = schedule;
        this.travelDate = travelDate;
        this.status = ReservationStatus.RESERVED;
    }

    @PrePersist
    protected void onCreate() {
        if (this.reservedAt == null) this.reservedAt = OffsetDateTime.now();
    }

    // ===== 비즈니스 메서드 =====
    public void cancel() {
        if (this.status == ReservationStatus.CANCELLED) {
            throw new CustomException(ErrorCode.RESERVATION_ALREADY_CANCELLED);
        }
        this.status = ReservationStatus.CANCELLED;
        this.cancelledAt = OffsetDateTime.now();
    }

    public boolean isOwnedBy(Integer userId) {
        return this.user.getUserId().equals(userId);
    }
}