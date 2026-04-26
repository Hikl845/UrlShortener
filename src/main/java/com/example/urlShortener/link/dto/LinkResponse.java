package com.example.urlshortener.link.dto;

public class LinkResponse {

    private String shortCode;
    private String originalUrl;
    private int clickCount;

    public LinkResponse(String shortCode, String originalUrl, int clickCount) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.clickCount = clickCount;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public int getClickCount() {
        return clickCount;
    }
}
