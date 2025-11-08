package com.example.authservice.domain;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryUserRepository implements UserRepository {
    private final Map<String, AuthAppUser> usersById = new ConcurrentHashMap<>();
    private final Map<String, String> idByUsername = new ConcurrentHashMap<>();
    private final Map<String, String> idByEmail = new ConcurrentHashMap<>();

    @Override
    public Optional<AuthAppUser> findByUsername(String username) {
        String id = idByUsername.get(username.toLowerCase());
        return Optional.ofNullable(id).map(usersById::get);
    }

    @Override
    public Optional<AuthAppUser> findByEmail(String email) {
        String id = idByEmail.get(email.toLowerCase());
        return Optional.ofNullable(id).map(usersById::get);
    }

    @Override
    public AuthAppUser save(AuthAppUser user) {
        usersById.put(user.id(), user);
        idByUsername.put(user.username().toLowerCase(), user.id());
        idByEmail.put(user.email().toLowerCase(), user.id());
        return user;
    }
}
