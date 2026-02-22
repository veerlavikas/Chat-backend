package com.chat.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // ✅ Enable /queue for private 1-on-1 messages 
        // ✅ Keep /topic for group chats or status updates
        config.enableSimpleBroker("/topic", "/queue"); 
        
        config.setApplicationDestinationPrefixes("/app");
        
        // ✅ This prefix is used for private messaging: /user/{phone}/queue/messages
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // ✅ Vital for Expo Go / Mobile testing
                .withSockJS();
    }
}