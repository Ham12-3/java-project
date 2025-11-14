package com.example.authservice.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class CustomerSummaryResponse {

    private final Long id;
    private final String fullName;
    private final String email;
    private final String status;
    private final OffsetDateTime createdAt;
    private final String companyName;
    private final String country;
    private final String industry;
    private final BigDecimal riskScore;
    private final String verificationStatus;

    public CustomerSummaryResponse(Long id,
                                   String fullName,
                                   String email,
                                   String status,
                                   OffsetDateTime createdAt,
                                   String companyName,
                                   String country,
                                   String industry,
                                   BigDecimal riskScore,
                                   String verificationStatus) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
        this.companyName = companyName;
        this.country = country;
        this.industry = industry;
        this.riskScore = riskScore;
        this.verificationStatus = verificationStatus;
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

    public String getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCountry() {
        return country;
    }

    public String getIndustry() {
        return industry;
    }

    public BigDecimal getRiskScore() {
        return riskScore;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }
}
