package com.example.authservice.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class AuthAppUser {
    private final String id;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final Instant registeredAt;

    private AuthAppUser(String id, String username, String email, String passwordHash, Instant registeredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.username = Objects.requireNonNull(username, "username");
        this.email = Objects.requireNonNull(email, "email");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
        this.registeredAt = Objects.requireNonNull(registeredAt, "registeredAt");
    }

    public static AuthAppUser createNew(String username, String email, String passwordHash) {
        return new AuthAppUser(UUID.randomUUID().toString(), username, email, passwordHash, Instant.now());
    }

    public String id() {
        return id;
    }

    public String username() {
        return username;
    }

    public String email() {
        return email;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public Instant registeredAt() {
        return registeredAt;
    }
}
