package com.cabease.service;
import com.cabease.models.Booking;
import com.cabease.models.Cab;
import com.cabease.repository.BookingRepository;
import com.cabease.repository.CabRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
@Service
@Slf4j
public class DriverSimulatorService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CabRepository cabRepository;
    @Autowired
    private org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private final Map<Long, ScheduledFuture<?>> activeSimulators = new ConcurrentHashMap<>();
    @Async
    public void startSimulator(Long rideId) {
        if (activeSimulators.containsKey(rideId)) {
            log.warn("⚠️ Simulator already running for ride: {}", rideId);
            return;
        }
        Optional<Booking> bookingOpt = bookingRepository.findByIdWithCabAndDriver(rideId);
        if (!bookingOpt.isPresent()) {
            log.error("❌ Booking not found for simulator: {}", rideId);
            return;
        }
        Booking booking = bookingOpt.get();
        Cab cab = booking.getCab();
        if (cab == null) {
            log.error("❌ No cab assigned for ride: {}", rideId);
            return;
        }
        String driverName = "N/A";
        String cabNumber = cab.getCabNumber();
        Long cabId = cab.getId();
        BigDecimal cabLat = cab.getCurrentLatitude();
        BigDecimal cabLng = cab.getCurrentLongitude();
        try {
            if (cab.getDriver() != null) {
                driverName = cab.getDriver().getName();
            }
        } catch (Exception e) {
            log.warn("Could not fetch driver name: {}", e.getMessage());
        }
        log.info("🚗 AUTO-STARTING DRIVER SIMULATOR for Ride #{} | Driver: {} | Cab: {}",
            rideId, driverName, cabNumber);
        DriverSimulatorState state = new DriverSimulatorState(
            rideId,
            cabId,
            cabLat,
            cabLng,
            booking.getPickupLatitude(),
            booking.getPickupLongitude(),
            booking.getDropLatitude(),
            booking.getDropLongitude()
        );
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
            () -> simulateDriverMovement(state),
            0,
            3,
            TimeUnit.SECONDS
        );
        activeSimulators.put(rideId, future);
        log.info("✅ Driver simulator started for ride: {} (updates every 3 seconds)", rideId);
    }
    public void stopSimulator(Long rideId) {
        ScheduledFuture<?> future = activeSimulators.remove(rideId);
        if (future != null) {
            future.cancel(false);
            log.info("🛑 Driver simulator stopped for ride: {}", rideId);
        }
    }
    private void simulateDriverMovement(DriverSimulatorState state) {
        try {
            BigDecimal targetLat = state.isMovingToPickup() ? state.getPickupLat() : state.getDropLat();
            BigDecimal targetLng = state.isMovingToPickup() ? state.getPickupLng() : state.getDropLng();
            if (targetLat == null || targetLng == null) {
                log.warn("⚠️ Target coordinates missing for ride: {}. Stopping simulator.", state.getRideId());
                stopSimulator(state.getRideId());
                return;
            }
            double distance = calculateDistance(
                state.getCurrentLat(),
                state.getCurrentLng(),
                targetLat,
                targetLng
            );
            if (distance < 0.05) {
                if (state.isMovingToPickup()) {
                    log.info("📍 Driver reached PICKUP location for ride: {}", state.getRideId());
                    state.setMovingToPickup(false);
                } else {
                    log.info("🎯 Driver reached DROP location for ride: {}", state.getRideId());
                    stopSimulator(state.getRideId());
                    return;
                }
            }
            double stepSize = 0.025;
            BigDecimal[] newPosition = moveToward(
                state.getCurrentLat(),
                state.getCurrentLng(),
                targetLat,
                targetLng,
                stepSize
            );
            state.setCurrentLat(newPosition[0]);
            state.setCurrentLng(newPosition[1]);
            updateCabLocation(state.getCabId(), newPosition[0], newPosition[1]);
            broadcastLocationUpdate(
                state.getRideId(),
                state.getCabId(),
                newPosition[0],
                newPosition[1]
            );
            log.debug("🚗 Driver moved: Ride #{} | Distance to target: {:.2f} km",
                state.getRideId(), distance);
        } catch (Exception e) {
            log.error("❌ Error in driver simulator for ride {}: {}", state.getRideId(), e.getMessage(), e);
            stopSimulator(state.getRideId());
        }
    }
    private void updateCabLocation(Long cabId, BigDecimal lat, BigDecimal lng) {
        Optional<Cab> cabOpt = cabRepository.findById(cabId);
        if (cabOpt.isPresent()) {
            Cab cab = cabOpt.get();
            cab.setCurrentLatitude(lat);
            cab.setCurrentLongitude(lng);
            cabRepository.save(cab);
        }
    }
    private void broadcastLocationUpdate(Long rideId, Long cabId, BigDecimal lat, BigDecimal lng) {
        java.util.Map<String, Object> locationData = new java.util.HashMap<>();
        locationData.put("rideId", rideId);
        locationData.put("cabId", cabId);
        locationData.put("latitude", lat);
        locationData.put("longitude", lng);
        locationData.put("timestamp", System.currentTimeMillis());
        messagingTemplate.convertAndSend("/topic/ride/" + rideId, locationData);
        log.debug("📡 Broadcasted location for ride: {}", rideId);
    }
    private BigDecimal[] moveToward(BigDecimal currentLat, BigDecimal currentLng,
                                     BigDecimal targetLat, BigDecimal targetLng,
                                     double stepSize) {
        double distance = calculateDistance(currentLat, currentLng, targetLat, targetLng);
        if (distance <= stepSize) {
            return new BigDecimal[] { targetLat, targetLng };
        }
        double ratio = stepSize / distance;
        double newLat = currentLat.doubleValue() + ratio * (targetLat.doubleValue() - currentLat.doubleValue());
        double newLng = currentLng.doubleValue() + ratio * (targetLng.doubleValue() - currentLng.doubleValue());
        return new BigDecimal[] {
            BigDecimal.valueOf(newLat),
            BigDecimal.valueOf(newLng)
        };
    }
    private double calculateDistance(BigDecimal lat1, BigDecimal lng1, BigDecimal lat2, BigDecimal lng2) {
        final int EARTH_RADIUS_KM = 6371;
        double latDistance = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double lonDistance = Math.toRadians(lng2.doubleValue() - lng1.doubleValue());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1.doubleValue())) * Math.cos(Math.toRadians(lat2.doubleValue()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
    private static class DriverSimulatorState {
        private final Long rideId;
        private final Long cabId;
        private BigDecimal currentLat;
        private BigDecimal currentLng;
        private final BigDecimal pickupLat;
        private final BigDecimal pickupLng;
        private final BigDecimal dropLat;
        private final BigDecimal dropLng;
        private boolean movingToPickup = true;
        public DriverSimulatorState(Long rideId, Long cabId,
                                   BigDecimal currentLat, BigDecimal currentLng,
                                   BigDecimal pickupLat, BigDecimal pickupLng,
                                   BigDecimal dropLat, BigDecimal dropLng) {
            this.rideId = rideId;
            this.cabId = cabId;
            this.currentLat = currentLat;
            this.currentLng = currentLng;
            this.pickupLat = pickupLat;
            this.pickupLng = pickupLng;
            this.dropLat = dropLat;
            this.dropLng = dropLng;
        }
        public DriverSimulatorState(Long rideId, Booking booking, Cab cab) {
            this.rideId = rideId;
            this.cabId = cab.getId();
            this.currentLat = cab.getCurrentLatitude();
            this.currentLng = cab.getCurrentLongitude();
            this.pickupLat = booking.getPickupLatitude();
            this.pickupLng = booking.getPickupLongitude();
            this.dropLat = booking.getDropLatitude();
            this.dropLng = booking.getDropLongitude();
        }
        public Long getRideId() { return rideId; }
        public Long getCabId() { return cabId; }
        public BigDecimal getCurrentLat() { return currentLat; }
        public void setCurrentLat(BigDecimal lat) { this.currentLat = lat; }
        public BigDecimal getCurrentLng() { return currentLng; }
        public void setCurrentLng(BigDecimal lng) { this.currentLng = lng; }
        public BigDecimal getPickupLat() { return pickupLat; }
        public BigDecimal getPickupLng() { return pickupLng; }
        public BigDecimal getDropLat() { return dropLat; }
        public BigDecimal getDropLng() { return dropLng; }
        public boolean isMovingToPickup() { return movingToPickup; }
        public void setMovingToPickup(boolean movingToPickup) { this.movingToPickup = movingToPickup; }
    }
    public void shutdown() {
        log.info("🛑 Shutting down all driver simulators...");
        activeSimulators.values().forEach(future -> future.cancel(false));
        activeSimulators.clear();
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
