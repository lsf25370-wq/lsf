package com.csa.assistant.entity;

public class AccountEntity {

    private String userId;
    private String userName;
    private String email;
    private String phone;
    private Double balance;
    private Integer points;
    private String memberLevel;
    private String memberExpireDate;

    public AccountEntity() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getMemberLevel() {
        return memberLevel;
    }

    public void setMemberLevel(String memberLevel) {
        this.memberLevel = memberLevel;
    }

    public String getMemberExpireDate() {
        return memberExpireDate;
    }

    public void setMemberExpireDate(String memberExpireDate) {
        this.memberExpireDate = memberExpireDate;
    }
}
