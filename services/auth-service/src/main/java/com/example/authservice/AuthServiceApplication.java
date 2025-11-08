package com.example.authservice;

import com.example.authservice.domain.InMemoryUserRepository;
import com.example.authservice.domain.UserRegistrationRequest;
import com.example.authservice.domain.UserRegistrationService;

/**
 * Minimal entry point to demonstrate the in-memory authentication service.
 */
public final class AuthServiceApplication {
    private AuthServiceApplication() {
    }

    public static void main(String[] args) {
        UserRegistrationService service =
                new UserRegistrationService(new InMemoryUserRepository());

        UserRegistrationRequest request = new UserRegistrationRequest(
                "demo-user",
                "demo.user@example.com",
                "VerySecurePassword1!"
        );

        service.registerUser(request);
    }
}
