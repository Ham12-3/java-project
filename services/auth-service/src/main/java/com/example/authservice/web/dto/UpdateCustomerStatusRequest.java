package com.example.authservice.web.dto;

import com.example.authservice.domain.CustomerStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateCustomerStatusRequest {

    @NotNull
    private CustomerStatus status;

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }
}
