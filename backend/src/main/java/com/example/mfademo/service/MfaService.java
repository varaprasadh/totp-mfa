package com.example.mfademo.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
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
            boolean isValid = googleAuthenticator.authorize(secret, verificationCode);
            System.out.println("Current time window result: " + isValid);
            return isValid;
            
        } catch (NumberFormatException e) {
            System.out.println("MFA Verification failed - Invalid code format: " + code);
            return false;
        }
    }
}