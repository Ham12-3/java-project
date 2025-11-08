package com.example.authservice.web.dto;

public class RegisterResponse {

    private final String username;
    private final boolean mfaEnabled;
    private final String mfaSecret;

    public RegisterResponse(String username, boolean mfaEnabled, String mfaSecret) {
        this.username = username;
        this.mfaEnabled = mfaEnabled;
        this.mfaSecret = mfaSecret;
    }

    public String getUsername() {
        return username;
    }

    public boolean isMfaEnabled() {
        return mfaEnabled;
    }

    public String getMfaSecret() {
        return mfaSecret;
    }
}
