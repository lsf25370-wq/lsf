package com.csa.assistant.model;

public class FAQEntry {

    private String question;
    private String answer;
    private String category;
    private IntentType intent;

    public FAQEntry() {
    }

    public FAQEntry(String question, String answer, String category, IntentType intent) {
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.intent = intent;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public IntentType getIntent() {
        return intent;
    }

    public void setIntent(IntentType intent) {
        this.intent = intent;
    }
}
