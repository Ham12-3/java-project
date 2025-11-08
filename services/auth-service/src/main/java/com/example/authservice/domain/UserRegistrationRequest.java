package com.example.authservice.domain;

import java.util.Objects;

public final class UserRegistrationRequest {
    private final String username;
    private final String email;
    private final String password;

    public UserRegistrationRequest(String username, String email, String password) {
        this.username = Objects.requireNonNull(username, "username");
        this.email = Objects.requireNonNull(email, "email");
        this.password = Objects.requireNonNull(password, "password");
    }

    public String username() {
        return username;
    }

    public String email() {
        return email;
    }

    public String password() {
        return password;
    }
}
