package com.example.authservice.web.dto;

import java.time.OffsetDateTime;

public class CustomerResponse {

    private Long id;
    private String fullName;
    private String email;
    private String notes;
    private String status;
    private OffsetDateTime createdAt;

    public CustomerResponse(Long id, String fullName, String email, String notes, String status, OffsetDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.notes = notes;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getNotes() {
        return notes;
    }

    public String getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
