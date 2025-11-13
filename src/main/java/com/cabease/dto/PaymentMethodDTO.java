package com.cabease.dto;
import java.math.BigDecimal;
public class PaymentMethodDTO {
    private Long id;
    private Long userId;
    private String methodType;
    private String cardLastFour;
    private String cardBrand;
    private String upiId;
    private BigDecimal walletBalance;
    private Boolean isDefault;
    private Boolean isActive;
    private Integer expiryMonth;
    private Integer expiryYear;
    private String cardholderName;
    public PaymentMethodDTO() {}
    public PaymentMethodDTO(Long id, Long userId, String methodType, String cardLastFour,
                           String cardBrand, String upiId, BigDecimal walletBalance,
                           Boolean isDefault, Boolean isActive) {
        this.id = id;
        this.userId = userId;
        this.methodType = methodType;
        this.cardLastFour = cardLastFour;
        this.cardBrand = cardBrand;
        this.upiId = upiId;
        this.walletBalance = walletBalance;
        this.isDefault = isDefault;
        this.isActive = isActive;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getMethodType() { return methodType; }
    public void setMethodType(String methodType) { this.methodType = methodType; }
    public String getCardLastFour() { return cardLastFour; }
    public void setCardLastFour(String cardLastFour) { this.cardLastFour = cardLastFour; }
    public String getCardBrand() { return cardBrand; }
    public void setCardBrand(String cardBrand) { this.cardBrand = cardBrand; }
    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }
    public BigDecimal getWalletBalance() { return walletBalance; }
    public void setWalletBalance(BigDecimal walletBalance) { this.walletBalance = walletBalance; }
    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public Integer getExpiryMonth() { return expiryMonth; }
    public void setExpiryMonth(Integer expiryMonth) { this.expiryMonth = expiryMonth; }
    public Integer getExpiryYear() { return expiryYear; }
    public void setExpiryYear(Integer expiryYear) { this.expiryYear = expiryYear; }
    public String getCardholderName() { return cardholderName; }
    public void setCardholderName(String cardholderName) { this.cardholderName = cardholderName; }
}
