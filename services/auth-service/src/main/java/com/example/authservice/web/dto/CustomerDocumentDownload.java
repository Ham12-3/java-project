package com.example.authservice.web.dto;

public class CustomerDocumentDownload {

    private final String name;
    private final String contentType;
    private final byte[] data;

    public CustomerDocumentDownload(String name, String contentType, byte[] data) {
        this.name = name;
        this.contentType = contentType;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getData() {
        return data;
    }
}
