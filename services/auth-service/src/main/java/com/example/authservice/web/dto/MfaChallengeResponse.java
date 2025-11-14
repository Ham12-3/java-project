package com.example.authservice.web.dto;

import java.time.OffsetDateTime;

public class MfaChallengeResponse {

    private final Long challengeId;
    private final OffsetDateTime expiresAt;

    public MfaChallengeResponse(Long challengeId, OffsetDateTime expiresAt) {
        this.challengeId = challengeId;
        this.expiresAt = expiresAt;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }
}
