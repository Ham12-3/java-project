package com.example.authservice.web.dto;

public class CustomerLoginResponse {

    private final String token;
    private final Long customerId;

    public CustomerLoginResponse(String token, Long customerId) {
        this.token = token;
        this.customerId = customerId;
    }

    public String getToken() {
        return token;
    }

    public Long getCustomerId() {
        return customerId;
    }
}
