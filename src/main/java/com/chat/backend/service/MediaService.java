package com.chat.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class MediaService {

    @Autowired
    private Cloudinary cloudinary;

    /**
     * Uploads file to Cloudinary and returns PUBLIC URL
     * This will work for image / audio / video
     */
    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is empty");
        }

        Map uploadResult = cloudinary.uploader().upload(
            file.getBytes(),
            ObjectUtils.asMap(
                "resource_type", "auto",   // auto-detect image/video/audio
                "folder", "chat_app/media"
            )
        );

        // ✅ ALWAYS return secure HTTPS URL
        return uploadResult.get("secure_url").toString();
    }
}