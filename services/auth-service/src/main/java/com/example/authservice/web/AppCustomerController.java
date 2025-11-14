package com.example.authservice.web;

import com.example.authservice.service.CustomerAppService;
import com.example.authservice.service.CustomerProfileService;
import com.example.authservice.web.dto.ApplicationStatusResponse;
import com.example.authservice.web.dto.CreateCustomerDocumentRequest;
import com.example.authservice.web.dto.CustomerProfileDetailsResponse;
import com.example.authservice.web.dto.CustomerSignupRequest;
import com.example.authservice.web.dto.CustomerSignupResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app")
public class AppCustomerController {

    private final CustomerAppService customerAppService;
    private final CustomerProfileService customerProfileService;

    public AppCustomerController(CustomerAppService customerAppService,
                                 CustomerProfileService customerProfileService) {
        this.customerAppService = customerAppService;
        this.customerProfileService = customerProfileService;
    }

    @PostMapping("/signup")
    public ResponseEntity<CustomerSignupResponse> signup(@Valid @RequestBody CustomerSignupRequest request) {
        CustomerSignupResponse response = customerAppService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/customers/{id}/status")
    public ApplicationStatusResponse status(@PathVariable Long id) {
        return customerAppService.getStatus(id);
    }

    @PostMapping("/customers/{id}/documents")
    public CustomerProfileDetailsResponse addDocument(@PathVariable Long id,
                                                      @Valid @RequestBody CreateCustomerDocumentRequest request) {
        customerAppService.addDocument(id, request);
        return customerProfileService.getCustomerProfile(id);
    }
}
