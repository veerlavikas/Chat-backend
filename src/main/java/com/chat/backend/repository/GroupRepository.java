package com.chat.backend.repository;

import com.chat.backend.entity.ChatGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<ChatGroup, Long> {
    
    // ✅ Find groups where the memberIds list contains 'myId'
    // Note: Since memberIds is an @ElementCollection, we use the 'MEMBER OF' keyword
    @Query("SELECT g FROM ChatGroup g WHERE :myId MEMBER OF g.memberIds")
    List<ChatGroup> findMyGroups(@Param("myId") Long myId);
}