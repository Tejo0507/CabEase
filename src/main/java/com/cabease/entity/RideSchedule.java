package com.cabease.entity;
import com.cabease.models.Booking;
import com.cabease.models.User;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "ride_schedules")
public class RideSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "pickup_location", nullable = false, length = 500)
    private String pickupLocation;
    @Column(name = "pickup_latitude", precision = 10, scale = 8)
    private BigDecimal pickupLatitude;
    @Column(name = "pickup_longitude", precision = 11, scale = 8)
    private BigDecimal pickupLongitude;
    @Column(name = "drop_location", nullable = false, length = 500)
    private String dropLocation;
    @Column(name = "drop_latitude", precision = 10, scale = 8)
    private BigDecimal dropLatitude;
    @Column(name = "drop_longitude", precision = 11, scale = 8)
    private BigDecimal dropLongitude;
    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;
    @Column(name = "vehicle_type", nullable = false, length = 50)
    private String vehicleType;
    @Column(name = "passenger_count")
    private Integer passengerCount = 1;
    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;
    @Column(name = "status", length = 50)
    private String status = "SCHEDULED";
    @Column(name = "estimated_fare", precision = 19, scale = 2)
    private BigDecimal estimatedFare;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public BigDecimal getPickupLatitude() { return pickupLatitude; }
    public void setPickupLatitude(BigDecimal pickupLatitude) { this.pickupLatitude = pickupLatitude; }
    public BigDecimal getPickupLongitude() { return pickupLongitude; }
    public void setPickupLongitude(BigDecimal pickupLongitude) { this.pickupLongitude = pickupLongitude; }
    public String getDropLocation() { return dropLocation; }
    public void setDropLocation(String dropLocation) { this.dropLocation = dropLocation; }
    public BigDecimal getDropLatitude() { return dropLatitude; }
    public void setDropLatitude(BigDecimal dropLatitude) { this.dropLatitude = dropLatitude; }
    public BigDecimal getDropLongitude() { return dropLongitude; }
    public void setDropLongitude(BigDecimal dropLongitude) { this.dropLongitude = dropLongitude; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public Integer getPassengerCount() { return passengerCount; }
    public void setPassengerCount(Integer passengerCount) { this.passengerCount = passengerCount; }
    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getEstimatedFare() { return estimatedFare; }
    public void setEstimatedFare(BigDecimal estimatedFare) { this.estimatedFare = estimatedFare; }
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
}
