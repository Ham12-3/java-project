package com.example.authservice.service;

import com.example.authservice.domain.Customer;
import com.example.authservice.domain.CustomerProfile;
import com.example.authservice.domain.CustomerProfileRepository;
import com.example.authservice.domain.CustomerRepository;
import com.example.authservice.domain.CustomerStatus;
import com.example.authservice.domain.CustomerVerificationStatus;
import com.example.authservice.web.dto.CustomerEventSummary;
import com.example.authservice.web.dto.OpsAlertResponse;
import com.example.authservice.web.dto.OpsMetricsResponse;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class OperationsMetricsService {

    private final CustomerRepository customerRepository;
    private final CustomerProfileRepository profileRepository;

    public OperationsMetricsService(CustomerRepository customerRepository,
                                    CustomerProfileRepository profileRepository) {
        this.customerRepository = customerRepository;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public OpsMetricsResponse fetchMetrics() {
        Map<String, Long> counts = Arrays.stream(CustomerStatus.values())
                .collect(Collectors.toMap(
                        CustomerStatus::name,
                        customerRepository::countByStatus,
                        (a, b) -> b,
                        LinkedHashMap::new));

        List<CustomerEventSummary> approvals = customerRepository
                .findTop5ByStatusOrderByUpdatedAtDesc(CustomerStatus.APPROVED)
                .stream()
                .map(this::toEventSummary)
                .collect(Collectors.toList());

        List<CustomerEventSummary> rejections = customerRepository
                .findTop5ByStatusOrderByUpdatedAtDesc(CustomerStatus.REJECTED)
                .stream()
                .map(this::toEventSummary)
                .collect(Collectors.toList());

        List<OpsAlertResponse> alerts = buildAlerts();

        return new OpsMetricsResponse(counts, approvals, rejections, alerts);
    }

    private CustomerEventSummary toEventSummary(Customer customer) {
        return new CustomerEventSummary(
                customer.getId(),
                customer.getFullName(),
                customer.getStatus().name(),
                customer.getUpdatedAt()
        );
    }

    private List<OpsAlertResponse> buildAlerts() {
        List<CustomerProfile> highRiskProfiles = profileRepository
                .findTop5ByRiskScoreGreaterThanEqualOrderByUpdatedAtDesc(BigDecimal.valueOf(70));

        if (highRiskProfiles.isEmpty()) {
            return List.of(
                    new OpsAlertResponse("ALERT-1", "Fraud", "HIGH",
                            "Large payment flagged in manual review queue.",
                            "OPEN", OffsetDateTime.now().minusMinutes(12)),
                    new OpsAlertResponse("ALERT-2", "Risk", "MEDIUM",
                            "Inconsistent KYC data detected during screening.",
                            "OPEN", OffsetDateTime.now().minusMinutes(34)),
                    new OpsAlertResponse("ALERT-3", "AML", "LOW",
                            "Watchlist match resolved automatically.",
                            "CLOSED", OffsetDateTime.now().minusHours(2))
            );
        }

        return highRiskProfiles.stream()
                .map(profile -> new OpsAlertResponse(
                        "RISK-" + profile.getId(),
                        "Risk",
                        profile.getRiskScore().compareTo(BigDecimal.valueOf(85)) >= 0 ? "CRITICAL" : "HIGH",
                        "High risk score (" + profile.getRiskScore() + ") for " + profile.getCompanyName(),
                        profile.getVerificationStatus() == CustomerVerificationStatus.APPROVED ? "CLOSED" : "OPEN",
                        profile.getUpdatedAt()))
                .collect(Collectors.toList());
    }
}
