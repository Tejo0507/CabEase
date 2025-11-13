package com.cabease.models;
public enum BookingStatus {
    PENDING("Booking Request Pending", "⏳", false),
    CONFIRMED("Booking Confirmed", "✅", false),
    DRIVER_ASSIGNED("Driver Assigned", "👨‍✈️", false),
    DRIVER_ARRIVING("Driver Arriving", "🚗", true),
    DRIVER_ARRIVED("Driver Arrived", "📍", true),
    IN_PROGRESS("Trip In Progress", "🚀", true),
    COMPLETED("Trip Completed", "🏁", false),
    CANCELLED("Booking Cancelled", "❌", false),
    CANCELLED_BY_DRIVER("Cancelled by Driver", "❌", false),
    NO_DRIVER_AVAILABLE("No Driver Available", "🚫", false),
    PAYMENT_FAILED("Payment Failed", "💳", false),
    REFUNDED("Refunded", "💰", false);
    private final String displayName;
    private final String icon;
    private final boolean trackable;
    BookingStatus(String displayName, String icon, boolean trackable) {
        this.displayName = displayName;
        this.icon = icon;
        this.trackable = trackable;
    }
    public String getDisplayName() {
        return displayName;
    }
    public String getIcon() {
        return icon;
    }
    public boolean isTrackable() {
        return trackable;
    }
    public boolean isActive() {
        return this == CONFIRMED ||
               this == DRIVER_ASSIGNED ||
               this == DRIVER_ARRIVING ||
               this == DRIVER_ARRIVED ||
               this == IN_PROGRESS;
    }
    public boolean isTerminal() {
        return this == COMPLETED ||
               this == CANCELLED ||
               this == CANCELLED_BY_DRIVER ||
               this == NO_DRIVER_AVAILABLE ||
               this == REFUNDED;
    }
    public BookingStatus getNextStatus() {
        switch (this) {
            case PENDING:
                return CONFIRMED;
            case CONFIRMED:
                return DRIVER_ASSIGNED;
            case DRIVER_ASSIGNED:
                return DRIVER_ARRIVING;
            case DRIVER_ARRIVING:
                return DRIVER_ARRIVED;
            case DRIVER_ARRIVED:
                return IN_PROGRESS;
            case IN_PROGRESS:
                return COMPLETED;
            default:
                return this;
        }
    }
    public boolean allowsCancellation() {
        return this == PENDING ||
               this == CONFIRMED ||
               this == DRIVER_ASSIGNED;
    }
}
