package com.yonsei.shuttle.shuttle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsei.shuttle.shuttle.domain.IntercityShuttle;

public interface IntercityShuttleRepository extends JpaRepository<IntercityShuttle, Integer> {

    Optional<IntercityShuttle> findByShuttle_ShuttleId(Integer shuttleId);
}