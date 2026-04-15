package com.yonsei.shuttle.shuttle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsei.shuttle.shuttle.domain.Stop;

public interface StopRepository extends JpaRepository<Stop, Integer> {
}