package com.chat.backend.service;

import com.chat.backend.entity.MediaFile;
import com.chat.backend.repository.MediaRepository;
import org.springframework.stereotype.Service;

@Service
public class MediaService {

    private final MediaRepository repo;

    // Manual constructor for Spring injection
    public MediaService(MediaRepository repo) {
        this.repo = repo;
    }

    public MediaFile save(MediaFile file) {
        return repo.save(file);
    }
}