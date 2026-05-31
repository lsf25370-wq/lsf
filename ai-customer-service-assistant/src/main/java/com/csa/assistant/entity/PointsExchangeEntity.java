package com.csa.assistant.entity;

public class PointsExchangeEntity {
    private Long id;
    private String exchangeId;
    private String userId;
    private String productId;
    private String productName;
    private Integer pointsCost;
    private String status;
    private String exchangeTime;
    private String shipTime;
    private String trackingNo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getExchangeId() { return exchangeId; }
    public void setExchangeId(String exchangeId) { this.exchangeId = exchangeId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getPointsCost() { return pointsCost; }
    public void setPointsCost(Integer pointsCost) { this.pointsCost = pointsCost; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getExchangeTime() { return exchangeTime; }
    public void setExchangeTime(String exchangeTime) { this.exchangeTime = exchangeTime; }
    public String getShipTime() { return shipTime; }
    public void setShipTime(String shipTime) { this.shipTime = shipTime; }
    public String getTrackingNo() { return trackingNo; }
    public void setTrackingNo(String trackingNo) { this.trackingNo = trackingNo; }
}
