package com.chat.backend.repository;

import com.chat.backend.entity.ChatGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<ChatGroup, Long> {
    // Find all groups created by a specific user
    List<ChatGroup> findByAdminId(Long adminId);
}