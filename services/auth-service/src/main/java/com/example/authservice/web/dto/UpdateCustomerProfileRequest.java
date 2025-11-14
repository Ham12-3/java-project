package com.example.authservice.web.dto;

import com.example.authservice.domain.CustomerVerificationStatus;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class UpdateCustomerProfileRequest {

    @Size(max = 255)
    private String companyName;

    @Size(max = 100)
    private String country;

    @Size(max = 150)
    private String industry;

    @Digits(integer = 3, fraction = 2)
    private BigDecimal riskScore;

    private CustomerVerificationStatus verificationStatus;

    @Size(max = 1000)
    private String notes;

    @Size(max = 100)
    private String updatedBy;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public BigDecimal getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(BigDecimal riskScore) {
        this.riskScore = riskScore;
    }

    public CustomerVerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(CustomerVerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
