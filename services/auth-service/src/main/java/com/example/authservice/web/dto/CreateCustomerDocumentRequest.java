package com.example.authservice.web.dto;

import com.example.authservice.domain.CustomerDocumentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCustomerDocumentRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 100)
    private String type;

    @Size(max = 500)
    private String url;

    private CustomerDocumentStatus status = CustomerDocumentStatus.UPLOADED;

    @Size(max = 100)
    private String uploadedBy;

    @Size(max = 120)
    private String contentType;

    /**
     * Base64-encoded file payload. Optional when only metadata is provided.
     */
    private String base64Data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public CustomerDocumentStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerDocumentStatus status) {
        this.status = status;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getBase64Data() {
        return base64Data;
    }

    public void setBase64Data(String base64Data) {
        this.base64Data = base64Data;
    }
}
