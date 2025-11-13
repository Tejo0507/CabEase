package com.cabease.controllers;
import com.cabease.models.Booking;
import com.cabease.models.User;
import com.cabease.services.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;
@RestController
@RequestMapping("/api/bookings")
@Slf4j
public class BookingRestController {
    @Autowired
    private BookingService bookingService;
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Map<String, Object> bookingData,
                                          @AuthenticationPrincipal User user) {
        try {
            log.info("📥 Received booking data: {}", bookingData);
            Booking booking = new Booking();
            booking.setPickupLocation((String) bookingData.get("pickupLocation"));
            booking.setDropLocation((String) bookingData.get("dropLocation"));
            Object pickupLat = bookingData.get("pickupLatitude");
            Object pickupLng = bookingData.get("pickupLongitude");
            Object dropLat = bookingData.get("dropLatitude");
            Object dropLng = bookingData.get("dropLongitude");
            booking.setPickupLatitude(new BigDecimal(pickupLat.toString()));
            booking.setPickupLongitude(new BigDecimal(pickupLng.toString()));
            booking.setDropLatitude(new BigDecimal(dropLat.toString()));
            booking.setDropLongitude(new BigDecimal(dropLng.toString()));
            log.info("✅ Parsed coordinates: Pickup({},{}) Drop({},{})",
                booking.getPickupLatitude(), booking.getPickupLongitude(),
                booking.getDropLatitude(), booking.getDropLongitude());
            Booking createdBooking = bookingService.createBooking(booking, user.getId());
            log.info("🎉 Booking created successfully with ID: {}", createdBooking.getId());
            return ResponseEntity.ok(createdBooking);
        } catch (Exception e) {
            log.error("❌ Booking creation failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
