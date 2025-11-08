package com.example.authservice.domain;

public final class UserRegistrationResponse {
    private final String userId;

    public UserRegistrationResponse(String userId) {
        this.userId = userId;
    }

    public String userId() {
        return userId;
    }
}
