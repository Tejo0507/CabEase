package com.cabease.models;
public enum PaymentMethod {
    CASH("Cash Payment", "💵", "Pay directly to driver", true),
    UPI("UPI Payment", "📱", "Pay using UPI apps like GPay, PhonePe", true),
    CREDIT_CARD("Credit Card", "💳", "Pay using credit card", true),
    DEBIT_CARD("Debit Card", "💳", "Pay using debit card", true),
    WALLET("Digital Wallet", "👛", "Pay using Paytm, Amazon Pay", true),
    NET_BANKING("Net Banking", "🏦", "Pay using internet banking", true),
    PAY_LATER("Pay Later", "📋", "Book now, pay later options", false),
    CORPORATE("Corporate Account", "🏢", "Company billing account", false),
    SUBSCRIPTION("Subscription", "🎫", "Power Pass or subscription", false);
    private final String displayName;
    private final String icon;
    private final String description;
    private final boolean instantPayment;
    PaymentMethod(String displayName, String icon, String description, boolean instantPayment) {
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
        this.instantPayment = instantPayment;
    }
    public String getDisplayName() {
        return displayName;
    }
    public String getIcon() {
        return icon;
    }
    public String getDescription() {
        return description;
    }
    public boolean isInstantPayment() {
        return instantPayment;
    }
    public boolean requiresVerification() {
        return this == CREDIT_CARD ||
               this == DEBIT_CARD ||
               this == NET_BANKING ||
               this == CORPORATE;
    }
    public int getPriority() {
        switch (this) {
            case UPI:
                return 1;
            case CASH:
                return 2;
            case WALLET:
                return 3;
            case CREDIT_CARD:
                return 4;
            case DEBIT_CARD:
                return 5;
            case NET_BANKING:
                return 6;
            case PAY_LATER:
                return 7;
            case SUBSCRIPTION:
                return 8;
            case CORPORATE:
                return 9;
            default:
                return 5;
        }
    }
}
