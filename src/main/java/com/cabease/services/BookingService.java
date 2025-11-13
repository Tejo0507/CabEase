package com.cabease.services;
import com.cabease.models.Booking;
import com.cabease.models.BookingStatus;
import com.cabease.models.Cab;
import com.cabease.models.User;
import com.cabease.models.VehicleType;
import com.cabease.repository.BookingRepository;
import com.cabease.repository.CabRepository;
import com.cabease.repository.UserRepository;
import com.cabease.service.RideTrackingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
@Transactional
@Slf4j
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CabRepository cabRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private FareCalculationService fareCalculationService;
    @Autowired
    private RideTrackingService rideTrackingService;
    public List<Booking> findAllBookings() {
        return bookingRepository.findAll();
    }
    public List<Booking> findBookingsByUser(User user) {
        return bookingRepository.findByUser(user);
    }
    public List<Booking> findBookingsByUserAndStatus(User user, BookingStatus status) {
        return bookingRepository.findByUserAndStatus(user, status);
    }
    public List<Booking> findBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }
    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }
    public Booking createBooking(Booking booking, Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }
        User user = userOpt.get();
        booking.setUser(user);
        Cab assignedCab = findBestAvailableCab(booking);
        if (assignedCab == null) {
            throw new RuntimeException("No available cabs for your preferred vehicle type. Please try again later or select a different vehicle type.");
        }
        booking.setCab(assignedCab);
        FareCalculationService.FareEstimate fareEstimate = fareCalculationService.calculateFareEstimate(
            assignedCab.getVehicleType(),
            booking.getDistance(),
            booking.getBookingDateTime(),
            booking.getPickupLatitude(),
            booking.getPickupLongitude()
        );
        booking.setBaseFare(fareEstimate.getBaseFare());
        booking.setSurgeMultiplier(fareEstimate.getSurgeMultiplier());
        booking.setSurgeAmount(fareEstimate.getSurgeAmount());
        booking.setTaxAmount(fareEstimate.getTaxAmount());
        booking.setTotalFare(fareEstimate.getTotalFare());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setDriverAssignedAt(LocalDateTime.now());
        Booking savedBooking = bookingRepository.save(booking);
        assignedCab.setAvailable(false);
        cabRepository.save(assignedCab);
        log.info("🎯 BOOKING CONFIRMED: #{} | Driver: {} ({}) | Cab: {} | Vehicle: {} | Fare: ₹{}",
            savedBooking.getId(),
            assignedCab.getDriver() != null ? assignedCab.getDriver().getName() : "N/A",
            assignedCab.getDriver() != null ? assignedCab.getDriver().getPhoneNumber() : "",
            assignedCab.getCabNumber(),
            assignedCab.getVehicleType(),
            savedBooking.getTotalFare());
        try {
            emailService.sendBookingConfirmation(savedBooking);
            notifyBookingCreated(savedBooking);
        } catch (Exception e) {
            log.warn("Failed to send booking notification: {}", e.getMessage());
        }
        try {
            rideTrackingService.startTracking(savedBooking.getId());
            log.info("🚗 Real-time tracking started for booking: {}", savedBooking.getId());
        } catch (Exception e) {
            log.warn("Failed to start tracking session: {}", e.getMessage());
        }
        log.info("Enhanced booking created: {} for vehicle type: {}", savedBooking.getId(), assignedCab.getVehicleType());
        return savedBooking;
    }
    private Cab findBestAvailableCab(Booking booking) {
        List<Cab> availableCabs;
        if (booking.getPreferredVehicleType() != null) {
            availableCabs = cabRepository.findByVehicleTypeAndAvailableTrue(booking.getPreferredVehicleType());
            log.info("🔍 Found {} available {} cabs", availableCabs.size(), booking.getPreferredVehicleType());
        } else {
            availableCabs = cabRepository.findByAvailableTrue();
            log.info("🔍 Found {} available cabs (all types)", availableCabs.size());
        }
        if (availableCabs.isEmpty()) {
            log.warn("❌ No available cabs found for booking request");
            return null;
        }
        Cab nearestCab = null;
        double minDistance = Double.MAX_VALUE;
        BigDecimal pickupLat = booking.getPickupLatitude();
        BigDecimal pickupLng = booking.getPickupLongitude();
        if (pickupLat == null || pickupLng == null) {
            log.warn("⚠️ No pickup location provided, assigning first available cab");
            return availableCabs.get(0);
        }
        for (Cab cab : availableCabs) {
            if (cab.getCurrentLatitude() == null || cab.getCurrentLongitude() == null) {
                continue;
            }
            double distance = calculateDistance(
                pickupLat.doubleValue(),
                pickupLng.doubleValue(),
                cab.getCurrentLatitude().doubleValue(),
                cab.getCurrentLongitude().doubleValue()
            );
            log.debug("📍 Cab {} ({}) is {:.2f} km away",
                cab.getCabNumber(), cab.getVehicleType(), distance);
            if (distance < minDistance) {
                minDistance = distance;
                nearestCab = cab;
            }
        }
        if (nearestCab != null) {
            log.info("✅ AUTO-ASSIGNED nearest cab: {} ({}) - {:.2f} km away (Driver: {})",
                nearestCab.getCabNumber(),
                nearestCab.getVehicleType(),
                minDistance,
                nearestCab.getDriver() != null ? nearestCab.getDriver().getName() : "N/A");
        } else {
            log.warn("⚠️ No cabs with location data, assigning first available cab");
            nearestCab = availableCabs.get(0);
        }
        return nearestCab;
    }
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
    public Booking createScheduledBooking(Booking booking, Long userId, LocalDateTime scheduledTime) {
        booking.setScheduledFor(scheduledTime);
        booking.setIsScheduled(true);
        booking.setStatus(BookingStatus.PENDING);
        booking.setCab(null);
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }
        booking.setUser(userOpt.get());
        if (booking.getPreferredVehicleType() != null) {
            FareCalculationService.FareEstimate fareEstimate = fareCalculationService.calculateFareEstimate(
                booking.getPreferredVehicleType(),
                booking.getDistance(),
                scheduledTime,
                booking.getPickupLatitude(),
                booking.getPickupLongitude()
            );
            booking.setBaseFare(fareEstimate.getBaseFare());
            booking.setTotalFare(fareEstimate.getTotalFare());
        }
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Scheduled booking created: {} for {}", savedBooking.getId(), scheduledTime);
        return savedBooking;
    }
    public List<FareCalculationService.FareEstimate> getFareEstimates(BigDecimal distance,
                                                                    LocalDateTime bookingTime,
                                                                    BigDecimal pickupLat,
                                                                    BigDecimal pickupLng) {
        List<FareCalculationService.FareEstimate> estimates = new ArrayList<>();
        for (VehicleType vehicleType : VehicleType.values()) {
            try {
                FareCalculationService.FareEstimate estimate = fareCalculationService.calculateFareEstimate(
                    vehicleType, distance, bookingTime, pickupLat, pickupLng
                );
                estimates.add(estimate);
            } catch (Exception e) {
                log.warn("Failed to calculate fare for {}: {}", vehicleType, e.getMessage());
            }
        }
        return estimates.stream()
            .sorted((a, b) -> a.getTotalFare().compareTo(b.getTotalFare()))
            .collect(java.util.stream.Collectors.toList());
    }
    private void notifyBookingCreated(Booking booking) {
        log.info("Sending booking notifications for booking: {}", booking.getId());
    }
    public Booking updateBookingStatus(Long bookingId, BookingStatus status) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setStatus(status);
            if (status == BookingStatus.COMPLETED || status == BookingStatus.CANCELLED) {
                Cab cab = booking.getCab();
                cab.setAvailable(true);
                cabRepository.save(cab);
                if (status == BookingStatus.CANCELLED) {
                    emailService.sendBookingCancellation(booking);
                }
            }
            log.info("Updated booking {} status to {}", bookingId, status);
            return bookingRepository.save(booking);
        }
        throw new RuntimeException("Booking not found");
    }
    public void cancelBooking(Long bookingId) {
        updateBookingStatus(bookingId, BookingStatus.CANCELLED);
    }
    public Booking updateBooking(Booking booking) {
        log.info("Updating booking {}", booking.getId());
        return bookingRepository.save(booking);
    }
    public void deleteBooking(Long id) {
        log.info("Deleting booking {}", id);
        bookingRepository.deleteById(id);
    }
    public boolean isCabAvailableForBooking(Long cabId, LocalDateTime bookingTime) {
        return bookingRepository.countActiveBookingsByCab(cabId) == 0;
    }
    public boolean hasActiveBooking(Long userId) {
        return bookingRepository.hasActiveBooking(userId);
    }
    public List<Booking> findActiveBookingsByUser(Long userId) {
        return bookingRepository.findActiveBookingsByUser(userId);
    }
    private BigDecimal[] geocodeAddress(String address) {
        try {
            String apiKey = "AIzaSyCjyzQGcwNLjqnE-aF0uaWaaJbIhLUZ9Ss";
            String encodedAddress = java.net.URLEncoder.encode(address, "UTF-8");
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + encodedAddress + "&key=" + apiKey;
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(response);
            if ("OK".equals(root.get("status").asText())) {
                com.fasterxml.jackson.databind.JsonNode location = root.get("results").get(0)
                    .get("geometry").get("location");
                BigDecimal lat = new BigDecimal(location.get("lat").asText());
                BigDecimal lng = new BigDecimal(location.get("lng").asText());
                return new BigDecimal[]{lat, lng};
            } else {
                throw new RuntimeException("Geocoding failed: " + root.get("status").asText());
            }
        } catch (Exception e) {
            log.error("Geocoding error for address '{}': {}", address, e.getMessage());
            throw new RuntimeException("Failed to geocode address: " + e.getMessage());
        }
    }
}
