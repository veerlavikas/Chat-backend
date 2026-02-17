package com.chat.backend.service;

import com.chat.backend.repository.MediaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class MediaService {

    private final MediaRepository repo;
    // This is where files will be stored on Render
    private final String uploadDir = "uploads/media/";

    public MediaService(MediaRepository repo) {
        this.repo = repo;
        // Create the directory if it doesn't exist
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        // 1. Generate unique filename to avoid overwrites
        String originalName = file.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;

        // 2. Save file to disk
        Path path = Paths.get(uploadDir + fileName);
        Files.write(path, file.getBytes());

        return fileName; // Return the new name to be saved in the database
    }
}