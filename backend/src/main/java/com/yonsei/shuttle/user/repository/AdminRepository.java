package com.yonsei.shuttle.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsei.shuttle.user.domain.Admin;
import com.yonsei.shuttle.user.domain.User;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Optional<Admin> findByUser(User user);

    Optional<Admin> findByUser_UserId(Integer userId);
}