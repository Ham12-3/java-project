package com.example.authservice.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.regex.Pattern;

public final class UserRegistrationService {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_.-]{3,32}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UserRepository userRepository;

    public UserRegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {
        validate(request);

        String normalizedEmail = request.email().toLowerCase(Locale.ROOT);
        String normalizedUsername = request.username().toLowerCase(Locale.ROOT);

        userRepository.findByEmail(normalizedEmail)
                .ifPresent(user -> {
                    throw new UserRegistrationValidationException("email already registered");
                });
        userRepository.findByUsername(normalizedUsername)
                .ifPresent(user -> {
                    throw new UserRegistrationValidationException("username already registered");
                });

        String passwordHash = sha256(request.password());
        AuthAppUser user = AuthAppUser.createNew(request.username(), normalizedEmail, passwordHash);
        AuthAppUser saved = userRepository.save(user);
        return new UserRegistrationResponse(saved.id());
    }

    private void validate(UserRegistrationRequest request) {
        if (!USERNAME_PATTERN.matcher(request.username()).matches()) {
            throw new UserRegistrationValidationException("username must be 3-32 characters and contain only alphanumeric characters or ._-"
            );
        }
        if (!EMAIL_PATTERN.matcher(request.email()).matches()) {
            throw new UserRegistrationValidationException("invalid email address");
        }
        if (request.password().length() < 12) {
            throw new UserRegistrationValidationException("password must be at least 12 characters long");
        }
        if (request.password().chars().noneMatch(Character::isUpperCase)
                || request.password().chars().noneMatch(Character::isLowerCase)
                || request.password().chars().noneMatch(Character::isDigit)) {
            throw new UserRegistrationValidationException(
                    "password must contain upper case, lower case, and numeric characters");
        }
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hashed.length * 2);
            for (byte b : hashed) {
                sb.append(String.format(Locale.ROOT, "%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
