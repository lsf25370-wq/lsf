package com.csa.assistant.entity;

public class LogisticsEntity {

    private String trackingNo;
    private String logisticsCompany;
    private String status;
    private String eventsJson;

    public LogisticsEntity() {
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

    public String getEventsJson() {
        return eventsJson;
    }

    public void setEventsJson(String eventsJson) {
        this.eventsJson = eventsJson;
    }
}
