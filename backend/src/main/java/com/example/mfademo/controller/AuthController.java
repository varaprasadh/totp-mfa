package com.example.mfademo.controller;

import com.example.mfademo.dto.*;
import com.example.mfademo.entity.User;
import com.example.mfademo.repository.UserRepository;
import com.example.mfademo.security.JwtUtils;
import com.example.mfademo.security.UserDetailsImpl;
import com.example.mfademo.service.MfaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3001", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    PasswordEncoder encoder;
    
    @Autowired
    JwtUtils jwtUtils;
    
    @Autowired
    MfaService mfaService;
    
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (userDetails.isMfaEnabled()) {
            if (loginRequest.getTotpCode() == null || loginRequest.getTotpCode().isEmpty()) {
                return ResponseEntity.ok(new JwtResponse(null, userDetails.getId(), 
                        userDetails.getUsername(), userDetails.getEmail(), true, true));
            }
            
            User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            if (!mfaService.verifyCode(user.getMfaSecret(), loginRequest.getTotpCode())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid TOTP code!"));
            }
        }
        
        String jwt = jwtUtils.generateJwtToken(userDetails.getUsername());
        
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), 
                userDetails.getUsername(), userDetails.getEmail(), userDetails.isMfaEnabled(), false));
    }
    
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }
        
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }
        
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        
        userRepository.save(user);
        
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    
    @PostMapping("/mfa/setup")
    public ResponseEntity<?> setupMfa(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        
        String secret = mfaService.generateSecret();
        user.setMfaSecret(secret);
        userRepository.save(user);
        
        String qrCodeUrl = mfaService.generateQRUrl(user.getUsername(), secret);
        
        try {
            String qrCodeImage = mfaService.generateQRCodeImage(qrCodeUrl);
            Map<String, String> response = new HashMap<>();
            response.put("secret", secret);
            response.put("qrCodeUrl", qrCodeUrl);
            response.put("qrCodeImage", qrCodeImage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error generating QR code"));
        }
    }
    
    @PostMapping("/mfa/verify")
    public ResponseEntity<?> verifyMfa(@RequestBody Map<String, String> request, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        
        String code = request.get("code");
        if (mfaService.verifyCode(user.getMfaSecret(), code)) {
            user.setMfaEnabled(true);
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse("MFA enabled successfully!"));
        }
        
        return ResponseEntity.badRequest().body(new MessageResponse("Invalid verification code"));
    }
    
    @PostMapping("/mfa/disable")
    public ResponseEntity<?> disableMfa(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        
        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        userRepository.save(user);
        
        return ResponseEntity.ok(new MessageResponse("MFA disabled successfully!"));
    }
}