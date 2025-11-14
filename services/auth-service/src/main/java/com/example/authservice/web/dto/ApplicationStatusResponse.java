package com.example.authservice.web.dto;

public class ApplicationStatusResponse {

    private final String onboardingStatus;
    private final String verificationStatus;
    private final AccountSummaryResponse account;

    public ApplicationStatusResponse(String onboardingStatus,
                                     String verificationStatus,
                                     AccountSummaryResponse account) {
        this.onboardingStatus = onboardingStatus;
        this.verificationStatus = verificationStatus;
        this.account = account;
    }

    public String getOnboardingStatus() {
        return onboardingStatus;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public AccountSummaryResponse getAccount() {
        return account;
    }
}
