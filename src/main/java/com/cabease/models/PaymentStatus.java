package com.cabease.models;
public enum PaymentStatus {
    PENDING("Payment Pending", "⏳", false),
    PROCESSING("Payment Processing", "🔄", false),
    COMPLETED("Payment Completed", "✅", true),
    FAILED("Payment Failed", "❌", true),
    CANCELLED("Payment Cancelled", "🚫", true),
    REFUNDED("Payment Refunded", "💰", true),
    PARTIAL_REFUND("Partial Refund", "💸", true),
    DISPUTED("Payment Disputed", "⚠️", false),
    CHARGEBACK("Chargeback Initiated", "🔙", false);
    private final String displayName;
    private final String icon;
    private final boolean isTerminal;
    PaymentStatus(String displayName, String icon, boolean isTerminal) {
        this.displayName = displayName;
        this.icon = icon;
        this.isTerminal = isTerminal;
    }
    public String getDisplayName() {
        return displayName;
    }
    public String getIcon() {
        return icon;
    }
    public boolean isTerminal() {
        return isTerminal;
    }
    public boolean isSuccessful() {
        return this == COMPLETED;
    }
    public boolean requiresAction() {
        return this == PENDING ||
               this == PROCESSING ||
               this == DISPUTED ||
               this == CHARGEBACK;
    }
    public boolean isFailed() {
        return this == FAILED ||
               this == CANCELLED;
    }
}
