package com.yonsei.shuttle.shuttle.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsei.shuttle.shuttle.domain.Shuttle;
import com.yonsei.shuttle.shuttle.domain.ShuttleType;

public interface ShuttleRepository extends JpaRepository<Shuttle, Integer> {

    List<Shuttle> findAllByType(ShuttleType type);
}