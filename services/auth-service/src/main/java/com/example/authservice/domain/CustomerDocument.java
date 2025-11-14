package com.example.authservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "customer_documents")
public class CustomerDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id")
    private CustomerProfile profile;

    @Column(name = "doc_name", nullable = false, length = 255)
    private String name;

    @Column(name = "doc_type", length = 100)
    private String type;

    @Column(name = "doc_url", length = 500)
    private String url;

    @Column(name = "content_type", length = 120)
    private String contentType;

    @Lob
    @Column(name = "file_data")
    private byte[] fileData;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private CustomerDocumentStatus status = CustomerDocumentStatus.UPLOADED;

    @Column(name = "uploaded_at", nullable = false)
    private OffsetDateTime uploadedAt;

    @PrePersist
    void onCreate() {
        this.uploadedAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public CustomerProfile getProfile() {
        return profile;
    }

    public void setProfile(CustomerProfile profile) {
        this.profile = profile;
    }

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

    public OffsetDateTime getUploadedAt() {
        return uploadedAt;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
}
