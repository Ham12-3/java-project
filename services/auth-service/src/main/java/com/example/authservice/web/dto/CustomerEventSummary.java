package com.example.authservice.web.dto;

import java.time.OffsetDateTime;

public class CustomerEventSummary {

    private final Long customerId;
    private final String customerName;
    private final String status;
    private final OffsetDateTime occurredAt;

    public CustomerEventSummary(Long customerId, String customerName, String status, OffsetDateTime occurredAt) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.status = status;
        this.occurredAt = occurredAt;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getStatus() {
        return status;
    }

    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }
}
