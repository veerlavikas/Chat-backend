package com.chat.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import jakarta.annotation.PostConstruct;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FireBaseConfig {

    @PostConstruct
    public void init() throws IOException {
        // Prevent double initialization
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }

        InputStream serviceAccount;
        
        // 1. Check if we are in Production (Render) and have the Environment Variable
        String firebaseEnv = System.getenv("FIREBASE_CREDENTIALS");

        if (firebaseEnv != null && !firebaseEnv.trim().isEmpty()) {
            // Read from the Environment Variable on Render
            serviceAccount = new ByteArrayInputStream(firebaseEnv.getBytes(StandardCharsets.UTF_8));
            System.out.println("Firebase initialized via Environment Variable.");
        } else {
            // 2. Fall back to local file for development on your PC
            // ✅ ClassPathResource is the safe way to read files in Spring Boot
            serviceAccount = new ClassPathResource("serviceAccountKey.json").getInputStream();
            System.out.println("Firebase initialized via local JSON file.");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
    }
}