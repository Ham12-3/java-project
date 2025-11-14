package com.example.authservice.service;

import com.example.authservice.domain.Customer;
import com.example.authservice.domain.CustomerAuditLog;
import com.example.authservice.domain.CustomerAuditLogRepository;
import com.example.authservice.domain.CustomerDocument;
import com.example.authservice.domain.CustomerDocumentRepository;
import com.example.authservice.domain.CustomerDocumentStatus;
import com.example.authservice.domain.CustomerProfile;
import com.example.authservice.domain.CustomerProfileRepository;
import com.example.authservice.domain.CustomerRepository;
import com.example.authservice.domain.CustomerStatus;
import com.example.authservice.domain.CustomerVerificationStatus;
import com.example.authservice.web.dto.AccountSummaryResponse;
import com.example.authservice.web.dto.CreateCustomerDocumentRequest;
import com.example.authservice.web.dto.CustomerAuditLogResponse;
import com.example.authservice.web.dto.CustomerDocumentResponse;
import com.example.authservice.web.dto.CustomerDocumentDownload;
import com.example.authservice.web.dto.CustomerProfileDetailsResponse;
import com.example.authservice.web.dto.CustomerSummaryResponse;
import com.example.authservice.web.dto.UpdateCustomerProfileRequest;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CustomerProfileService {

    private static final String DEFAULT_ACTOR = "system";

    private final CustomerRepository customerRepository;
    private final CustomerProfileRepository profileRepository;
    private final CustomerDocumentRepository documentRepository;
    private final CustomerAuditLogRepository auditLogRepository;
    private final CustomerAccountService accountService;

    public CustomerProfileService(CustomerRepository customerRepository,
                                  CustomerProfileRepository profileRepository,
                                  CustomerDocumentRepository documentRepository,
                                  CustomerAuditLogRepository auditLogRepository,
                                  CustomerAccountService accountService) {
        this.customerRepository = customerRepository;
        this.profileRepository = profileRepository;
        this.documentRepository = documentRepository;
        this.auditLogRepository = auditLogRepository;
        this.accountService = accountService;
    }

    @Transactional
    public void handleOnboardingStatusChange(Customer customer,
                                             CustomerStatus previousStatus,
                                             String actor) {
        if (customer.getStatus() != CustomerStatus.APPROVED) {
            return;
        }

        CustomerProfile profile = profileRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> initializeProfile(customer));

        OffsetDateTime now = OffsetDateTime.now();
        if (profile.getSubmittedAt() == null) {
            profile.setSubmittedAt(now);
        }
        if (profile.getApprovedAt() == null) {
            profile.setApprovedAt(now);
        }
        profileRepository.save(profile);

        if (previousStatus != CustomerStatus.APPROVED) {
            logAudit(profile, "ONBOARDING_APPROVED", actor,
                    "Onboarding approved; profile created for " + customer.getFullName());
        }
        accountService.ensureAccount(customer);
    }

    @Transactional
    public CustomerProfileDetailsResponse updateProfile(Long customerId, UpdateCustomerProfileRequest request) {
        CustomerProfile profile = requireProfile(customerId);
        List<String> changedFields = new ArrayList<>();

        if (updateField(profile::getCompanyName, profile::setCompanyName, request.getCompanyName())) {
            changedFields.add("companyName");
        }
        if (updateField(profile::getCountry, profile::setCountry, request.getCountry())) {
            changedFields.add("country");
        }
        if (updateField(profile::getIndustry, profile::setIndustry, request.getIndustry())) {
            changedFields.add("industry");
        }
        if (updateBigDecimal(profile::getRiskScore, profile::setRiskScore, request.getRiskScore())) {
            changedFields.add("riskScore");
        }
        if (updateField(profile::getNotes, profile::setNotes, request.getNotes())) {
            changedFields.add("notes");
        }

        CustomerVerificationStatus newStatus = request.getVerificationStatus();
        if (newStatus != null && newStatus != profile.getVerificationStatus()) {
            profile.setVerificationStatus(newStatus);
            changedFields.add("verificationStatus");
        }

        if (!changedFields.isEmpty()) {
            profileRepository.save(profile);
            logAudit(profile, "PROFILE_UPDATE", resolveActor(request.getUpdatedBy()),
                    "Updated fields: " + String.join(", ", changedFields));
        }

        return buildDetailsResponse(customerId, profile);
    }

    @Transactional
    public CustomerProfileDetailsResponse addDocument(Long customerId, CreateCustomerDocumentRequest request) {
        CustomerProfile profile = requireProfile(customerId);
        CustomerDocument document = new CustomerDocument();
        document.setProfile(profile);
        document.setName(request.getName());
        document.setType(request.getType());
        document.setUrl(request.getUrl());
        document.setStatus(Optional.ofNullable(request.getStatus()).orElse(CustomerDocumentStatus.UPLOADED));
        document.setContentType(request.getContentType());
        document.setFileData(decodeBase64(request.getBase64Data()));
        documentRepository.save(document);

        logAudit(profile, "DOCUMENT_ATTACHED", resolveActor(request.getUploadedBy()),
                "Attached document \"" + request.getName() + "\" with status " + document.getStatus());

        return buildDetailsResponse(customerId, profile);
    }

    @Transactional
    public List<CustomerAuditLogResponse> getAuditLog(Long customerId) {
        return profileRepository.findByCustomerId(customerId)
                .map(profile -> auditLogRepository.findByProfileIdOrderByCreatedAtDesc(profile.getId())
                        .stream()
                        .map(this::toAuditResponse)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Transactional
    public List<CustomerSummaryResponse> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        Map<Long, CustomerProfile> profilesByCustomerId = loadProfiles(customers);

        return customers.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(customer -> toSummary(customer, profilesByCustomerId.get(customer.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomerProfileDetailsResponse getCustomerProfile(Long customerId) {
        Customer customer = findCustomer(customerId);
        CustomerProfile profile = profileRepository.findByCustomerId(customerId).orElse(null);
        return buildDetailsResponse(customer, profile);
    }

    private CustomerProfile requireProfile(Long customerId) {
        return profileRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalStateException("Customer profile not found. Approve onboarding first."));
    }

    private CustomerProfile initializeProfile(Customer customer) {
        CustomerProfile profile = new CustomerProfile();
        profile.setCustomer(customer);
        profile.setCompanyName(customer.getFullName());
        profile.setNotes(customer.getNotes());
        profile.setVerificationStatus(CustomerVerificationStatus.IN_REVIEW);
        profile.setSubmittedAt(OffsetDateTime.now());
        return profileRepository.save(profile);
    }

    @Transactional
    public CustomerProfile ensureProfileRecord(Customer customer) {
        return profileRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> initializeProfile(customer));
    }

    private Map<Long, CustomerProfile> loadProfiles(Collection<Customer> customers) {
        if (customers.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> ids = customers.stream().map(Customer::getId).collect(Collectors.toList());
        Map<Long, CustomerProfile> map = new HashMap<>();
        for (CustomerProfile profile : profileRepository.findByCustomerIdIn(ids)) {
            map.put(profile.getCustomer().getId(), profile);
        }
        return map;
    }

    private Customer findCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }

    private void logAudit(CustomerProfile profile, String action, String actor, String notes) {
        CustomerAuditLog log = new CustomerAuditLog();
        log.setProfile(profile);
        log.setAction(action);
        log.setActor(actor);
        log.setNotes(notes);
        auditLogRepository.save(log);
    }

    private CustomerSummaryResponse toSummary(Customer customer, CustomerProfile profile) {
        return new CustomerSummaryResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getStatus().name(),
                customer.getCreatedAt(),
                profile != null ? profile.getCompanyName() : null,
                profile != null ? profile.getCountry() : null,
                profile != null ? profile.getIndustry() : null,
                profile != null ? profile.getRiskScore() : null,
                profile != null ? profile.getVerificationStatus().name() : CustomerVerificationStatus.NOT_STARTED.name()
        );
    }

    private CustomerProfileDetailsResponse buildDetailsResponse(Long customerId, CustomerProfile profile) {
        Customer customer = findCustomer(customerId);
        return buildDetailsResponse(customer, profile);
    }

    private CustomerProfileDetailsResponse buildDetailsResponse(Customer customer, CustomerProfile profile) {
        CustomerSummaryResponse summary = toSummary(customer, profile);
        AccountSummaryResponse account = accountService.getSummary(customer.getId());
        List<CustomerDocumentResponse> documents = profile != null
                ? documentRepository.findByProfileIdOrderByUploadedAtDesc(profile.getId())
                .stream()
                .map(this::toDocumentResponse)
                .collect(Collectors.toList())
                : Collections.emptyList();

        List<CustomerAuditLogResponse> auditEntries = profile != null
                ? auditLogRepository.findByProfileIdOrderByCreatedAtDesc(profile.getId())
                .stream()
                .map(this::toAuditResponse)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return new CustomerProfileDetailsResponse(
                summary,
                profile != null ? profile.getNotes() : null,
                profile != null ? profile.getSubmittedAt() : null,
                profile != null ? profile.getApprovedAt() : null,
                documents,
                auditEntries,
                account
        );
    }

    private CustomerDocumentResponse toDocumentResponse(CustomerDocument document) {
        return new CustomerDocumentResponse(
                document.getId(),
                document.getName(),
                document.getType(),
                document.getUrl(),
                document.getStatus().name(),
                document.getContentType(),
                document.getFileData() != null,
                document.getUploadedAt()
        );
    }

    private CustomerAuditLogResponse toAuditResponse(CustomerAuditLog log) {
        return new CustomerAuditLogResponse(
                log.getId(),
                log.getAction(),
                log.getActor(),
                log.getNotes(),
                log.getCreatedAt()
        );
    }

    private boolean updateField(Supplier<String> getter, Consumer<String> setter, String newValue) {
        if (newValue == null) {
            return false;
        }
        if (!Objects.equals(getter.get(), newValue)) {
            setter.accept(newValue);
            return true;
        }
        return false;
    }

    private boolean updateBigDecimal(Supplier<BigDecimal> getter, Consumer<BigDecimal> setter, BigDecimal newValue) {
        if (newValue == null) {
            return false;
        }
        BigDecimal current = getter.get();
        if (current == null || current.compareTo(newValue) != 0) {
            setter.accept(newValue);
            return true;
        }
        return false;
    }

    private String resolveActor(String requestedActor) {
        return (requestedActor == null || requestedActor.isBlank()) ? DEFAULT_ACTOR : requestedActor;
    }

    private byte[] decodeBase64(String payload) {
        if (payload == null || payload.isBlank()) {
            return null;
        }
        String trimmed = payload.contains(",") ? payload.substring(payload.indexOf(',') + 1) : payload;
        return java.util.Base64.getDecoder().decode(trimmed);
    }

    @Transactional
    public CustomerDocumentDownload downloadDocument(Long customerId, Long documentId) {
        CustomerProfile profile = profileRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for customer"));
        CustomerDocument document = documentRepository.findByIdAndProfileId(documentId, profile.getId())
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));
        if (document.getFileData() == null) {
            throw new IllegalStateException("Document does not have stored file data");
        }
        return new CustomerDocumentDownload(
                document.getName(),
                document.getContentType() != null ? document.getContentType() : "application/octet-stream",
                document.getFileData()
        );
    }
}
