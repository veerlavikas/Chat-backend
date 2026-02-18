package com.chat.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class GeminiService {

    // ✅ Your API Key is now integrated
    private final String API_KEY = "AIzaSyB1p-xBfh6aeGKtAPH73zIcJcjcS2NJsng"; 
    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;

    public String getGeminiResponse(String userMessage) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Structure the request for Gemini 1.5 Flash
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", userMessage);

            Map<String, Object> contentMap = new HashMap<>();
            contentMap.put("parts", Collections.singletonList(textPart));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", Collections.singletonList(contentMap));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Execute the POST request
            ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_URL, entity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
                
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> firstCandidate = candidates.get(0);
                    Map<String, Object> contentRes = (Map<String, Object>) firstCandidate.get("content");
                    List<Map<String, Object>> partsRes = (List<Map<String, Object>>) contentRes.get("parts");
                    
                    return (String) partsRes.get(0).get("text");
                }
            }
            return "I'm thinking, but I couldn't find the words. 🤖";

        } catch (Exception e) {
            System.err.println("GEMINI ERROR: " + e.getMessage());
            return "Meta AI is currently unavailable. Please try again later.";
        }
    }
}