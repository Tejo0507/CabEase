package com.cabease.models;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "cabs", indexes = {
    @Index(name = "idx_cab_vehicle_type", columnList = "vehicleType"),
    @Index(name = "idx_cab_available", columnList = "available"),
    @Index(name = "idx_cab_location", columnList = "currentLatitude, currentLongitude")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cab {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Cab number is required")
    @Column(unique = true)
    private String cabNumber;
    @NotBlank(message = "Model is required")
    private String model;
    @NotBlank(message = "Brand is required")
    private String brand;
    @NotNull(message = "Vehicle type is required")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VehicleType vehicleType = VehicleType.CAB;
    @NotNull(message = "Capacity is required")
    private Integer capacity;
    @NotNull(message = "Price per km is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price per km must be positive")
    private BigDecimal pricePerKm;
    @Builder.Default
    private Boolean available = true;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = true)
    private Driver driver;
    @Column(precision = 10, scale = 8)
    private BigDecimal currentLatitude;
    @Column(precision = 11, scale = 8)
    private BigDecimal currentLongitude;
    private LocalDateTime lastLocationUpdate;
    private String color;
    @Column(name = "manufacture_year", length = 4)
    private Integer year;
    private String fuelType;
    @Builder.Default
    private Boolean acAvailable = false;
    @Builder.Default
    private Boolean musicSystem = false;
    @Builder.Default
    private Boolean gpsEnabled = true;
    @Builder.Default
    private Boolean verified = false;
    private String insuranceNumber;
    private LocalDateTime insuranceExpiry;
    private String registrationNumber;
    private LocalDateTime registrationExpiry;
    @Builder.Default
    private Double averageRating = 0.0;
    @Builder.Default
    private Integer totalTrips = 0;
    @Builder.Default
    private BigDecimal totalEarnings = BigDecimal.ZERO;
    @Builder.Default
    private Boolean allowFemaleDriverPreference = false;
    @Builder.Default
    private Boolean surgeEligible = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (capacity == null && vehicleType != null) {
            capacity = vehicleType.getMaxCapacity();
        }
        if (pricePerKm == null && vehicleType != null) {
            pricePerKm = BigDecimal.valueOf(vehicleType.getBaseFare());
        }
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    public void updateLocation(BigDecimal latitude, BigDecimal longitude) {
        this.currentLatitude = latitude;
        this.currentLongitude = longitude;
        this.lastLocationUpdate = LocalDateTime.now();
    }
    public boolean isOnline() {
        return available && lastLocationUpdate != null &&
               lastLocationUpdate.isAfter(LocalDateTime.now().minusMinutes(5));
    }
    public int getEstimatedArrivalTime() {
        return vehicleType != null ? vehicleType.getEstimatedArrivalMinutes() : 7;
    }
    public BigDecimal calculateFare(BigDecimal distance, Double surgeMultiplier) {
        if (pricePerKm == null || distance == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal baseFare = distance.multiply(pricePerKm);
        if (vehicleType != null) {
            baseFare = baseFare.multiply(BigDecimal.valueOf(vehicleType.getFareMultiplier()));
        }
        if (surgeMultiplier != null && surgeMultiplier > 1.0 && vehicleType.supportsSurge()) {
            baseFare = baseFare.multiply(BigDecimal.valueOf(surgeMultiplier));
        }
        return baseFare;
    }
}
