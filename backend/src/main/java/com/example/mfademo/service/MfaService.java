package com.example.mfademo.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class MfaService {
    private final GoogleAuthenticator googleAuthenticator;
    
    public MfaService() {
        googleAuthenticator = new GoogleAuthenticator();
    }
    
    public String generateSecret() {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        return key.getKey();
    }
    
    public String generateQRUrl(String username, String secret) {
        // Create proper OTP Auth URL format with URL encoding
        String issuer = "MFA Demo App";
        String label = issuer + ":" + username;
        
        // Debug the QR URL generation
        System.out.println("=== QR URL Generation Debug ===");
        System.out.println("Username: " + username);
        System.out.println("Secret: " + secret);
        System.out.println("Secret length: " + secret.length());
        
        String qrUrl = String.format("otpauth://totp/%s?secret=%s&issuer=%s",
                label.replace(" ", "%20"), secret, issuer.replace(" ", "%20"));
        
        System.out.println("Generated QR URL: " + qrUrl);
        
        return qrUrl;
    }
    
    public String generateQRCodeImage(String qrCodeText) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, 200, 200);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
    
    public boolean verifyCode(String secret, String code) {
        try {
            int verificationCode = Integer.parseInt(code);
            
            // Enhanced debugging
            System.out.println("=== MFA Verification Debug ===");
            System.out.println("Secret (first 8 chars): " + secret.substring(0, Math.min(8, secret.length())));
            System.out.println("Secret length: " + secret.length());
            System.out.println("Code: " + code);
            
            // The GoogleAuth library expects time in milliseconds, not seconds!
            long currentTimeMillis = System.currentTimeMillis();
            System.out.println("Current time (millis): " + currentTimeMillis);
            System.out.println("Current time (seconds): " + (currentTimeMillis / 1000));
            
            // First, let's see what code the library thinks should be valid right now
            try {
                int expectedCode = googleAuthenticator.getTotpPassword(secret);
                System.out.println("Expected TOTP code right now: " + String.format("%06d", expectedCode));
            } catch (Exception e) {
                System.out.println("Error getting expected TOTP: " + e.getMessage());
            }
            
            // Try the simple authorize method first (current time window)
            boolean isValid = googleAuthenticator.authorize(secret, verificationCode);
            System.out.println("Current time window result: " + isValid);
            
            if (isValid) {
                System.out.println("✅ MFA Verification SUCCESS with current time");
                return true;
            }
            
            // Try a more comprehensive approach - test if it's a timing issue
            // Let's try with time in different formats and see what works
            
            // Method 1: Try with time in milliseconds
            for (int windowOffset = -4; windowOffset <= 4; windowOffset++) {
                long testTimeMillis = currentTimeMillis + (windowOffset * 30000L); // 30-second windows in millis
                
                try {
                    isValid = googleAuthenticator.authorize(secret, verificationCode, testTimeMillis);
                    System.out.println("Time window (millis) " + windowOffset + " (time=" + testTimeMillis + "): " + isValid);
                    
                    if (isValid) {
                        System.out.println("✅ MFA Verification SUCCESS with millis time window: " + windowOffset);
                        return true;
                    }
                } catch (Exception e) {
                    System.out.println("Error with millis time window " + windowOffset + ": " + e.getMessage());
                }
            }
            
            // Method 2: Try with time in 30-second steps (TOTP standard)
            long timeStep = currentTimeMillis / 30000L; // 30-second time steps
            System.out.println("Current time step: " + timeStep);
            
            for (int windowOffset = -4; windowOffset <= 4; windowOffset++) {
                long testTimeStep = timeStep + windowOffset;
                long testTimeMillis = testTimeStep * 30000L;
                
                try {
                    isValid = googleAuthenticator.authorize(secret, verificationCode, testTimeMillis);
                    System.out.println("Time step window " + windowOffset + " (step=" + testTimeStep + ", millis=" + testTimeMillis + "): " + isValid);
                    
                    if (isValid) {
                        System.out.println("✅ MFA Verification SUCCESS with time step: " + windowOffset);
                        return true;
                    }
                } catch (Exception e) {
                    System.out.println("Error with time step " + windowOffset + ": " + e.getMessage());
                }
            }
            
            System.out.println("❌ MFA Verification FAILED for all time windows and methods");
            return false;
            
        } catch (NumberFormatException e) {
            System.out.println("MFA Verification failed - Invalid code format: " + code);
            return false;
        }
    }
}