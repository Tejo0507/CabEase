package com.cabease.controllers;
import com.cabease.models.Booking;
import com.cabease.models.BookingStatus;
import com.cabease.models.Cab;
import com.cabease.models.User;
import com.cabease.services.BookingService;
import com.cabease.services.CabService;
import com.cabease.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
@Controller
@RequestMapping("/bookings")
@Slf4j
public class BookingController {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private CabService cabService;
    @Autowired
    private LocationService locationService;
    @GetMapping
    public String listBookings(@AuthenticationPrincipal User user, Model model) {
        List<Booking> bookings = bookingService.findBookingsByUser(user);
        model.addAttribute("bookings", bookings);
        return "bookings/list";
    }
    @GetMapping("/new")
    public String newBookingForm(Model model) {
        return "bookings/enhanced-new";
    }
    @GetMapping("/new/legacy")
    public String legacyBookingForm(Model model) {
        Booking booking = new Booking();
        booking.setCab(new Cab());
        model.addAttribute("booking", booking);
        List<Cab> availableCabs = cabService.findAvailableCabs();
        model.addAttribute("availableCabs", availableCabs);
        return "bookings/new";
    }
    @PostMapping
    public String createBooking(@Valid @ModelAttribute Booking booking, BindingResult result,
                                @AuthenticationPrincipal User user, Model model, RedirectAttributes redirectAttributes) {
        List<Cab> availableCabs = cabService.findAvailableCabs();
        if (result.hasErrors()) {
            model.addAttribute("availableCabs", availableCabs);
            return "bookings/new";
        }
        try {
            LocationService.Coordinates pickupCoords = locationService.getCoordinates(booking.getPickupLocation());
            LocationService.Coordinates dropCoords = locationService.getCoordinates(booking.getDropLocation());
            if (pickupCoords == null || dropCoords == null) {
                model.addAttribute("availableCabs", availableCabs);
                model.addAttribute("error", "Invalid pickup or drop location selected");
                return "bookings/new";
            }
            booking.setPickupLatitude(pickupCoords.getLatitude());
            booking.setPickupLongitude(pickupCoords.getLongitude());
            booking.setDropLatitude(dropCoords.getLatitude());
            booking.setDropLongitude(dropCoords.getLongitude());
            log.info("📍 Converted locations to coordinates: Pickup({}, {}) = {},{} | Drop({}, {}) = {},{}",
                booking.getPickupLocation(), booking.getDropLocation(),
                pickupCoords.getLatitude(), pickupCoords.getLongitude(),
                booking.getPickupLocation(), booking.getDropLocation(),
                dropCoords.getLatitude(), dropCoords.getLongitude());
            bookingService.createBooking(booking, user.getId());
            redirectAttributes.addFlashAttribute("success", "Booking created successfully");
            return "redirect:/bookings";
        } catch (Exception e) {
            log.error("❌ Booking creation failed: {}", e.getMessage(), e);
            model.addAttribute("availableCabs", availableCabs);
            model.addAttribute("error", e.getMessage());
            return "bookings/new";
        }
    }
    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(id);
            redirectAttributes.addFlashAttribute("success", "Booking cancelled successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/bookings";
    }
    @GetMapping("/{id}")
    public String viewBooking(@PathVariable Long id, Model model) {
        Booking booking = bookingService.findById(id).orElseThrow(() -> new RuntimeException("Booking not found"));
        model.addAttribute("booking", booking);
        return "bookings/view";
    }
}
