package com.csa.assistant.model;

public class Coupon {

    private String couponId;
    private String name;
    private Double discount;
    private Double minAmount;
    private String expireDate;
    private boolean available;
    private String status;
    private String useTime;

    public Coupon() {
    }

    public Coupon(String couponId, String name, Double discount, Double minAmount, String expireDate, boolean available) {
        this.couponId = couponId;
        this.name = name;
        this.discount = discount;
        this.minAmount = minAmount;
        this.expireDate = expireDate;
        this.available = available;
        this.status = available ? "可用" : "已过期";
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Double minAmount) {
        this.minAmount = minAmount;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }
}
