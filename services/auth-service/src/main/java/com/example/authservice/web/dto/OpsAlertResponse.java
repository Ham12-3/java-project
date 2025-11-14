package com.example.authservice.web.dto;

import java.time.OffsetDateTime;

public class OpsAlertResponse {

    private final String id;
    private final String type;
    private final String severity;
    private final String description;
    private final String status;
    private final OffsetDateTime createdAt;

    public OpsAlertResponse(String id,
                            String type,
                            String severity,
                            String description,
                            String status,
                            OffsetDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.severity = severity;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getSeverity() {
        return severity;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
