package com.example.openTable.dto;

import com.example.openTable.model.Address;

public class OrderRequest {
    private Long userId;
    private String guestId;
    private String email;
    private String paymentId;
    private Address address;
    private String paypalResponseJson;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPaypalResponseJson() {
        return paypalResponseJson;
    }

    public void setPaypalResponseJson(String paypalResponseJson) {
        this.paypalResponseJson = paypalResponseJson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
