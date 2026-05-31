package com.csa.assistant.model;

public class TrackingEvent {

    private String time;
    private String description;
    private String location;

    public TrackingEvent() {
    }

    public TrackingEvent(String time, String description, String location) {
        this.time = time;
        this.description = description;
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
