package com.cabease.models;
public enum VehicleType {
    CAB("Car", "🚗", 4, 1.0, 15.0, "Comfortable sedans and hatchbacks"),
    AUTO("Auto", "🛺", 3, 0.7, 8.0, "Affordable 3-wheeler rickshaws"),
    BIKE("Bike", "🏍️", 2, 0.5, 5.0, "Quick 2-wheeler rides for solo trips"),
    LUXURY("Luxury", "🚘", 4, 2.0, 25.0, "Premium cars with luxury amenities"),
    MICRO("Micro", "🚙", 4, 0.8, 12.0, "Compact cars for budget-friendly rides"),
    XL("XL", "🚐", 6, 1.5, 20.0, "Spacious vehicles for group travel");
    private final String displayName;
    private final String icon;
    private final int maxCapacity;
    private final double fareMultiplier;
    private final double baseFare;
    private final String description;
    VehicleType(String displayName, String icon, int maxCapacity, double fareMultiplier, double baseFare, String description) {
        this.displayName = displayName;
        this.icon = icon;
        this.maxCapacity = maxCapacity;
        this.fareMultiplier = fareMultiplier;
        this.baseFare = baseFare;
        this.description = description;
    }
    public String getDisplayName() {
        return displayName;
    }
    public String getIcon() {
        return icon;
    }
    public int getMaxCapacity() {
        return maxCapacity;
    }
    public double getFareMultiplier() {
        return fareMultiplier;
    }
    public double getBaseFare() {
        return baseFare;
    }
    public String getDescription() {
        return description;
    }
    public int getEstimatedArrivalMinutes() {
        switch (this) {
            case BIKE:
                return 3;
            case AUTO:
                return 5;
            case CAB:
            case MICRO:
                return 7;
            case LUXURY:
                return 10;
            case XL:
                return 12;
            default:
                return 7;
        }
    }
    public boolean supportsSurge() {
        return this == CAB || this == LUXURY || this == XL;
    }
    public int getPriority() {
        switch (this) {
            case BIKE:
                return 1;
            case AUTO:
                return 2;
            case CAB:
                return 3;
            case MICRO:
                return 4;
            case XL:
                return 5;
            case LUXURY:
                return 6;
            default:
                return 3;
        }
    }
}
