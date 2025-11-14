package com.example.authservice.service;

import com.example.authservice.domain.Customer;
import com.example.authservice.domain.CustomerRepository;
import com.example.authservice.domain.CustomerStatus;
import com.example.authservice.web.dto.CreateCustomerRequest;
import com.example.authservice.web.dto.CustomerResponse;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class OnboardingService {

    private final CustomerRepository customers;
    private final CustomerProfileService customerProfileService;

    public OnboardingService(CustomerRepository customers,
                             CustomerProfileService customerProfileService) {
        this.customers = customers;
        this.customerProfileService = customerProfileService;
    }

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        customers.findByEmailIgnoreCase(request.getEmail()).ifPresent(existing -> {
            throw new IllegalArgumentException("Customer with that email already exists");
        });

        Customer customer = new Customer(request.getFullName(), request.getEmail(), request.getNotes());
        Customer saved = customers.save(customer);
        return toResponse(saved);
    }

    public List<CustomerResponse> listCustomers() {
        return customers.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomerResponse updateStatus(Long id, CustomerStatus status) {
        Customer customer = customers.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        CustomerStatus previousStatus = customer.getStatus();
        customer.setStatus(status);
        customerProfileService.handleOnboardingStatusChange(customer, previousStatus, "onboarding");
        return toResponse(customer);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getNotes(),
                customer.getStatus().name(),
                customer.getCreatedAt()
        );
    }
}
