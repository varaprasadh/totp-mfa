package com.example.mfademo.controller;

import com.example.mfademo.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", userDetails.getId());
        profile.put("username", userDetails.getUsername());
        profile.put("email", userDetails.getEmail());
        profile.put("mfaEnabled", userDetails.isMfaEnabled());
        
        return ResponseEntity.ok(profile);
    }
}