package com.example.authservice.domain;

public final class UserRegistrationValidationException extends RuntimeException {
    public UserRegistrationValidationException(String message) {
        super(message);
    }
}
