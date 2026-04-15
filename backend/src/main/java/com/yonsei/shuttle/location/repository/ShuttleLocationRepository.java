package com.yonsei.shuttle.location.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsei.shuttle.location.domain.ShuttleLocation;

public interface ShuttleLocationRepository extends JpaRepository<ShuttleLocation, Integer> {

    /**
     * 셔틀의 최신 위치 1건
     */
    Optional<ShuttleLocation> findTopByShuttle_ShuttleIdOrderByUpdatedAtDesc(Integer shuttleId);
}