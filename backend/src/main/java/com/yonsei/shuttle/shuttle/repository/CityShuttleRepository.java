package com.yonsei.shuttle.shuttle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsei.shuttle.shuttle.domain.CityShuttle;

public interface CityShuttleRepository extends JpaRepository<CityShuttle, Integer> {

    Optional<CityShuttle> findByShuttle_ShuttleId(Integer shuttleId);
}