package com.example.authservice;

import com.example.authservice.domain.InMemoryUserRepository;
import com.example.authservice.domain.UserRegistrationRequest;
import com.example.authservice.domain.UserRegistrationResponse;
import com.example.authservice.domain.UserRegistrationService;
import com.example.authservice.domain.UserRegistrationValidationException;

import junit.framework.TestCase;

public class UserRegistrationServiceTest extends TestCase {
    private InMemoryUserRepository repository;
    private UserRegistrationService service;

    @Override
    protected void setUp() {
        repository = new InMemoryUserRepository();
        service = new UserRegistrationService(repository);
    }

    public void testSuccessfulRegistrationPersistsUser() {
        UserRegistrationResponse response = service.registerUser(
                new UserRegistrationRequest("sampleUser", "sample@example.com", "SupremelySafe123")
        );

        assertNotNull(response.userId());
        assertTrue(repository.findByUsername("sampleuser").isPresent());
    }

    public void testDuplicateEmailThrowsValidationException() {
        service.registerUser(new UserRegistrationRequest("user1", "same@example.com", "PasswordOne123"));

        try {
            service.registerUser(new UserRegistrationRequest("user2", "same@example.com", "PasswordTwo123"));
            fail("Expected validation exception");
        } catch (UserRegistrationValidationException exception) {
            assertEquals("email already registered", exception.getMessage());
        }
    }

    public void testPasswordComplexityIsEnforced() {
        try {
            service.registerUser(new UserRegistrationRequest("user3", "complex@example.com", "alllowercase"));
            fail("Expected validation exception");
        } catch (UserRegistrationValidationException exception) {
            assertTrue(exception.getMessage().contains("password"));
        }
    }
}
