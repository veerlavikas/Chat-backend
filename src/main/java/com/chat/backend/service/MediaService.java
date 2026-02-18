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

    // IMPORTANT: Make sure this matches your Render URL exactly
    private final String BASE_URL = "https://chat-backend-9v66.onrender.com"; 
    private final String UPLOAD_DIR = "uploads/media/";

    public MediaService(MediaRepository repo) {
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        // 1. Generate unique filename
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
             extension = originalName.substring(originalName.lastIndexOf("."));
        } else {
             extension = ".jpg"; // Default fallback
        }
        String fileName = UUID.randomUUID().toString() + extension;

        // 2. Save file to "uploads/media/"
        Path path = Paths.get(UPLOAD_DIR + fileName);
        Files.write(path, file.getBytes());

        // 3. RETURN THE FULL URL (This fixes the 404 error)
        // Access path becomes: /uploads/media/filename.jpg
        return BASE_URL + "/uploads/media/" + fileName; 
    }
}