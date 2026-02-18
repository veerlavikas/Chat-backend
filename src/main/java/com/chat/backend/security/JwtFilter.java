package com.chat.backend.security;

import com.chat.backend.entity.User;
import com.chat.backend.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter implements Filter {

    @Autowired
    private UserRepository userRepo;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String idToken = authHeader.substring(7);

            try {
                // ✅ 1. Verify the token with Firebase Admin SDK
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
                
                // ✅ 2. Extract phone number from the Claims map
                // Firebase stores this under the "phone_number" key
                String firebasePhone = (String) decodedToken.getClaims().get("phone_number");

                if (firebasePhone != null) {
                    // ✅ 3. Normalize the phone number
                    // Firebase returns +91XXXXXXXXXX. If your DB stores just XXXXXXXXXX, strip the prefix.
                    String normalizedPhone = firebasePhone.replace("+91", "").trim();

                    // ✅ 4. Find the user in your TiDB database
                    User user = userRepo.findByPhone(normalizedPhone);

                    if (user != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // ✅ 5. Set the user in the Security Context
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                user, 
                                null, 
                                Collections.emptyList()
                        );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }

            } catch (Exception e) {
                // Token is invalid, expired, or user not found
                System.err.println("Firebase Auth Error: " + e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(req, res);
    }
}