package com.cabease.controllers;
import com.cabease.models.Booking;
import com.cabease.models.BookingStatus;
import com.cabease.services.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Optional;
@Controller
@RequestMapping("/api/bookings")
@Slf4j
public class EmailCancellationController {
    @Autowired
    private BookingService bookingService;
    @GetMapping("/{bookingId}/cancel")
    public String cancelBookingViaEmail(@PathVariable Long bookingId,
                                      @RequestParam String token,
                                      Model model) {
        try {
            if (!isValidCancellationToken(token, bookingId)) {
                model.addAttribute("error", "Invalid or expired cancellation link.");
                model.addAttribute("title", "Cancellation Failed");
                return "emails/cancellation-result";
            }
            Optional<Booking> bookingOpt = bookingService.findById(bookingId);
            if (!bookingOpt.isPresent()) {
                model.addAttribute("error", "Booking not found.");
                model.addAttribute("title", "Cancellation Failed");
                return "emails/cancellation-result";
            }
            Booking booking = bookingOpt.get();
            if (!booking.isCancellable()) {
                model.addAttribute("error", "This booking cannot be cancelled. Current status: " +
                                         booking.getStatus().getDisplayName());
                model.addAttribute("title", "Cancellation Not Allowed");
                model.addAttribute("booking", booking);
                return "emails/cancellation-result";
            }
            if (booking.getStatus() == BookingStatus.CANCELLED ||
                booking.getStatus() == BookingStatus.CANCELLED_BY_DRIVER) {
                model.addAttribute("error", "This booking has already been cancelled.");
                model.addAttribute("title", "Already Cancelled");
                model.addAttribute("booking", booking);
                return "emails/cancellation-result";
            }
            booking.setStatus(BookingStatus.CANCELLED);
            booking.setCancelledAt(LocalDateTime.now());
            booking.setCancellationReason("Cancelled via email link");
            bookingService.updateBooking(booking);
            model.addAttribute("success", "Your ride has been cancelled successfully.");
            model.addAttribute("title", "Cancellation Successful");
            model.addAttribute("booking", booking);
            model.addAttribute("message", String.format(
                "Your ride from %s to %s has been cancelled successfully. We hope to serve you again soon.",
                booking.getPickupLocation(), booking.getDropLocation()
            ));
            log.info("Booking {} cancelled via email link by user {}",
                    bookingId, booking.getUser().getEmail());
            return "emails/cancellation-result";
        } catch (Exception e) {
            log.error("Error cancelling booking {} via email", bookingId, e);
            model.addAttribute("error", "An error occurred while cancelling your booking. Please try again or contact support.");
            model.addAttribute("title", "Cancellation Failed");
            return "emails/cancellation-result";
        }
    }
    @PostMapping("/{bookingId}/cancel-api")
    @ResponseBody
    public ResponseEntity<?> cancelBookingApi(@PathVariable Long bookingId,
                                            @RequestParam String token) {
        try {
            if (!isValidCancellationToken(token, bookingId)) {
                return ResponseEntity.badRequest().body("Invalid or expired cancellation token");
            }
            Optional<Booking> bookingOpt = bookingService.findById(bookingId);
            if (!bookingOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            Booking booking = bookingOpt.get();
            if (!booking.isCancellable()) {
                return ResponseEntity.badRequest().body("Booking cannot be cancelled in current status: " +
                                                      booking.getStatus().getDisplayName());
            }
            booking.setStatus(BookingStatus.CANCELLED);
            booking.setCancelledAt(LocalDateTime.now());
            booking.setCancellationReason("Cancelled via email link");
            bookingService.updateBooking(booking);
            return ResponseEntity.ok().body("Booking cancelled successfully");
        } catch (Exception e) {
            log.error("Error cancelling booking {} via API", bookingId, e);
            return ResponseEntity.internalServerError().body("Error cancelling booking");
        }
    }
    private boolean isValidCancellationToken(String token, Long bookingId) {
        if (token == null || !token.contains("-")) {
            return false;
        }
        String[] parts = token.split("-");
        if (parts.length != 6) {
            return false;
        }
        try {
            Long tokenBookingId = Long.parseLong(parts[5]);
            return tokenBookingId.equals(bookingId);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
