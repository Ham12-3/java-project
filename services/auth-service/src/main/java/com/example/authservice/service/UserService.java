package com.example.authservice.service;

import com.example.authservice.domain.AuthUser;
import com.example.authservice.domain.AuthUserRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final AuthUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final MfaService mfaService;

    public UserService(AuthUserRepository repository, PasswordEncoder passwordEncoder, MfaService mfaService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.mfaService = mfaService;
    }

    @Transactional
    public RegistrationResult register(String username, String email, String password, boolean mfaEnabled) {
        if (repository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        String hashed = passwordEncoder.encode(password);
        String secret = null;
        boolean enabled = false;
        if (mfaEnabled) {
            secret = mfaService.generateSecret();
            enabled = true;
        }

        AuthUser user = new AuthUser(username, email, hashed, enabled, secret);
        repository.save(user);
        return new RegistrationResult(user, secret);
    }

    public Optional<AuthUser> findByUsername(String username) {
        return repository.findByUsernameIgnoreCase(username);
    }

    public record RegistrationResult(AuthUser user, String mfaSecret) {
    }
}
