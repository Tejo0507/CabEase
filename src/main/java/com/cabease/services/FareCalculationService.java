package com.cabease.services;
import com.cabease.models.VehicleType;
import com.cabease.models.Booking;
import com.cabease.models.Cab;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
@Service
@Slf4j
public class FareCalculationService {
    private static final BigDecimal BASE_SURGE_MULTIPLIER = BigDecimal.valueOf(1.0);
    private static final BigDecimal MAX_SURGE_MULTIPLIER = BigDecimal.valueOf(3.0);
    private static final BigDecimal TAX_RATE = BigDecimal.valueOf(0.05);
    public FareEstimate calculateFareEstimate(VehicleType vehicleType, BigDecimal distance,
                                           LocalDateTime bookingTime, BigDecimal pickupLat,
                                           BigDecimal pickupLng) {
        log.info("Calculating fare for {} vehicle, distance: {} km", vehicleType, distance);
        BigDecimal baseFare = calculateBaseFare(vehicleType, distance);
        BigDecimal surgeMultiplier = calculateSurgeMultiplier(vehicleType, bookingTime, pickupLat, pickupLng);
        BigDecimal surgeAmount = baseFare.multiply(surgeMultiplier.subtract(BigDecimal.ONE));
        BigDecimal taxAmount = baseFare.add(surgeAmount).multiply(TAX_RATE);
        BigDecimal totalFare = baseFare.add(surgeAmount).add(taxAmount);
        totalFare = totalFare.setScale(2, RoundingMode.HALF_UP);
        FareEstimate estimate = FareEstimate.builder()
            .vehicleType(vehicleType)
            .distance(distance)
            .baseFare(baseFare)
            .surgeMultiplier(surgeMultiplier)
            .surgeAmount(surgeAmount)
            .taxAmount(taxAmount)
            .totalFare(totalFare)
            .estimatedArrivalTime(vehicleType.getEstimatedArrivalMinutes())
            .build();
        log.info("Fare estimate: Base={}, Surge={}x, Total={}", baseFare, surgeMultiplier, totalFare);
        return estimate;
    }
    private BigDecimal calculateBaseFare(VehicleType vehicleType, BigDecimal distance) {
        BigDecimal baseRate = BigDecimal.valueOf(vehicleType.getBaseFare());
        BigDecimal fareMultiplier = BigDecimal.valueOf(vehicleType.getFareMultiplier());
        BigDecimal minimumDistance = BigDecimal.valueOf(2.0);
        BigDecimal chargeable = distance.max(minimumDistance);
        return baseRate.add(chargeable.multiply(fareMultiplier)).setScale(2, RoundingMode.HALF_UP);
    }
    private BigDecimal calculateSurgeMultiplier(VehicleType vehicleType, LocalDateTime bookingTime,
                                              BigDecimal lat, BigDecimal lng) {
        if (!vehicleType.supportsSurge()) {
            return BASE_SURGE_MULTIPLIER;
        }
        BigDecimal surge = BASE_SURGE_MULTIPLIER;
        surge = surge.add(getTimeSurge(bookingTime));
        surge = surge.add(getDaySurge(bookingTime));
        surge = surge.add(getWeatherSurge());
        surge = surge.add(getLocationSurge(lat, lng));
        return surge.min(MAX_SURGE_MULTIPLIER);
    }
    private BigDecimal getTimeSurge(LocalDateTime bookingTime) {
        LocalTime time = bookingTime.toLocalTime();
        if (time.isAfter(LocalTime.of(7, 30)) && time.isBefore(LocalTime.of(10, 30))) {
            return BigDecimal.valueOf(0.3);
        }
        if (time.isAfter(LocalTime.of(17, 30)) && time.isBefore(LocalTime.of(21, 30))) {
            return BigDecimal.valueOf(0.4);
        }
        if (time.isAfter(LocalTime.of(23, 0)) || time.isBefore(LocalTime.of(5, 0))) {
            return BigDecimal.valueOf(0.2);
        }
        return BigDecimal.ZERO;
    }
    private BigDecimal getDaySurge(LocalDateTime bookingTime) {
        DayOfWeek dayOfWeek = bookingTime.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return BigDecimal.valueOf(0.15);
        }
        if (dayOfWeek == DayOfWeek.FRIDAY &&
            bookingTime.getHour() >= 18) {
            return BigDecimal.valueOf(0.2);
        }
        return BigDecimal.ZERO;
    }
    private BigDecimal getWeatherSurge() {
        double random = Math.random();
        if (random < 0.1) {
            return BigDecimal.valueOf(0.25);
        }
        return BigDecimal.ZERO;
    }
    private BigDecimal getLocationSurge(BigDecimal lat, BigDecimal lng) {
        Map<String, BigDecimal> highDemandSurge = getHighDemandAreas();
        for (Map.Entry<String, BigDecimal> area : highDemandSurge.entrySet()) {
            if (isInHighDemandArea(lat, lng, area.getKey())) {
                return area.getValue();
            }
        }
        return BigDecimal.ZERO;
    }
    private Map<String, BigDecimal> getHighDemandAreas() {
        Map<String, BigDecimal> areas = new HashMap<>();
        areas.put("airport", BigDecimal.valueOf(0.5));
        areas.put("railway_station", BigDecimal.valueOf(0.3));
        areas.put("mall", BigDecimal.valueOf(0.2));
        areas.put("business_district", BigDecimal.valueOf(0.25));
        areas.put("hospital", BigDecimal.valueOf(0.15));
        return areas;
    }
    private boolean isInHighDemandArea(BigDecimal lat, BigDecimal lng, String areaType) {
        return Math.random() < 0.15;
    }
    public BigDecimal calculatePowerPassDiscount(BigDecimal totalFare, boolean hasPowerPass, VehicleType vehicleType) {
        if (!hasPowerPass) {
            return BigDecimal.ZERO;
        }
        BigDecimal discountPercent;
        switch (vehicleType) {
            case BIKE:
                discountPercent = BigDecimal.valueOf(0.15);
                break;
            case AUTO:
                discountPercent = BigDecimal.valueOf(0.10);
                break;
            case CAB:
            case MICRO:
                discountPercent = BigDecimal.valueOf(0.05);
                break;
            default:
                discountPercent = BigDecimal.ZERO;
                break;
        }
        return totalFare.multiply(discountPercent).setScale(2, RoundingMode.HALF_UP);
    }
    @lombok.Builder
    @lombok.Data
    public static class FareEstimate {
        private VehicleType vehicleType;
        private BigDecimal distance;
        private BigDecimal baseFare;
        private BigDecimal surgeMultiplier;
        private BigDecimal surgeAmount;
        private BigDecimal taxAmount;
        private BigDecimal discountAmount;
        private BigDecimal totalFare;
        private int estimatedArrivalTime;
        private String surgeReason;
    }
}
