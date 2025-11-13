package com.cabease.dto;
import java.math.BigDecimal;
public class RazorpayOrderDTO {
    private String orderId;
    private BigDecimal amount;
    private String currency;
    private String receipt;
    private String status;
    private String razorpayKeyId;
    public RazorpayOrderDTO() {}
    public RazorpayOrderDTO(String orderId, BigDecimal amount, String currency, String receipt, String status, String razorpayKeyId) {
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.receipt = receipt;
        this.status = status;
        this.razorpayKeyId = razorpayKeyId;
    }
    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public String getReceipt() {
        return receipt;
    }
    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getRazorpayKeyId() {
        return razorpayKeyId;
    }
    public void setRazorpayKeyId(String razorpayKeyId) {
        this.razorpayKeyId = razorpayKeyId;
    }
}
