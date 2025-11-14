package com.example.authservice.service;

import com.example.authservice.domain.AuthUser;
import com.example.authservice.domain.AuthUserRepository;
import com.example.authservice.domain.PasswordResetToken;
import com.example.authservice.domain.PasswordResetTokenRepository;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {
    private static final Duration TOKEN_TTL = Duration.ofMinutes(30);

    private final PasswordResetTokenRepository tokens;
    private final AuthUserRepository users;
    private final PasswordEncoder encoder;
    private final SecureRandom random = new SecureRandom();

    public PasswordResetService(PasswordResetTokenRepository tokens,
                                AuthUserRepository users,
                                PasswordEncoder encoder) {
        this.tokens = tokens;
        this.users = users;
        this.encoder = encoder;
    }

    public String requestReset(String email) {
        return users.findByEmailIgnoreCase(email)
            .map(user -> {
                String token = Base64.getUrlEncoder().withoutPadding()
                        .encodeToString(random.generateSeed(32));
                PasswordResetToken entity = new PasswordResetToken(user, token, Instant.now().plus(TOKEN_TTL));
                tokens.save(entity);
                return token; // in production, send via email instead
            })
            .orElse("");
    }

    @Transactional
    public void confirmReset(String token, String newPassword) {
        PasswordResetToken entity = tokens.findByTokenAndUsedFalse(token)
            .orElse(null);
        if (entity == null || entity.getExpiresAt().isBefore(Instant.now())) {
            return;
        }
        AuthUser user = entity.getUser();
        user.setPasswordHash(encoder.encode(newPassword));
        entity.markUsed();
    }
}
