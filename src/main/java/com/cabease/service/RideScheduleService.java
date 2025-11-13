package com.cabease.service;
import com.cabease.entity.RideSchedule;
import com.cabease.models.User;
import com.cabease.models.VehicleType;
import com.cabease.repository.RideScheduleRepository;
import com.cabease.services.FareCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
@Transactional
public class RideScheduleService {
    @Autowired
    private RideScheduleRepository rideScheduleRepository;
    @Autowired
    private FareCalculationService fareCalculationService;
    public RideSchedule scheduleRide(
            User user,
            String pickupLocation,
            BigDecimal pickupLat,
            BigDecimal pickupLng,
            String dropLocation,
            BigDecimal dropLat,
            BigDecimal dropLng,
            LocalDateTime scheduledTime,
            String vehicleType,
            Integer passengerCount,
            String specialInstructions) {
        if (scheduledTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Scheduled time must be in the future");
        }
        if (scheduledTime.isAfter(LocalDateTime.now().plusDays(30))) {
            throw new IllegalArgumentException("Cannot schedule rides more than 30 days in advance");
        }
        BigDecimal distance = calculateDistance(pickupLat, pickupLng, dropLat, dropLng);
        FareCalculationService.FareEstimate fareEstimate = fareCalculationService.calculateFareEstimate(
            VehicleType.valueOf(vehicleType),
            distance,
            scheduledTime,
            pickupLat,
            pickupLng
        );
        RideSchedule rideSchedule = new RideSchedule();
        rideSchedule.setUser(user);
        rideSchedule.setPickupLocation(pickupLocation);
        rideSchedule.setPickupLatitude(pickupLat);
        rideSchedule.setPickupLongitude(pickupLng);
        rideSchedule.setDropLocation(dropLocation);
        rideSchedule.setDropLatitude(dropLat);
        rideSchedule.setDropLongitude(dropLng);
        rideSchedule.setScheduledTime(scheduledTime);
        rideSchedule.setVehicleType(vehicleType);
        rideSchedule.setPassengerCount(passengerCount != null ? passengerCount : 1);
        rideSchedule.setSpecialInstructions(specialInstructions);
        rideSchedule.setStatus("SCHEDULED");
        rideSchedule.setEstimatedFare(fareEstimate.getTotalFare());
        rideSchedule.setCreatedAt(LocalDateTime.now());
        rideSchedule.setUpdatedAt(LocalDateTime.now());
        return rideScheduleRepository.save(rideSchedule);
    }
    public Optional<RideSchedule> getRideScheduleById(Long id) {
        return rideScheduleRepository.findById(id);
    }
    public List<RideSchedule> getAllRidesByUser(User user) {
        return rideScheduleRepository.findByUserOrderByScheduledTimeDesc(user);
    }
    public List<RideSchedule> getUpcomingRides(User user) {
        return rideScheduleRepository.findUpcomingRidesByUser(user, LocalDateTime.now());
    }
    public List<RideSchedule> getPastRides(User user) {
        return rideScheduleRepository.findPastRidesByUser(user, LocalDateTime.now());
    }
    public List<RideSchedule> getTodayRides(User user) {
        return rideScheduleRepository.findTodayRidesByUser(user, LocalDateTime.now());
    }
    public Long countUpcomingRides(User user) {
        return rideScheduleRepository.countUpcomingRidesByUser(user, LocalDateTime.now());
    }
    public RideSchedule modifyScheduledRide(
            Long scheduleId,
            User user,
            LocalDateTime newScheduledTime,
            String newSpecialInstructions) {
        Optional<RideSchedule> optionalSchedule = rideScheduleRepository.findById(scheduleId);
        if (!optionalSchedule.isPresent()) {
            throw new IllegalArgumentException("Scheduled ride not found");
        }
        RideSchedule rideSchedule = optionalSchedule.get();
        if (!rideSchedule.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You are not authorized to modify this scheduled ride");
        }
        if ("CANCELLED".equals(rideSchedule.getStatus()) || "COMPLETED".equals(rideSchedule.getStatus())) {
            throw new IllegalArgumentException("Cannot modify a " + rideSchedule.getStatus().toLowerCase() + " ride");
        }
        if (newScheduledTime != null) {
            if (newScheduledTime.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("New scheduled time must be in the future");
            }
            BigDecimal distance = calculateDistance(
                rideSchedule.getPickupLatitude(),
                rideSchedule.getPickupLongitude(),
                rideSchedule.getDropLatitude(),
                rideSchedule.getDropLongitude()
            );
            FareCalculationService.FareEstimate fareEstimate = fareCalculationService.calculateFareEstimate(
                VehicleType.valueOf(rideSchedule.getVehicleType()),
                distance,
                newScheduledTime,
                rideSchedule.getPickupLatitude(),
                rideSchedule.getPickupLongitude()
            );
            rideSchedule.setScheduledTime(newScheduledTime);
            rideSchedule.setEstimatedFare(fareEstimate.getTotalFare());
        }
        if (newSpecialInstructions != null) {
            rideSchedule.setSpecialInstructions(newSpecialInstructions);
        }
        rideSchedule.setUpdatedAt(LocalDateTime.now());
        return rideScheduleRepository.save(rideSchedule);
    }
    public RideSchedule cancelScheduledRide(Long scheduleId, User user, String cancellationReason) {
        Optional<RideSchedule> optionalSchedule = rideScheduleRepository.findById(scheduleId);
        if (!optionalSchedule.isPresent()) {
            throw new IllegalArgumentException("Scheduled ride not found");
        }
        RideSchedule rideSchedule = optionalSchedule.get();
        if (!rideSchedule.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You are not authorized to cancel this scheduled ride");
        }
        if ("CANCELLED".equals(rideSchedule.getStatus())) {
            throw new IllegalArgumentException("Ride is already cancelled");
        }
        if ("COMPLETED".equals(rideSchedule.getStatus())) {
            throw new IllegalArgumentException("Cannot cancel a completed ride");
        }
        rideSchedule.setStatus("CANCELLED");
        rideSchedule.setCancelledAt(LocalDateTime.now());
        rideSchedule.setCancellationReason(cancellationReason != null ? cancellationReason : "User cancelled");
        rideSchedule.setUpdatedAt(LocalDateTime.now());
        return rideScheduleRepository.save(rideSchedule);
    }
    public RideSchedule confirmScheduledRide(Long scheduleId) {
        Optional<RideSchedule> optionalSchedule = rideScheduleRepository.findById(scheduleId);
        if (!optionalSchedule.isPresent()) {
            throw new IllegalArgumentException("Scheduled ride not found");
        }
        RideSchedule rideSchedule = optionalSchedule.get();
        if (!"SCHEDULED".equals(rideSchedule.getStatus())) {
            throw new IllegalArgumentException("Only scheduled rides can be confirmed");
        }
        rideSchedule.setStatus("CONFIRMED");
        rideSchedule.setUpdatedAt(LocalDateTime.now());
        return rideScheduleRepository.save(rideSchedule);
    }
    public RideSchedule completeScheduledRide(Long scheduleId) {
        Optional<RideSchedule> optionalSchedule = rideScheduleRepository.findById(scheduleId);
        if (!optionalSchedule.isPresent()) {
            throw new IllegalArgumentException("Scheduled ride not found");
        }
        RideSchedule rideSchedule = optionalSchedule.get();
        rideSchedule.setStatus("COMPLETED");
        rideSchedule.setUpdatedAt(LocalDateTime.now());
        return rideScheduleRepository.save(rideSchedule);
    }
    private BigDecimal calculateDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        final int EARTH_RADIUS = 6371;
        double dLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double dLon = Math.toRadians(lon2.doubleValue() - lon1.doubleValue());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1.doubleValue()))
                * Math.cos(Math.toRadians(lat2.doubleValue()))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;
        return BigDecimal.valueOf(Math.round(distance * 100.0) / 100.0);
    }
}
