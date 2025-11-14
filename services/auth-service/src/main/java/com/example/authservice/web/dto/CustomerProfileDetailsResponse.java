package com.example.authservice.web.dto;

import java.time.OffsetDateTime;
import java.util.List;

public class CustomerProfileDetailsResponse {

    private final CustomerSummaryResponse summary;
    private final String notes;
    private final OffsetDateTime submittedAt;
    private final OffsetDateTime approvedAt;
    private final List<CustomerDocumentResponse> documents;
    private final List<CustomerAuditLogResponse> auditLog;
    private final AccountSummaryResponse account;

    public CustomerProfileDetailsResponse(CustomerSummaryResponse summary,
                                          String notes,
                                          OffsetDateTime submittedAt,
                                          OffsetDateTime approvedAt,
                                          List<CustomerDocumentResponse> documents,
                                          List<CustomerAuditLogResponse> auditLog,
                                          AccountSummaryResponse account) {
        this.summary = summary;
        this.notes = notes;
        this.submittedAt = submittedAt;
        this.approvedAt = approvedAt;
        this.documents = documents;
        this.auditLog = auditLog;
        this.account = account;
    }

    public CustomerSummaryResponse getSummary() {
        return summary;
    }

    public String getNotes() {
        return notes;
    }

    public OffsetDateTime getSubmittedAt() {
        return submittedAt;
    }

    public OffsetDateTime getApprovedAt() {
        return approvedAt;
    }

    public List<CustomerDocumentResponse> getDocuments() {
        return documents;
    }

    public List<CustomerAuditLogResponse> getAuditLog() {
        return auditLog;
    }

    public AccountSummaryResponse getAccount() {
        return account;
    }
}
