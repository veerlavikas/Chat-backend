package com.chat.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chat.backend.entity.StatusComment;

public interface StatusCommentRepository extends JpaRepository<StatusComment, Long> {
    List<StatusComment> findByStatusId(Long statusId);
}