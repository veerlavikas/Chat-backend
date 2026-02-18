package com.chat.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

@Service
public class GeminiService {

    // 🔴 REPLACE WITH YOUR ACTUAL GEMINI API KEY
    private final String API_KEY = "YOUR_GEMINI_API_KEY_HERE"; 
    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + API_KEY;

    public String getGeminiResponse(String userMessage) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // 1. Prepare Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 2. Prepare Body (Google's JSON format)
            Map<String, Object> part = new HashMap<>();
            part.put("text", userMessage);
            
            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(part));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(content));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 3. Call API
            ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_URL, entity, Map.class);
            
            // 4. Parse Response (Extract the text)
            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
            Map<String, Object> firstCandidate = candidates.get(0);
            Map<String, Object> contentRes = (Map<String, Object>) firstCandidate.get("content");
            List<Map<String, Object>> partsRes = (List<Map<String, Object>>) contentRes.get("parts");
            
            return (String) partsRes.get(0).get("text");

        } catch (Exception e) {
            e.printStackTrace();
            return "I'm having trouble thinking right now. Try again later! 🤖";
        }
    }
}