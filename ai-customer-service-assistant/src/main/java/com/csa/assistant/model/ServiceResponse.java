package com.csa.assistant.model;

import java.util.List;

public class ServiceResponse {

    private String sessionId;
    private IntentType intent;
    private String intentDescription;
    private String answer;
    private double confidence;
    private List<String> relatedFAQs;
    private boolean needsEscalation;

    public ServiceResponse() {
    }

    public ServiceResponse(String sessionId, IntentType intent, String answer, double confidence) {
        this.sessionId = sessionId;
        this.intent = intent;
        this.intentDescription = intent.getDescription();
        this.answer = answer;
        this.confidence = confidence;
        this.needsEscalation = intent == IntentType.ESCALATION || intent == IntentType.UNKNOWN;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public IntentType getIntent() {
        return intent;
    }

    public void setIntent(IntentType intent) {
        this.intent = intent;
    }

    public String getIntentDescription() {
        return intentDescription;
    }

    public void setIntentDescription(String intentDescription) {
        this.intentDescription = intentDescription;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public List<String> getRelatedFAQs() {
        return relatedFAQs;
    }

    public void setRelatedFAQs(List<String> relatedFAQs) {
        this.relatedFAQs = relatedFAQs;
    }

    public boolean isNeedsEscalation() {
        return needsEscalation;
    }

    public void setNeedsEscalation(boolean needsEscalation) {
        this.needsEscalation = needsEscalation;
    }
}
