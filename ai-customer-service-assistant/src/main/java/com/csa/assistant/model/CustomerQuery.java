package com.csa.assistant.model;

import java.time.LocalDateTime;

public class CustomerQuery {

    private String sessionId;
    private String query;
    private String userId;
    private IntentType detectedIntent;
    private LocalDateTime timestamp;

    public CustomerQuery() {
        this.timestamp = LocalDateTime.now();
    }

    public CustomerQuery(String sessionId, String query, String userId) {
        this.sessionId = sessionId;
        this.query = query;
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public IntentType getDetectedIntent() {
        return detectedIntent;
    }

    public void setDetectedIntent(IntentType detectedIntent) {
        this.detectedIntent = detectedIntent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
