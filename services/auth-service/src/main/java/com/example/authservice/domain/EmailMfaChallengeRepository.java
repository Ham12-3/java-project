package com.example.authservice.domain;

import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailMfaChallengeRepository extends JpaRepository<EmailMfaChallenge, Long> {

    Optional<EmailMfaChallenge> findFirstByUsernameAndConsumedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(
            String username, OffsetDateTime now);
}
