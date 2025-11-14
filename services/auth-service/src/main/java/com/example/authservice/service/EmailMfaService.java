package com.example.authservice.service;

import com.example.authservice.domain.AuthUser;
import com.example.authservice.domain.EmailMfaChallenge;
import com.example.authservice.domain.EmailMfaChallengeRepository;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EmailMfaService {

    private static final Duration EXPIRY = Duration.ofMinutes(5);
    private final SecureRandom secureRandom = new SecureRandom();

    private final EmailMfaChallengeRepository challenges;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public EmailMfaService(EmailMfaChallengeRepository challenges,
                           PasswordEncoder passwordEncoder,
                           EmailService emailService) {
        this.challenges = challenges;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public IssuedChallenge issue(AuthUser user) {
        String code = generateCode();
        EmailMfaChallenge challenge = new EmailMfaChallenge();
        challenge.setUsername(user.getUsername());
        challenge.setCodeHash(passwordEncoder.encode(code));
        challenge.setExpiresAt(OffsetDateTime.now().plus(EXPIRY));
        challenges.save(challenge);

        String body = """
                Hi %s,

                Use the one-time code %s to complete your sign-in. This code expires in %d minutes.

                If you did not request this code, please ignore this email.

                – Fintech App Security
                """.formatted(user.getUsername(), code, EXPIRY.toMinutes());
        emailService.sendPlainText(user.getEmail(), "Your Fintech App security code", body);
        return new IssuedChallenge(challenge.getId(), challenge.getExpiresAt());
    }

    @Transactional
    public boolean verify(String username, String code) {
        return challenges.findFirstByUsernameAndConsumedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(
                        username, OffsetDateTime.now())
                .filter(challenge -> passwordEncoder.matches(code, challenge.getCodeHash()))
                .map(challenge -> {
                    challenge.setConsumedAt(OffsetDateTime.now());
                    return true;
                })
                .orElse(false);
    }

    private String generateCode() {
        int value = secureRandom.nextInt(1_000_000);
        return String.format("%06d", value);
    }

    public record IssuedChallenge(Long challengeId, OffsetDateTime expiresAt) {
    }
}
