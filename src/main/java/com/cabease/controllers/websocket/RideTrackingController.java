package com.cabease.controllers.websocket;
import com.cabease.service.RideTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@Controller
public class RideTrackingController {
    private static final Logger log = LoggerFactory.getLogger(RideTrackingController.class);
    @Autowired
    private RideTrackingService rideTrackingService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/location/{rideId}")
    public void updateLocation(@DestinationVariable Long rideId, @Payload LocationUpdate locationUpdate) {
        try {
            rideTrackingService.updateDriverLocation(
                rideId,
                locationUpdate.getCabId(),
                locationUpdate.getLatitude(),
                locationUpdate.getLongitude()
            );
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("event", "ERROR");
            error.put("message", e.getMessage());
            messagingTemplate.convertAndSend("/queue/driver/" + locationUpdate.getCabId(), error);
        }
    }
    @MessageMapping("/status/{rideId}")
    @SendTo("/topic/ride/{rideId}")
    public Map<String, Object> updateRideStatus(@DestinationVariable Long rideId, @Payload StatusUpdate statusUpdate) {
        Map<String, Object> response = new HashMap<>();
        response.put("event", "STATUS_CHANGED");
        response.put("rideId", rideId);
        response.put("status", statusUpdate.getStatus());
        response.put("message", statusUpdate.getMessage());
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }
    @PostMapping("/api/v1/tracking/start/{rideId}")
    @ResponseBody
    public Map<String, Object> startTracking(@PathVariable Long rideId) {
        try {
            rideTrackingService.startTracking(rideId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tracking started");
            response.put("rideId", rideId);
            return response;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }
    @PostMapping("/api/v1/tracking/stop/{rideId}")
    @ResponseBody
    public Map<String, Object> stopTracking(@PathVariable Long rideId) {
        try {
            rideTrackingService.stopTracking(rideId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tracking stopped");
            response.put("rideId", rideId);
            return response;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }
    @GetMapping("/api/v1/tracking/{rideId}")
    @ResponseBody
    public Map<String, Object> getTrackingData(@PathVariable Long rideId) {
        try {
            RideTrackingService.RideTrackingSession session = rideTrackingService.getTrackingSession(rideId);
            if (session == null) {
                log.warn("⚠️ No tracking session found for ride {}. Attempting to create...", rideId);
                try {
                    rideTrackingService.startTracking(rideId);
                    session = rideTrackingService.getTrackingSession(rideId);
                    if (session == null) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("error", "Failed to create tracking session. Booking may not exist.");
                        return error;
                    }
                    log.info("✅ Tracking session created for ride: {}", rideId);
                } catch (Exception e) {
                    log.error("❌ Failed to create tracking session for ride {}: {}", rideId, e.getMessage());
                    Map<String, Object> error = new HashMap<>();
                    error.put("error", "Failed to initialize tracking: " + e.getMessage());
                    return error;
                }
            }
            Map<String, Object> response = new HashMap<>();
            response.put("rideId", session.getRideId());
            response.put("driverLatitude", session.getDriverLatitude());
            response.put("driverLongitude", session.getDriverLongitude());
            response.put("pickupLatitude", session.getPickupLatitude());
            response.put("pickupLongitude", session.getPickupLongitude());
            response.put("dropLatitude", session.getDropLatitude());
            response.put("dropLongitude", session.getDropLongitude());
            response.put("distanceToPickup", session.getDistanceToPickup());
            response.put("etaMinutes", session.getEtaMinutes());
            response.put("driverName", session.getDriverName());
            response.put("driverPhone", session.getDriverPhone());
            response.put("vehicleNumber", session.getVehicleNumber());
            response.put("vehicleModel", session.getVehicleModel());
            response.put("status", session.getStatus());
            response.put("lastUpdate", session.getLastUpdate().toString());
            return response;
        } catch (Exception e) {
            log.error("❌ Error getting tracking data for ride {}: {}", rideId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }
    public static class LocationUpdate {
        private Long cabId;
        private BigDecimal latitude;
        private BigDecimal longitude;
        public Long getCabId() { return cabId; }
        public void setCabId(Long cabId) { this.cabId = cabId; }
        public BigDecimal getLatitude() { return latitude; }
        public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
        public BigDecimal getLongitude() { return longitude; }
        public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    }
    public static class StatusUpdate {
        private String status;
        private String message;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
