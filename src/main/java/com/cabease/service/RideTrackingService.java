package com.cabease.service;
import com.cabease.models.Booking;
import com.cabease.models.BookingStatus;
import com.cabease.models.Cab;
import com.cabease.repository.BookingRepository;
import com.cabease.repository.CabRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
@Service
@Transactional
@Slf4j
public class RideTrackingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CabRepository cabRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private DriverSimulatorService driverSimulatorService;
    private final Map<Long, RideTrackingSession> activeSessions = new ConcurrentHashMap<>();
    public void updateDriverLocation(Long rideId, Long cabId, BigDecimal latitude, BigDecimal longitude) {
        Optional<Booking> bookingOpt = bookingRepository.findById(rideId);
        if (!bookingOpt.isPresent()) {
            throw new IllegalArgumentException("Ride not found");
        }
        Booking booking = bookingOpt.get();
        if (!isTrackable(booking.getStatus())) {
            throw new IllegalArgumentException("Ride is not in a trackable state: " + booking.getStatus());
        }
        Optional<Cab> cabOpt = cabRepository.findById(cabId);
        if (cabOpt.isPresent()) {
            Cab cab = cabOpt.get();
            cab.setCurrentLatitude(latitude);
            cab.setCurrentLongitude(longitude);
            cab.setLastLocationUpdate(LocalDateTime.now());
            cabRepository.save(cab);
        }
        double distanceKm = calculateDistance(
            latitude,
            longitude,
            booking.getPickupLatitude(),
            booking.getPickupLongitude()
        );
        int etaMinutes = calculateETA(distanceKm);
        RideTrackingSession session = activeSessions.computeIfAbsent(rideId, id -> new RideTrackingSession(id));
        session.setDriverLatitude(latitude);
        session.setDriverLongitude(longitude);
        session.setDistanceToPickup(BigDecimal.valueOf(distanceKm));
        session.setEtaMinutes(etaMinutes);
        session.setLastUpdate(LocalDateTime.now());
        broadcastLocationUpdate(rideId, session);
    }
    public void startTracking(Long rideId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(rideId);
        if (!bookingOpt.isPresent()) {
            return;
        }
        Booking booking = bookingOpt.get();
        Cab cab = booking.getCab();
        if (cab == null) {
            return;
        }
        RideTrackingSession session = new RideTrackingSession(rideId);
        session.setDriverLatitude(cab.getCurrentLatitude());
        session.setDriverLongitude(cab.getCurrentLongitude());
        session.setPickupLatitude(booking.getPickupLatitude());
        session.setPickupLongitude(booking.getPickupLongitude());
        session.setDropLatitude(booking.getDropLatitude());
        session.setDropLongitude(booking.getDropLongitude());
        session.setDriverName(cab.getDriver().getName());
        session.setDriverPhone(cab.getDriver().getPhoneNumber());
        session.setVehicleNumber(cab.getRegistrationNumber());
        session.setVehicleModel(cab.getBrand() + " " + cab.getModel());
        session.setStatus(booking.getStatus());
        activeSessions.put(rideId, session);
        broadcastLocationUpdate(rideId, session);
        try {
            driverSimulatorService.startSimulator(rideId);
            log.info("✅ Driver simulator auto-started for ride: {}", rideId);
        } catch (Exception e) {
            log.error("❌ Failed to auto-start driver simulator for ride {}: {}", rideId, e.getMessage(), e);
        }
    }
    public void stopTracking(Long rideId) {
        activeSessions.remove(rideId);
        try {
            driverSimulatorService.stopSimulator(rideId);
            log.info("🛑 Driver simulator stopped for ride: {}", rideId);
        } catch (Exception e) {
            log.warn("⚠️ Failed to stop driver simulator for ride {}: {}", rideId, e.getMessage());
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("event", "TRACKING_ENDED");
        payload.put("rideId", rideId);
        payload.put("timestamp", LocalDateTime.now().toString());
        messagingTemplate.convertAndSend("/topic/ride/" + rideId, payload);
    }
    public RideTrackingSession getTrackingSession(Long rideId) {
        return activeSessions.get(rideId);
    }
    private void broadcastLocationUpdate(Long rideId, RideTrackingSession session) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("event", "LOCATION_UPDATE");
        payload.put("rideId", rideId);
        payload.put("driverLatitude", session.getDriverLatitude());
        payload.put("driverLongitude", session.getDriverLongitude());
        payload.put("distanceToPickup", session.getDistanceToPickup());
        payload.put("etaMinutes", session.getEtaMinutes());
        payload.put("driverName", session.getDriverName());
        payload.put("driverPhone", session.getDriverPhone());
        payload.put("vehicleNumber", session.getVehicleNumber());
        payload.put("vehicleModel", session.getVehicleModel());
        payload.put("status", session.getStatus() != null ? session.getStatus().name() : "UNKNOWN");
        payload.put("timestamp", session.getLastUpdate().toString());
        messagingTemplate.convertAndSend("/topic/ride/" + rideId, payload);
    }
    private boolean isTrackable(BookingStatus status) {
        return status == BookingStatus.CONFIRMED ||
               status == BookingStatus.IN_PROGRESS ||
               status == BookingStatus.DRIVER_ASSIGNED;
    }
    private double calculateDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double lonDistance = Math.toRadians(lon2.doubleValue() - lon1.doubleValue());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1.doubleValue())) * Math.cos(Math.toRadians(lat2.doubleValue()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;
        return distance;
    }
    private int calculateETA(double distanceKm) {
        double averageSpeedKmH = 30.0;
        double hours = distanceKm / averageSpeedKmH;
        return (int) Math.ceil(hours * 60);
    }
    public static class RideTrackingSession {
        private Long rideId;
        private BigDecimal driverLatitude;
        private BigDecimal driverLongitude;
        private BigDecimal pickupLatitude;
        private BigDecimal pickupLongitude;
        private BigDecimal dropLatitude;
        private BigDecimal dropLongitude;
        private BigDecimal distanceToPickup;
        private Integer etaMinutes;
        private String driverName;
        private String driverPhone;
        private String vehicleNumber;
        private String vehicleModel;
        private BookingStatus status;
        private LocalDateTime lastUpdate;
        public RideTrackingSession(Long rideId) {
            this.rideId = rideId;
            this.lastUpdate = LocalDateTime.now();
        }
        public Long getRideId() { return rideId; }
        public void setRideId(Long rideId) { this.rideId = rideId; }
        public BigDecimal getDriverLatitude() { return driverLatitude; }
        public void setDriverLatitude(BigDecimal driverLatitude) { this.driverLatitude = driverLatitude; }
        public BigDecimal getDriverLongitude() { return driverLongitude; }
        public void setDriverLongitude(BigDecimal driverLongitude) { this.driverLongitude = driverLongitude; }
        public BigDecimal getPickupLatitude() { return pickupLatitude; }
        public void setPickupLatitude(BigDecimal pickupLatitude) { this.pickupLatitude = pickupLatitude; }
        public BigDecimal getPickupLongitude() { return pickupLongitude; }
        public void setPickupLongitude(BigDecimal pickupLongitude) { this.pickupLongitude = pickupLongitude; }
        public BigDecimal getDropLatitude() { return dropLatitude; }
        public void setDropLatitude(BigDecimal dropLatitude) { this.dropLatitude = dropLatitude; }
        public BigDecimal getDropLongitude() { return dropLongitude; }
        public void setDropLongitude(BigDecimal dropLongitude) { this.dropLongitude = dropLongitude; }
        public BigDecimal getDistanceToPickup() { return distanceToPickup; }
        public void setDistanceToPickup(BigDecimal distanceToPickup) { this.distanceToPickup = distanceToPickup; }
        public Integer getEtaMinutes() { return etaMinutes; }
        public void setEtaMinutes(Integer etaMinutes) { this.etaMinutes = etaMinutes; }
        public String getDriverName() { return driverName; }
        public void setDriverName(String driverName) { this.driverName = driverName; }
        public String getDriverPhone() { return driverPhone; }
        public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }
        public String getVehicleNumber() { return vehicleNumber; }
        public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
        public String getVehicleModel() { return vehicleModel; }
        public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
        public BookingStatus getStatus() { return status; }
        public void setStatus(BookingStatus status) { this.status = status; }
        public LocalDateTime getLastUpdate() { return lastUpdate; }
        public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }
    }
}
