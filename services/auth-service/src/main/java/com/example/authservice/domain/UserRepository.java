package com.example.authservice.domain;

import java.util.Optional;

public interface UserRepository {
    Optional<AuthAppUser> findByUsername(String username);

    Optional<AuthAppUser> findByEmail(String email);

    AuthAppUser save(AuthAppUser user);
}
