package com.csa.assistant.model;

public class ReturnRequest {

    private Long id;
    private String requestId;
    private String orderId;
    private String reason;
    private String description;
    private String status;
    private Double refundAmount;
    private String createTime;
    private String processedTime;
    private String userId;

    public ReturnRequest() {
    }

    public ReturnRequest(Long id, String requestId, String orderId, String reason,
                         String description, String status, Double refundAmount,
                         String createTime, String processedTime, String userId) {
        this.id = id;
        this.requestId = requestId;
        this.orderId = orderId;
        this.reason = reason;
        this.description = description;
        this.status = status;
        this.refundAmount = refundAmount;
        this.createTime = createTime;
        this.processedTime = processedTime;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getProcessedTime() {
        return processedTime;
    }

    public void setProcessedTime(String processedTime) {
        this.processedTime = processedTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
