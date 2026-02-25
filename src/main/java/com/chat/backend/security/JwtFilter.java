package com.chat.backend.security;

import com.chat.backend.entity.User;
import com.chat.backend.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                String phone = jwtUtil.extractPhone(token);

                if (phone != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    
                    Optional<User> userOpt = userService.getByPhone(phone);

                    // ✅ FIX 1: Use .isPresent() instead of != null
                    if (userOpt.isPresent()) {
                        
                        // ✅ FIX 2: Extract the actual user from the Optional box using .get()
                        User actualUser = userOpt.get();
                        
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                actualUser, null, Collections.emptyList() // Pass actualUser here!
                        );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }

            } catch (Exception e) {
                System.err.println("JWT Validation Error: " + e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}