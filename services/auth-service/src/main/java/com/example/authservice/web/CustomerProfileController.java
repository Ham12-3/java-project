package com.example.authservice.web;

import com.example.authservice.service.CustomerProfileService;
import com.example.authservice.web.dto.CreateCustomerDocumentRequest;
import com.example.authservice.web.dto.CustomerAuditLogResponse;
import com.example.authservice.web.dto.CustomerDocumentDownload;
import com.example.authservice.web.dto.CustomerProfileDetailsResponse;
import com.example.authservice.web.dto.CustomerSummaryResponse;
import com.example.authservice.web.dto.UpdateCustomerProfileRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
public class CustomerProfileController {

    private final CustomerProfileService customerProfileService;

    public CustomerProfileController(CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    @GetMapping
    public List<CustomerSummaryResponse> listCustomers() {
        return customerProfileService.listCustomers();
    }

    @GetMapping("/{id}")
    public CustomerProfileDetailsResponse getCustomer(@PathVariable Long id) {
        return customerProfileService.getCustomerProfile(id);
    }

    @PatchMapping("/{id}")
    public CustomerProfileDetailsResponse updateProfile(@PathVariable Long id,
                                                        @Valid @RequestBody UpdateCustomerProfileRequest request) {
        return customerProfileService.updateProfile(id, request);
    }

    @PostMapping("/{id}/documents")
    public CustomerProfileDetailsResponse attachDocument(@PathVariable Long id,
                                                         @Valid @RequestBody CreateCustomerDocumentRequest request) {
        return customerProfileService.addDocument(id, request);
    }

    @GetMapping("/{id}/audit")
    public List<CustomerAuditLogResponse> audit(@PathVariable Long id) {
        return customerProfileService.getAuditLog(id);
    }

    @GetMapping("/{id}/documents/{documentId}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id, @PathVariable Long documentId) {
        CustomerDocumentDownload download = customerProfileService.downloadDocument(id, documentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + download.getName() + "\"")
                .contentType(MediaType.parseMediaType(download.getContentType()))
                .body(download.getData());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
