package com.example.mfademo.security;

import com.example.mfademo.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {
    private Long id;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private boolean mfaEnabled;
    
    // Default constructor
    public UserDetailsImpl() {}
    
    // Constructor
    public UserDetailsImpl(Long id, String username, String email, String password, boolean mfaEnabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.mfaEnabled = mfaEnabled;
    }
    
    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.isMfaEnabled()
        );
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    // Getters for custom fields
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public boolean isMfaEnabled() { return mfaEnabled; }
}