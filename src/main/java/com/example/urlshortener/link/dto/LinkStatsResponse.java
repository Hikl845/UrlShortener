package com.example.urlshortener.link.dto;


public class LinkStatsResponse {

    private final String shortCode;
    private final String originalUrl;
    private final int clickCount;
    private final boolean active;

    public LinkStatsResponse(String shortCode, String originalUrl,
                             int clickCount, boolean active) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.clickCount = clickCount;
        this.active = active;
    }

    public String getShortCode() { return shortCode; }
    public String getOriginalUrl() { return originalUrl; }
    public int getClickCount() { return clickCount; }
    public boolean isActive() { return active; }
}
