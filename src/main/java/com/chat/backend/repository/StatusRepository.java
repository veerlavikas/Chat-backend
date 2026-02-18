package com.chat.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chat.backend.entity.Status;


public interface StatusRepository extends JpaRepository<Status, Long> {
    // Fetch statuses posted in the last 24 hours
    List<Status> findByExpiresAtAfter(LocalDateTime now);
}