package com.example.authservice.web.dto;

public class CustomerSignupResponse {

    private final Long customerId;

    public CustomerSignupResponse(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCustomerId() {
        return customerId;
    }
}
