package com.chat.backend.service;

import com.chat.backend.entity.MediaFile;
import com.chat.backend.repository.MediaRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository repo;
    @Autowired

    public MediaService(MediaRepository repo) {
		super();
		this.repo = repo;
	}


	public MediaFile save(MediaFile file) {
        return repo.save(file);
    }
}
