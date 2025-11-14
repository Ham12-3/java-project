package com.example.authservice.web.dto;

import java.util.List;
import java.util.Map;

public class OpsMetricsResponse {

    private final Map<String, Long> onboardingCounts;
    private final List<CustomerEventSummary> recentApprovals;
    private final List<CustomerEventSummary> recentRejections;
    private final List<OpsAlertResponse> alerts;

    public OpsMetricsResponse(Map<String, Long> onboardingCounts,
                              List<CustomerEventSummary> recentApprovals,
                              List<CustomerEventSummary> recentRejections,
                              List<OpsAlertResponse> alerts) {
        this.onboardingCounts = onboardingCounts;
        this.recentApprovals = recentApprovals;
        this.recentRejections = recentRejections;
        this.alerts = alerts;
    }

    public Map<String, Long> getOnboardingCounts() {
        return onboardingCounts;
    }

    public List<CustomerEventSummary> getRecentApprovals() {
        return recentApprovals;
    }

    public List<CustomerEventSummary> getRecentRejections() {
        return recentRejections;
    }

    public List<OpsAlertResponse> getAlerts() {
        return alerts;
    }
}
