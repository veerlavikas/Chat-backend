package com.chat.backend.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    // 🎁 TIP: You should eventually put this key in your application.properties!
    // For now, just paste your Brevo API key right here to test it.
	@Value("${brevo.api.key}")
	private String BREVO_API_KEY; 
    
    private final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    public void sendOtpEmail(String toEmail, String otp) {
        RestTemplate restTemplate = new RestTemplate();
        
        // 1. Set the secure headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", BREVO_API_KEY);
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");

        // 2. Build the beautiful email payload
        Map<String, Object> body = Map.of(
            "sender", Map.of("name", "Vicky's Chat App", "email", "noreply@vickychatapp.com"),
            "to", List.of(Map.of("email", toEmail)),
            "subject", "Your Verification Code",
            "htmlContent", "<div style='font-family: Arial; padding: 20px; text-align: center;'>"
                    + "<h2>Welcome to Chat App!</h2>"
                    + "<p>Your one-time verification code is:</p>"
                    + "<h1 style='color: #0A84FF; letter-spacing: 5px;'>" + otp + "</h1>"
                    + "</div>"
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // 3. Send it through HTTPS (Render can't block this!)
        try {
            restTemplate.exchange(BREVO_URL, HttpMethod.POST, request, String.class);
            System.out.println("✅ OTP sent successfully via Brevo API to: " + toEmail);
        } catch (Exception e) {
            System.out.println("❌ Failed to send OTP: " + e.getMessage());
        }
    }
}