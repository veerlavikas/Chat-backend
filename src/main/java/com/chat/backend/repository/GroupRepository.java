package com.chat.backend.repository;

import com.chat.backend.entity.ChatGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<ChatGroup, Long> {
    
    /**
     * ✅ Find groups where the provided phone number is a member.
     * Updated to use String phone instead of Long ID.
     */
    @Query("SELECT g FROM ChatGroup g JOIN g.memberPhones p WHERE p = :phone")
    List<ChatGroup> findGroupsByMemberPhone(@Param("phone") String phone);

    /**
     * ✅ Alternative: If you are using @ElementCollection for phone numbers
     */
    @Query("SELECT g FROM ChatGroup g WHERE :phone MEMBER OF g.memberPhones")
    List<ChatGroup> findMyGroupsByPhone(@Param("phone") String phone);
}