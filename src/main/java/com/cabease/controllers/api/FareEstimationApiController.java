package com.cabease.controllers.api;
import com.cabease.models.VehicleType;
import com.cabease.services.FareCalculationService;
import com.cabease.services.CabService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/v1/fare")
@CrossOrigin(origins = "*")
@Slf4j
public class FareEstimationApiController {
    @Autowired
    private FareCalculationService fareCalculationService;
    @Autowired
    private CabService cabService;
    @GetMapping("/estimate")
    public ResponseEntity<List<FareEstimateResponse>> getFareEstimates(
            @RequestParam BigDecimal pickupLat,
            @RequestParam BigDecimal pickupLng,
            @RequestParam BigDecimal dropLat,
            @RequestParam BigDecimal dropLng,
            @RequestParam(required = false) String bookingTime) {
        try {
            BigDecimal distance = calculateDistance(pickupLat, pickupLng, dropLat, dropLng);
            LocalDateTime requestTime = bookingTime != null ?
                LocalDateTime.parse(bookingTime) : LocalDateTime.now();
            List<FareEstimateResponse> estimates = generateFareEstimates(
                distance, requestTime, pickupLat, pickupLng);
            log.info("Generated {} fare estimates for distance: {} km", estimates.size(), distance);
            return ResponseEntity.ok(estimates);
        } catch (Exception e) {
            log.error("Error calculating fare estimates: ", e);
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/estimate/{vehicleType}")
    public ResponseEntity<FareEstimateResponse> getFareEstimateForVehicleType(
            @PathVariable VehicleType vehicleType,
            @RequestParam BigDecimal pickupLat,
            @RequestParam BigDecimal pickupLng,
            @RequestParam BigDecimal dropLat,
            @RequestParam BigDecimal dropLng,
            @RequestParam(required = false) String bookingTime) {
        try {
            BigDecimal distance = calculateDistance(pickupLat, pickupLng, dropLat, dropLng);
            LocalDateTime requestTime = bookingTime != null ?
                LocalDateTime.parse(bookingTime) : LocalDateTime.now();
            FareCalculationService.FareEstimate estimate = fareCalculationService.calculateFareEstimate(
                vehicleType, distance, requestTime, pickupLat, pickupLng);
            Long availableCount = cabService.countAvailableCabsByVehicleType(vehicleType);
            FareEstimateResponse response = FareEstimateResponse.builder()
                .vehicleType(vehicleType)
                .displayName(vehicleType.getDisplayName())
                .icon(vehicleType.getIcon())
                .description(vehicleType.getDescription())
                .baseFare(estimate.getBaseFare())
                .surgeMultiplier(estimate.getSurgeMultiplier())
                .surgeAmount(estimate.getSurgeAmount())
                .taxAmount(estimate.getTaxAmount())
                .totalFare(estimate.getTotalFare())
                .estimatedArrivalTime(estimate.getEstimatedArrivalTime())
                .availableCount(availableCount)
                .capacity(vehicleType.getMaxCapacity())
                .supportsSurge(vehicleType.supportsSurge())
                .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error calculating fare estimate for {}: ", vehicleType, e);
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/availability")
    public ResponseEntity<Map<VehicleType, VehicleAvailability>> getVehicleAvailability(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(defaultValue = "5.0") BigDecimal radiusKm) {
        try {
            Map<VehicleType, VehicleAvailability> availability =
                java.util.Arrays.stream(VehicleType.values())
                    .collect(Collectors.toMap(
                        type -> type,
                        type -> {
                            Long count = cabService.countAvailableCabsByVehicleTypeNearby(type, lat, lng, radiusKm);
                            return VehicleAvailability.builder()
                                .vehicleType(type)
                                .availableCount(count)
                                .estimatedArrivalTime(type.getEstimatedArrivalMinutes())
                                .isAvailable(count > 0)
                                .build();
                        }
                    ));
            return ResponseEntity.ok(availability);
        } catch (Exception e) {
            log.error("Error getting vehicle availability: ", e);
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/powerpass-discount")
    public ResponseEntity<PowerPassDiscountResponse> calculatePowerPassDiscount(
            @RequestParam VehicleType vehicleType,
            @RequestParam BigDecimal totalFare,
            @RequestParam(defaultValue = "false") boolean hasPowerPass) {
        BigDecimal discountAmount = fareCalculationService.calculatePowerPassDiscount(
            totalFare, hasPowerPass, vehicleType);
        BigDecimal finalFare = totalFare.subtract(discountAmount);
        PowerPassDiscountResponse response = PowerPassDiscountResponse.builder()
            .vehicleType(vehicleType)
            .originalFare(totalFare)
            .discountAmount(discountAmount)
            .finalFare(finalFare)
            .savingsPercent(totalFare.compareTo(BigDecimal.ZERO) > 0 ?
                discountAmount.multiply(BigDecimal.valueOf(100)).divide(totalFare, 2, java.math.RoundingMode.HALF_UP) :
                BigDecimal.ZERO)
            .hasPowerPass(hasPowerPass)
            .build();
        return ResponseEntity.ok(response);
    }
    private List<FareEstimateResponse> generateFareEstimates(BigDecimal distance,
                                                           LocalDateTime bookingTime,
                                                           BigDecimal pickupLat,
                                                           BigDecimal pickupLng) {
        return java.util.Arrays.stream(VehicleType.values())
            .map(vehicleType -> {
                try {
                    FareCalculationService.FareEstimate estimate = fareCalculationService.calculateFareEstimate(
                        vehicleType, distance, bookingTime, pickupLat, pickupLng);
                    Long availableCount = cabService.countAvailableCabsByVehicleType(vehicleType);
                    return FareEstimateResponse.builder()
                        .vehicleType(vehicleType)
                        .displayName(vehicleType.getDisplayName())
                        .icon(vehicleType.getIcon())
                        .description(vehicleType.getDescription())
                        .baseFare(estimate.getBaseFare())
                        .surgeMultiplier(estimate.getSurgeMultiplier())
                        .surgeAmount(estimate.getSurgeAmount())
                        .taxAmount(estimate.getTaxAmount())
                        .totalFare(estimate.getTotalFare())
                        .estimatedArrivalTime(estimate.getEstimatedArrivalTime())
                        .availableCount(availableCount)
                        .capacity(vehicleType.getMaxCapacity())
                        .supportsSurge(vehicleType.supportsSurge())
                        .isRecommended(vehicleType == VehicleType.CAB)
                        .build();
                } catch (Exception e) {
                    log.warn("Failed to generate estimate for {}: {}", vehicleType, e.getMessage());
                    return null;
                }
            })
            .filter(java.util.Objects::nonNull)
            .sorted((a, b) -> a.getTotalFare().compareTo(b.getTotalFare()))
            .collect(Collectors.toList());
    }
    private BigDecimal calculateDistance(BigDecimal lat1, BigDecimal lng1, BigDecimal lat2, BigDecimal lng2) {
        double lat1Rad = Math.toRadians(lat1.doubleValue());
        double lng1Rad = Math.toRadians(lng1.doubleValue());
        double lat2Rad = Math.toRadians(lat2.doubleValue());
        double lng2Rad = Math.toRadians(lng2.doubleValue());
        double dlat = lat2Rad - lat1Rad;
        double dlng = lng2Rad - lng1Rad;
        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(dlng / 2) * Math.sin(dlng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = 6371 * c;
        return BigDecimal.valueOf(distance).setScale(2, java.math.RoundingMode.HALF_UP);
    }
    @lombok.Builder
    @lombok.Data
    public static class FareEstimateResponse {
        private VehicleType vehicleType;
        private String displayName;
        private String icon;
        private String description;
        private BigDecimal baseFare;
        private BigDecimal surgeMultiplier;
        private BigDecimal surgeAmount;
        private BigDecimal taxAmount;
        private BigDecimal totalFare;
        private int estimatedArrivalTime;
        private Long availableCount;
        private int capacity;
        private boolean supportsSurge;
        private boolean isRecommended;
    }
    @lombok.Builder
    @lombok.Data
    public static class VehicleAvailability {
        private VehicleType vehicleType;
        private Long availableCount;
        private int estimatedArrivalTime;
        private boolean isAvailable;
    }
    @lombok.Builder
    @lombok.Data
    public static class PowerPassDiscountResponse {
        private VehicleType vehicleType;
        private BigDecimal originalFare;
        private BigDecimal discountAmount;
        private BigDecimal finalFare;
        private BigDecimal savingsPercent;
        private boolean hasPowerPass;
    }
}
