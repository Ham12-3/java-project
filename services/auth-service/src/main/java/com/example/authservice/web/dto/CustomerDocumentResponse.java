package com.example.authservice.web.dto;

import java.time.OffsetDateTime;

public class CustomerDocumentResponse {

    private final Long id;
    private final String name;
    private final String type;
    private final String url;
    private final String status;
    private final String contentType;
    private final boolean downloadable;
    private final OffsetDateTime uploadedAt;

    public CustomerDocumentResponse(Long id,
                                    String name,
                                    String type,
                                    String url,
                                    String status,
                                    String contentType,
                                    boolean downloadable,
                                    OffsetDateTime uploadedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.url = url;
        this.status = status;
        this.contentType = contentType;
        this.downloadable = downloadable;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getStatus() {
        return status;
    }

    public String getContentType() {
        return contentType;
    }

    public boolean isDownloadable() {
        return downloadable;
    }

    public OffsetDateTime getUploadedAt() {
        return uploadedAt;
    }
}
