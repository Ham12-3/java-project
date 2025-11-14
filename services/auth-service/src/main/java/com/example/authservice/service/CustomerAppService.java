package com.example.authservice.service;

import com.example.authservice.domain.Customer;
import com.example.authservice.domain.CustomerRepository;
import com.example.authservice.domain.CustomerStatus;
import com.example.authservice.web.dto.ApplicationStatusResponse;
import com.example.authservice.web.dto.CreateCustomerDocumentRequest;
import com.example.authservice.web.dto.CustomerSignupRequest;
import com.example.authservice.web.dto.CustomerSignupResponse;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CustomerAppService {

    private final CustomerRepository customers;
    private final UserService userService;
    private final CustomerProfileService profileService;

    public CustomerAppService(CustomerRepository customers,
                              UserService userService,
                              CustomerProfileService profileService) {
        this.customers = customers;
        this.userService = userService;
        this.profileService = profileService;
    }

    @Transactional
    public CustomerSignupResponse signup(CustomerSignupRequest request) {
        var registration = userService.register(request.getUsername(), request.getEmail(), request.getPassword(), false);
        customers.findByEmailIgnoreCase(request.getEmail()).ifPresent(existing -> {
            throw new IllegalArgumentException("Customer with that email already exists");
        });

        Customer customer = new Customer(request.getFullName(), request.getEmail(), request.getNotes());
        customer.setStatus(CustomerStatus.PENDING);
        customer.setAuthUser(registration.user());
        Customer saved = customers.save(customer);

        var profile = profileService.ensureProfileRecord(saved);
        profile.setCompanyName(Optional.ofNullable(request.getCompanyName()).orElse(saved.getFullName()));
        profile.setCountry(request.getCountry());
        profile.setIndustry(request.getIndustry());
        profile.setNotes(request.getNotes());

        if (request.getDocuments() != null) {
            request.getDocuments().forEach(doc -> {
                if (doc.getUploadedBy() == null || doc.getUploadedBy().isBlank()) {
                    doc.setUploadedBy(request.getUsername());
                }
                profileService.addDocument(saved.getId(), doc);
            });
        }
        return new CustomerSignupResponse(saved.getId());
    }

    public ApplicationStatusResponse getStatus(Long customerId) {
        Customer customer = customers.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        var profile = profileService.getCustomerProfile(customerId);
        return new ApplicationStatusResponse(
                customer.getStatus().name(),
                profile.getSummary().getVerificationStatus(),
                profile.getAccount()
        );
    }

    public Customer findCustomerByUsername(String username) {
        return customers.findByAuthUserId(
                        userService.findByUsername(username)
                                .map(user -> user.getId())
                                .orElseThrow(() -> new IllegalArgumentException("User not found")))
                .orElseThrow(() -> new IllegalArgumentException("Customer not found for user"));
    }

    @Transactional
    public void addDocument(Long customerId, CreateCustomerDocumentRequest request) {
        profileService.addDocument(customerId, request);
    }
}
