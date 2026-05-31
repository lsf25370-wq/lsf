package com.csa.assistant.model;

import java.util.List;

public class LogisticsInfo {

    private String trackingNo;
    private String logisticsCompany;
    private String status;
    private List<TrackingEvent> trackingEvents;

    public LogisticsInfo() {
    }

    public LogisticsInfo(String trackingNo, String logisticsCompany, String status, List<TrackingEvent> trackingEvents) {
        this.trackingNo = trackingNo;
        this.logisticsCompany = logisticsCompany;
        this.status = status;
        this.trackingEvents = trackingEvents;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public String getLogisticsCompany() {
        return logisticsCompany;
    }

    public void setLogisticsCompany(String logisticsCompany) {
        this.logisticsCompany = logisticsCompany;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TrackingEvent> getTrackingEvents() {
        return trackingEvents;
    }

    public void setTrackingEvents(List<TrackingEvent> trackingEvents) {
        this.trackingEvents = trackingEvents;
    }
}
