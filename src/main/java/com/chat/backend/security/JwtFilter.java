package com.chat.backend.security;

import com.chat.backend.entity.User;
import com.chat.backend.service.UserService; // ✅ Import your service
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
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

@Component
public class JwtFilter extends OncePerRequestFilter { // ✅ Safer Filter

    @Autowired
    private UserService userService; // ✅ Use Service instead of Repo

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String idToken = authHeader.substring(7);

            try {
                // 1. Verify the token with Firebase Admin SDK
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
                
                // 2. Extract phone number
                String firebasePhone = (String) decodedToken.getClaims().get("phone_number");

                if (firebasePhone != null) {
                    // 3. Normalize the phone number
                    String normalizedPhone = firebasePhone.replace("+91", "").trim();

                    // ✅ 4. Sync User: Finds existing OR creates a brand new user!
                    User user = userService.syncUser(normalizedPhone);

                    if (user != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // 5. Set the user in the Security Context
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                user, null, Collections.emptyList()
                        );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }

            } catch (Exception e) {
                System.err.println("Firebase Auth Error: " + e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}