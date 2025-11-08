package com.example.authservice.service;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import jakarta.annotation.PostConstruct;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Service
public class MfaService {

    private static final String HMAC_ALGORITHM = "HmacSHA1";
    private static final int SECRET_LENGTH = 20;

    private final SecureRandom secureRandom = new SecureRandom();
    private TimeBasedOneTimePasswordGenerator generator;

    @PostConstruct
    void init() throws NoSuchAlgorithmException {
        generator = new TimeBasedOneTimePasswordGenerator(Duration.ofSeconds(30), 6, HMAC_ALGORITHM);
    }

    public String generateSecret() {
        byte[] secret = new byte[SECRET_LENGTH];
        secureRandom.nextBytes(secret);
        return Base64.getEncoder().encodeToString(secret);
    }

    public boolean verifyCode(String secret, String code) {
        if (secret == null || code == null || code.isBlank()) {
            return false;
        }
        try {
            Key key = toKey(secret);
            Instant now = Instant.now();
            for (int i = -1; i <= 1; i++) {
                Instant candidate = now.plus(generator.getTimeStep().multipliedBy(i));
                String expected = generator.generateOneTimePasswordString(key, candidate);
                if (expected.equals(code)) {
                    return true;
                }
            }
            return false;
        } catch (InvalidKeyException e) {
            return false;
        }
    }

    public String currentCode(String secret) {
        try {
            return generator.generateOneTimePasswordString(toKey(secret), Instant.now());
        } catch (InvalidKeyException e) {
            throw new IllegalStateException("Unable to generate MFA code", e);
        }
    }

    private SecretKey toKey(String secret) {
        byte[] decoded = Base64.getDecoder().decode(secret);
        return new SecretKeySpec(decoded, HMAC_ALGORITHM);
    }
}
