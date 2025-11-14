package com.example.authservice.service;

import jakarta.annotation.PostConstruct;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MfaService {

    private static final String HMAC_ALGORITHM = "HmacSHA1";
    private static final Duration TIME_STEP = Duration.ofSeconds(30);
    private static final int SECRET_LENGTH = 20;
    private static final int CODE_DIGITS = 6;
    private static final int MODULUS = (int) Math.pow(10, CODE_DIGITS);
    private static final int WINDOW = 1;

    private final SecureRandom secureRandom = new SecureRandom();
    private Mac mac;

    @PostConstruct
    void init() throws NoSuchAlgorithmException {
        mac = Mac.getInstance(HMAC_ALGORITHM);
    }

    public String generateSecret() {
        byte[] secret = new byte[SECRET_LENGTH];
        secureRandom.nextBytes(secret);
        return Base64.getEncoder().encodeToString(secret);
    }

    public boolean verifyCode(String secret, String code) {
        if (!StringUtils.hasText(secret) || !StringUtils.hasText(code)) {
            return false;
        }
        try {
            SecretKey key = toKey(secret);
            long counter = counterForInstant(Instant.now());
            String trimmedCode = code.trim();
            for (int i = -WINDOW; i <= WINDOW; i++) {
                if (generateCode(key, counter + i).equals(trimmedCode)) {
                    return true;
                }
            }
            return false;
        } catch (RuntimeException | InvalidKeyException e) {
            return false;
        }
    }

    public String currentCode(String secret) {
        if (!StringUtils.hasText(secret)) {
            throw new IllegalArgumentException("Secret must be provided");
        }
        try {
            SecretKey key = toKey(secret);
            return generateCode(key, counterForInstant(Instant.now()));
        } catch (InvalidKeyException e) {
            throw new IllegalStateException("Unable to generate MFA code", e);
        }
    }

    private String generateCode(SecretKey key, long counter) throws InvalidKeyException {
        Mac macInstance = duplicateMac();
        macInstance.init(key);
        byte[] data = ByteBuffer.allocate(8).putLong(counter).array();
        byte[] hash = macInstance.doFinal(data);

        int offset = hash[hash.length - 1] & 0x0F;
        int binary =
            ((hash[offset] & 0x7F) << 24)
                | ((hash[offset + 1] & 0xFF) << 16)
                | ((hash[offset + 2] & 0xFF) << 8)
                | (hash[offset + 3] & 0xFF);

        int otp = Math.floorMod(binary, MODULUS);
        return String.format("%0" + CODE_DIGITS + "d", otp);
    }

    private Mac duplicateMac() {
        try {
            return (Mac) mac.clone();
        } catch (CloneNotSupportedException e) {
            try {
                return Mac.getInstance(HMAC_ALGORITHM);
            } catch (NoSuchAlgorithmException ex) {
                throw new IllegalStateException("HMAC algorithm unavailable", ex);
            }
        }
    }

    private long counterForInstant(Instant instant) {
        return instant.getEpochSecond() / TIME_STEP.getSeconds();
    }

    private SecretKey toKey(String secret) {
        byte[] decoded = Base64.getDecoder().decode(secret);
        return new SecretKeySpec(decoded, HMAC_ALGORITHM);
    }
}
