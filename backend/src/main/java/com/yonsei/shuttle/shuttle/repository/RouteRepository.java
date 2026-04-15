package com.yonsei.shuttle.shuttle.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsei.shuttle.shuttle.domain.Route;

public interface RouteRepository extends JpaRepository<Route, Integer> {

    List<Route> findAllByShuttle_ShuttleId(Integer shuttleId);
}