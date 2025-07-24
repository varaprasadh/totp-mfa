package com.example.mfademo.dto;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private boolean mfaEnabled;
    private boolean requiresMfa;
    
    // Default constructor
    public JwtResponse() {}
    
    // Constructor with all fields
    public JwtResponse(String token, String type, Long id, String username, String email, boolean mfaEnabled, boolean requiresMfa) {
        this.token = token;
        this.type = type;
        this.id = id;
        this.username = username;
        this.email = email;
        this.mfaEnabled = mfaEnabled;
        this.requiresMfa = requiresMfa;
    }
    
    // Constructor without type (uses default "Bearer")
    public JwtResponse(String token, Long id, String username, String email, boolean mfaEnabled, boolean requiresMfa) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.mfaEnabled = mfaEnabled;
        this.requiresMfa = requiresMfa;
    }
    
    // Getters and setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public boolean isMfaEnabled() { return mfaEnabled; }
    public void setMfaEnabled(boolean mfaEnabled) { this.mfaEnabled = mfaEnabled; }
    
    public boolean isRequiresMfa() { return requiresMfa; }
    public void setRequiresMfa(boolean requiresMfa) { this.requiresMfa = requiresMfa; }
}