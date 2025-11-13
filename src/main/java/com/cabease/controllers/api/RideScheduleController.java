package com.cabease.controllers.api;
import com.cabease.entity.RideSchedule;
import com.cabease.models.User;
import com.cabease.service.RideScheduleService;
import com.cabease.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/v1/schedule")
public class RideScheduleController {
    @Autowired
    private RideScheduleService rideScheduleService;
    @Autowired
    private UserService userService;
    @PostMapping
    public ResponseEntity<?> scheduleRide(@RequestBody ScheduleRideRequest request) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }
            LocalDateTime scheduledTime = LocalDateTime.parse(request.getScheduledTime(),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            RideSchedule rideSchedule = rideScheduleService.scheduleRide(
                user,
                request.getPickupLocation(),
                request.getPickupLatitude(),
                request.getPickupLongitude(),
                request.getDropLocation(),
                request.getDropLatitude(),
                request.getDropLongitude(),
                scheduledTime,
                request.getVehicleType(),
                request.getPassengerCount(),
                request.getSpecialInstructions()
            );
            return ResponseEntity.ok(toResponse(rideSchedule));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to schedule ride: " + e.getMessage()));
        }
    }
    @GetMapping
    public ResponseEntity<?> getAllScheduledRides() {
        try {
            User user = getCurrentUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }
            List<RideSchedule> rides = rideScheduleService.getAllRidesByUser(user);
            List<Map<String, Object>> response = rides.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch scheduled rides: " + e.getMessage()));
        }
    }
    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingRides() {
        try {
            User user = getCurrentUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }
            List<RideSchedule> rides = rideScheduleService.getUpcomingRides(user);
            List<Map<String, Object>> response = rides.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch upcoming rides: " + e.getMessage()));
        }
    }
    @GetMapping("/past")
    public ResponseEntity<?> getPastRides() {
        try {
            User user = getCurrentUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }
            List<RideSchedule> rides = rideScheduleService.getPastRides(user);
            List<Map<String, Object>> response = rides.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch past rides: " + e.getMessage()));
        }
    }
    @GetMapping("/today")
    public ResponseEntity<?> getTodayRides() {
        try {
            User user = getCurrentUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }
            List<RideSchedule> rides = rideScheduleService.getTodayRides(user);
            List<Map<String, Object>> response = rides.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch today's rides: " + e.getMessage()));
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getScheduledRide(@PathVariable Long id) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }
            RideSchedule rideSchedule = rideScheduleService.getRideScheduleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scheduled ride not found"));
            if (!rideSchedule.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access denied"));
            }
            return ResponseEntity.ok(toResponse(rideSchedule));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch scheduled ride: " + e.getMessage()));
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyScheduledRide(
            @PathVariable Long id,
            @RequestBody ModifyScheduleRequest request) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }
            LocalDateTime newScheduledTime = null;
            if (request.getScheduledTime() != null) {
                newScheduledTime = LocalDateTime.parse(request.getScheduledTime(),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            RideSchedule rideSchedule = rideScheduleService.modifyScheduledRide(
                id,
                user,
                newScheduledTime,
                request.getSpecialInstructions()
            );
            return ResponseEntity.ok(toResponse(rideSchedule));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to modify scheduled ride: " + e.getMessage()));
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelScheduledRide(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }
            RideSchedule rideSchedule = rideScheduleService.cancelScheduledRide(id, user, reason);
            return ResponseEntity.ok(Map.of(
                "message", "Ride cancelled successfully",
                "ride", toResponse(rideSchedule)
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to cancel scheduled ride: " + e.getMessage()));
        }
    }
    @GetMapping("/count/upcoming")
    public ResponseEntity<?> getUpcomingRidesCount() {
        try {
            User user = getCurrentUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }
            Long count = rideScheduleService.countUpcomingRides(user);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to count upcoming rides: " + e.getMessage()));
        }
    }
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            if (email != null && !email.equals("anonymousUser")) {
                return userService.findByEmail(email).orElse(null);
            }
        }
        return null;
    }
    private Map<String, Object> toResponse(RideSchedule ride) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", ride.getId());
        response.put("pickupLocation", ride.getPickupLocation());
        response.put("pickupLatitude", ride.getPickupLatitude());
        response.put("pickupLongitude", ride.getPickupLongitude());
        response.put("dropLocation", ride.getDropLocation());
        response.put("dropLatitude", ride.getDropLatitude());
        response.put("dropLongitude", ride.getDropLongitude());
        response.put("scheduledTime", ride.getScheduledTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.put("vehicleType", ride.getVehicleType());
        response.put("passengerCount", ride.getPassengerCount());
        response.put("specialInstructions", ride.getSpecialInstructions());
        response.put("status", ride.getStatus());
        response.put("estimatedFare", ride.getEstimatedFare());
        response.put("createdAt", ride.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.put("updatedAt", ride.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        if (ride.getCancelledAt() != null) {
            response.put("cancelledAt", ride.getCancelledAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            response.put("cancellationReason", ride.getCancellationReason());
        }
        return response;
    }
    static class ScheduleRideRequest {
        private String pickupLocation;
        private BigDecimal pickupLatitude;
        private BigDecimal pickupLongitude;
        private String dropLocation;
        private BigDecimal dropLatitude;
        private BigDecimal dropLongitude;
        private String scheduledTime;
        private String vehicleType;
        private Integer passengerCount;
        private String specialInstructions;
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
        public String getScheduledTime() { return scheduledTime; }
        public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }
        public String getVehicleType() { return vehicleType; }
        public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
        public Integer getPassengerCount() { return passengerCount; }
        public void setPassengerCount(Integer passengerCount) { this.passengerCount = passengerCount; }
        public String getSpecialInstructions() { return specialInstructions; }
        public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }
    }
    static class ModifyScheduleRequest {
        private String scheduledTime;
        private String specialInstructions;
        public String getScheduledTime() { return scheduledTime; }
        public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }
        public String getSpecialInstructions() { return specialInstructions; }
        public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }
    }
}
