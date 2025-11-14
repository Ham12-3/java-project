package com.example.authservice.web.dto;

import java.time.OffsetDateTime;

public class CustomerAuditLogResponse {

    private final Long id;
    private final String action;
    private final String actor;
    private final String notes;
    private final OffsetDateTime createdAt;

    public CustomerAuditLogResponse(Long id, String action, String actor, String notes, OffsetDateTime createdAt) {
        this.id = id;
        this.action = action;
        this.actor = actor;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getActor() {
        return actor;
    }

    public String getNotes() {
        return notes;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
