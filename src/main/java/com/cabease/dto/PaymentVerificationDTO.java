package com.cabease.dto;
import javax.validation.constraints.NotBlank;
public class PaymentVerificationDTO {
    @NotBlank(message = "Order ID is required")
    private String orderId;
    @NotBlank(message = "Payment ID is required")
    private String paymentId;
    @NotBlank(message = "Signature is required")
    private String signature;
    private Long userId;
    private String paymentType;
    public PaymentVerificationDTO() {}
    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getPaymentId() {
        return paymentId;
    }
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    public String getSignature() {
        return signature;
    }
    public void setSignature(String signature) {
        this.signature = signature;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getPaymentType() {
        return paymentType;
    }
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}
