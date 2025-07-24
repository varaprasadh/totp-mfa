package com.example.mfademo.dto;

public class MfaSetupResponse {
    private String secret;
    private String qrCodeUrl;
    
    // Default constructor
    public MfaSetupResponse() {}
    
    // Constructor
    public MfaSetupResponse(String secret, String qrCodeUrl) {
        this.secret = secret;
        this.qrCodeUrl = qrCodeUrl;
    }
    
    // Getters and setters
    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
    
    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }
}