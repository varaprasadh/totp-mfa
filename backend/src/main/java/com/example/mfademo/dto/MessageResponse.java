package com.example.mfademo.dto;

public class MessageResponse {
    private String message;
    
    // Default constructor
    public MessageResponse() {}
    
    // Constructor
    public MessageResponse(String message) {
        this.message = message;
    }
    
    // Getter and setter
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}