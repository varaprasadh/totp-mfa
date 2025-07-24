package com.example.mfademo.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    private String username;
    
    @NotBlank
    private String password;
    
    private String totpCode;
    
    // Default constructor
    public LoginRequest() {}
    
    // Constructor
    public LoginRequest(String username, String password, String totpCode) {
        this.username = username;
        this.password = password;
        this.totpCode = totpCode;
    }
    
    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getTotpCode() { return totpCode; }
    public void setTotpCode(String totpCode) { this.totpCode = totpCode; }
}