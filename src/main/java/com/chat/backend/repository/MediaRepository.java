package com.chat.backend.repository;

import com.chat.backend.entity.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<MediaFile, Long> {}
