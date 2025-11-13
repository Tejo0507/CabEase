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
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_status", columnList = "status"),
    @Index(name = "idx_booking_user", columnList = "user_id"),
    @Index(name = "idx_booking_scheduled", columnList = "scheduledFor"),
    @Index(name = "idx_booking_created", columnList = "createdAt")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Pickup location is required")
    private String pickupLocation;
    @NotBlank(message = "Drop location is required")
    private String dropLocation;
    @Column(precision = 10, scale = 8)
    private BigDecimal pickupLatitude;
    @Column(precision = 11, scale = 8)
    private BigDecimal pickupLongitude;
    @Column(precision = 10, scale = 8)
    private BigDecimal dropLatitude;
    @Column(precision = 11, scale = 8)
    private BigDecimal dropLongitude;
    @NotNull(message = "Booking time is required")
    private LocalDateTime bookingDateTime;
    private LocalDateTime scheduledFor;
    @Builder.Default
    private Boolean isScheduled = false;
    @NotNull(message = "Distance is required")
    @DecimalMin(value = "0.1", message = "Distance must be at least 0.1 km")
    private BigDecimal distance;
    @NotNull(message = "Base fare is required")
    private BigDecimal baseFare;
    private BigDecimal surgeMultiplier;
    private BigDecimal surgeAmount;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    @NotNull(message = "Total fare is required")
    private BigDecimal totalFare;
    @Enumerated(EnumType.STRING)
    private VehicleType preferredVehicleType;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cab_id", nullable = true)
    private Cab cab;
    @Builder.Default
    private Boolean femaleDriverPreferred = false;
    @Builder.Default
    private Boolean allowSharing = false;
    @Builder.Default
    private Boolean isEmergency = false;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private String paymentTransactionId;
    private LocalDateTime tripStartedAt;
    private LocalDateTime tripEndedAt;
    private LocalDateTime driverAssignedAt;
    private LocalDateTime driverArrivedAt;
    private Integer userRating;
    private Integer driverRating;
    private String userFeedback;
    private String driverFeedback;
    @Builder.Default
    private Boolean sosTriggered = false;
    private LocalDateTime sosTriggeredAt;
    @Builder.Default
    private Boolean tripShared = false;
    private String cancellationReason;
    private LocalDateTime cancelledAt;
    private BigDecimal cancellationFee;
    private String specialInstructions;
    private Boolean needWheelchairAccess;
    private Boolean needChildSeat;
    private Integer passengerCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (surgeMultiplier == null) {
            surgeMultiplier = BigDecimal.ONE;
        }
        if (passengerCount == null) {
            passengerCount = 1;
        }
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    public int getEstimatedArrivalTime() {
        if (cab != null) {
            return cab.getEstimatedArrivalTime();
        }
        return preferredVehicleType != null ?
               preferredVehicleType.getEstimatedArrivalMinutes() : 7;
    }
    public boolean isScheduledRide() {
        return isScheduled && scheduledFor != null && scheduledFor.isAfter(LocalDateTime.now());
    }
    public Long getTripDurationMinutes() {
        if (tripStartedAt != null && tripEndedAt != null) {
            return java.time.Duration.between(tripStartedAt, tripEndedAt).toMinutes();
        }
        return null;
    }
    public void recalculateTotalFare() {
        BigDecimal total = baseFare != null ? baseFare : BigDecimal.ZERO;
        if (surgeAmount != null) {
            total = total.add(surgeAmount);
        }
        if (taxAmount != null) {
            total = total.add(taxAmount);
        }
        if (discountAmount != null) {
            total = total.subtract(discountAmount);
        }
        this.totalFare = total;
    }
    public boolean isCancellable() {
        return status == BookingStatus.PENDING ||
               status == BookingStatus.CONFIRMED ||
               (status == BookingStatus.DRIVER_ASSIGNED && driverArrivedAt == null);
    }
    public boolean isInProgress() {
        return status == BookingStatus.IN_PROGRESS &&
               tripStartedAt != null &&
               tripEndedAt == null;
    }
}
