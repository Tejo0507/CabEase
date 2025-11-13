package com.cabease.controllers.api;
import com.cabease.models.*;
import com.cabease.services.BookingService;
import com.cabease.services.FareCalculationService;
import com.cabease.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@RestController
@RequestMapping("/api/v1/booking")
@CrossOrigin(origins = "*")
@Slf4j
public class BookingApiController {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private FareCalculationService fareCalculationService;
    @Autowired
    private UserService userService;
    @PostMapping("/create")
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            java.security.Principal principal,
            javax.servlet.http.HttpServletRequest httpRequest) {
        log.debug("Incoming booking create request: {}", request);
        log.debug("Security principal: {}", principal == null ? "<none>" : principal.getName());
        try {
            javax.servlet.http.HttpSession session = httpRequest.getSession(false);
            log.debug("HTTP session id: {}", session == null ? "<no-session>" : session.getId());
        } catch (Exception e) {
            log.debug("Could not read session: {}", e.getMessage());
        }
        log.debug("Request Header - Cookie: {}", httpRequest.getHeader("Cookie"));
        log.debug("Request Header - X-Requested-With: {}", httpRequest.getHeader("X-Requested-With"));
        log.debug("Request Header - Referer: {}", httpRequest.getHeader("Referer"));
        try {
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            log.debug("SecurityContext Authentication: {}", auth == null ? "<none>" : auth.getName());
        } catch (Exception e) {
            log.debug("Could not read SecurityContext: {}", e.getMessage());
        }
        User user = null;
        if (principal != null) {
            user = userService.findByEmail(principal.getName()).orElse(null);
        }
        if (user == null) {
            log.warn("Booking attempt while not authenticated");
            return ResponseEntity.status(401)
                .body(BookingResponse.builder()
                    .error("Authentication required. Please login to create a booking.")
                    .build());
        }
        try {
            Booking booking = buildBookingFromRequest(request);
            Booking savedBooking = bookingService.createBooking(booking, user.getId());
            String trackingUrl = "/ride-tracking?rideId=" + savedBooking.getId();
            BookingResponse response = BookingResponse.builder()
                .bookingId(savedBooking.getId())
                .status(savedBooking.getStatus())
                .statusMessage(savedBooking.getStatus().getDisplayName())
                .estimatedArrivalTime(savedBooking.getEstimatedArrivalTime())
                .fareBreakdown(buildFareBreakdown(savedBooking))
                .driverInfo(buildDriverInfo(savedBooking.getCab()))
                .vehicleInfo(buildVehicleInfo(savedBooking.getCab()))
                .trackingEnabled(savedBooking.getStatus().isTrackable())
                .trackingUrl(trackingUrl)
                .build();
            log.info("Booking created successfully: {} - Tracking URL: {}", savedBooking.getId(), trackingUrl);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error creating booking: ", e);
            String errorMessage = e.getMessage();
            boolean isActiveBookingError = errorMessage != null &&
                (errorMessage.contains("ongoing ride") || errorMessage.contains("active booking"));
            return ResponseEntity.badRequest()
                .body(BookingResponse.builder()
                    .error(errorMessage)
                    .hasActiveBooking(isActiveBookingError)
                    .build());
        } catch (Exception e) {
            log.error("Error creating booking: ", e);
            return ResponseEntity.badRequest()
                .body(BookingResponse.builder()
                    .error("Failed to create booking. Please try again.")
                    .build());
        }
    }
    @PostMapping("/schedule")
    public ResponseEntity<BookingResponse> scheduleBooking(
            @Valid @RequestBody ScheduledBookingRequest request,
            java.security.Principal principal) {
        User user = null;
        if (principal != null) {
            user = userService.findByEmail(principal.getName()).orElse(null);
        }
        if (user == null) {
            log.warn("Schedule booking attempt while not authenticated");
            return ResponseEntity.status(401)
                .body(BookingResponse.builder()
                    .error("Authentication required. Please login to schedule a booking.")
                    .build());
        }
        try {
            Booking booking = buildBookingFromRequest(request);
            Booking savedBooking = bookingService.createScheduledBooking(
                booking, user.getId(), request.getScheduledTime());
            BookingResponse response = BookingResponse.builder()
                .bookingId(savedBooking.getId())
                .status(savedBooking.getStatus())
                .statusMessage("Ride scheduled for " + request.getScheduledTime())
                .scheduledTime(request.getScheduledTime())
                .fareBreakdown(buildFareBreakdown(savedBooking))
                .isScheduled(true)
                .build();
            log.info("Scheduled booking created: {} for {}", savedBooking.getId(), request.getScheduledTime());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating scheduled booking: ", e);
            return ResponseEntity.badRequest()
                .body(BookingResponse.builder()
                    .error(e.getMessage())
                    .build());
        }
    }
    @PostMapping("/estimate")
    public ResponseEntity<List<FareCalculationService.FareEstimate>> getFareEstimates(
            @Valid @RequestBody FareEstimateRequest request) {
        try {
            List<FareCalculationService.FareEstimate> estimates =
                bookingService.getFareEstimates(
                    request.getDistance(),
                    request.getBookingTime(),
                    request.getPickupLatitude(),
                    request.getPickupLongitude()
                );
            return ResponseEntity.ok(estimates);
        } catch (Exception e) {
            log.error("Error calculating fare estimates: ", e);
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<CancellationResponse> cancelBooking(
            @PathVariable Long bookingId,
            @RequestBody CancellationRequest request,
            java.security.Principal principal) {
        User user = null;
        if (principal != null) {
            user = userService.findByEmail(principal.getName()).orElse(null);
        }
        if (user == null) {
            log.warn("Cancel booking attempt while not authenticated");
            return ResponseEntity.status(401)
                .body(CancellationResponse.builder()
                    .bookingId(bookingId)
                    .cancelled(false)
                    .error("Authentication required. Please login to cancel a booking.")
                    .build());
        }
        try {
            Booking booking = bookingService.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
            if (!booking.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized access to booking");
            }
            if (!booking.isCancellable()) {
                throw new RuntimeException("Booking cannot be cancelled at this stage");
            }
            bookingService.cancelBooking(bookingId);
            CancellationResponse response = CancellationResponse.builder()
                .bookingId(bookingId)
                .cancelled(true)
                .cancellationReason(request.getReason())
                .refundAmount(calculateRefundAmount(booking))
                .message("Booking cancelled successfully")
                .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error cancelling booking {}: ", bookingId, e);
            return ResponseEntity.badRequest()
                .body(CancellationResponse.builder()
                    .bookingId(bookingId)
                    .cancelled(false)
                    .error(e.getMessage())
                    .build());
        }
    }
    @GetMapping("/active-check")
    public ResponseEntity<ActiveBookingCheckResponse> checkActiveBooking(java.security.Principal principal) {
        User user = null;
        if (principal != null) {
            user = userService.findByEmail(principal.getName()).orElse(null);
        }
        if (user == null) {
            return ResponseEntity.ok(ActiveBookingCheckResponse.builder()
                .hasActiveBooking(false)
                .build());
        }
        try {
            boolean hasActive = bookingService.hasActiveBooking(user.getId());
            List<Booking> activeBookings = null;
            if (hasActive) {
                activeBookings = bookingService.findActiveBookingsByUser(user.getId());
            }
            return ResponseEntity.ok(ActiveBookingCheckResponse.builder()
                .hasActiveBooking(hasActive)
                .activeBookings(activeBookings)
                .build());
        } catch (Exception e) {
            log.error("Error checking active booking: ", e);
            return ResponseEntity.ok(ActiveBookingCheckResponse.builder()
                .hasActiveBooking(false)
                .build());
        }
    }
    @GetMapping("/{bookingId}/status")
    public ResponseEntity<BookingStatusResponse> getBookingStatus(@PathVariable Long bookingId,
                                                                java.security.Principal principal) {
        User user = null;
        if (principal != null) {
            user = userService.findByEmail(principal.getName()).orElse(null);
        }
        if (user == null) {
            log.warn("Get booking status attempt while not authenticated");
            return ResponseEntity.status(401).build();
        }
        try {
            Booking booking = bookingService.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
            BookingStatusResponse response = BookingStatusResponse.builder()
                .bookingId(bookingId)
                .status(booking.getStatus())
                .statusMessage(booking.getStatus().getDisplayName())
                .isTrackable(booking.getStatus().isTrackable())
                .tripDuration(booking.getTripDurationMinutes())
                .driverInfo(booking.getCab() != null ? buildDriverInfo(booking.getCab()) : null)
                .vehicleInfo(booking.getCab() != null ? buildVehicleInfo(booking.getCab()) : null)
                .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting booking status for {}: ", bookingId, e);
            return ResponseEntity.badRequest().build();
        }
    }
    private Booking buildBookingFromRequest(BookingRequest request) {
        return Booking.builder()
            .pickupLocation(request.getPickupLocation())
            .dropLocation(request.getDropLocation())
            .pickupLatitude(request.getPickupLatitude())
            .pickupLongitude(request.getPickupLongitude())
            .dropLatitude(request.getDropLatitude())
            .dropLongitude(request.getDropLongitude())
            .distance(request.getDistance())
            .bookingDateTime(LocalDateTime.now())
            .preferredVehicleType(request.getVehicleType())
            .femaleDriverPreferred(request.isFemaleDriverPreferred())
            .paymentMethod(request.getPaymentMethod())
            .specialInstructions(request.getSpecialInstructions())
            .passengerCount(request.getPassengerCount())
            .build();
    }
    private FareBreakdown buildFareBreakdown(Booking booking) {
        return FareBreakdown.builder()
            .baseFare(booking.getBaseFare())
            .surgeMultiplier(booking.getSurgeMultiplier())
            .surgeAmount(booking.getSurgeAmount())
            .taxAmount(booking.getTaxAmount())
            .discountAmount(booking.getDiscountAmount())
            .totalFare(booking.getTotalFare())
            .build();
    }
    private DriverInfo buildDriverInfo(Cab cab) {
        if (cab == null || cab.getDriver() == null) {
            return null;
        }
        Driver driver = cab.getDriver();
        return DriverInfo.builder()
            .driverId(driver.getId())
            .name(driver.getName())
            .phoneNumber(driver.getPhoneNumber())
            .rating(4.5)
            .isVerified(driver.getAvailable())
            .build();
    }
    private VehicleInfo buildVehicleInfo(Cab cab) {
        if (cab == null) {
            return null;
        }
        return VehicleInfo.builder()
            .cabId(cab.getId())
            .cabNumber(cab.getCabNumber())
            .model(cab.getModel())
            .brand(cab.getBrand())
            .vehicleType(cab.getVehicleType())
            .color(cab.getColor())
            .acAvailable(cab.getAcAvailable())
            .build();
    }
    private BigDecimal calculateRefundAmount(Booking booking) {
        return booking.getTotalFare();
    }
    @lombok.Data
    public static class BookingRequest {
        private String pickupLocation;
        private String dropLocation;
        private BigDecimal pickupLatitude;
        private BigDecimal pickupLongitude;
        private BigDecimal dropLatitude;
        private BigDecimal dropLongitude;
        private BigDecimal distance;
        private VehicleType vehicleType;
        private boolean femaleDriverPreferred;
        private PaymentMethod paymentMethod;
        private String specialInstructions;
        private Integer passengerCount;
    }
    @lombok.Data
    @lombok.EqualsAndHashCode(callSuper = true)
    public static class ScheduledBookingRequest extends BookingRequest {
        private LocalDateTime scheduledTime;
    }
    @lombok.Data
    public static class FareEstimateRequest {
        private BigDecimal pickupLatitude;
        private BigDecimal pickupLongitude;
        private BigDecimal dropLatitude;
        private BigDecimal dropLongitude;
        private BigDecimal distance;
        private LocalDateTime bookingTime;
    }
    @lombok.Data
    public static class CancellationRequest {
        private String reason;
    }
    @lombok.Builder
    @lombok.Data
    public static class BookingResponse {
        private Long bookingId;
        private BookingStatus status;
        private String statusMessage;
        private Integer estimatedArrivalTime;
        private LocalDateTime scheduledTime;
        private FareBreakdown fareBreakdown;
        private DriverInfo driverInfo;
        private VehicleInfo vehicleInfo;
        private boolean trackingEnabled;
        private boolean isScheduled;
        private String trackingUrl;
        private boolean hasActiveBooking;
        private String error;
    }
    @lombok.Builder
    @lombok.Data
    public static class BookingStatusResponse {
        private Long bookingId;
        private BookingStatus status;
        private String statusMessage;
        private boolean isTrackable;
        private Long tripDuration;
        private DriverInfo driverInfo;
        private VehicleInfo vehicleInfo;
    }
    @lombok.Builder
    @lombok.Data
    public static class CancellationResponse {
        private Long bookingId;
        private boolean cancelled;
        private String cancellationReason;
        private BigDecimal refundAmount;
        private String message;
        private String error;
    }
    @lombok.Builder
    @lombok.Data
    public static class FareBreakdown {
        private BigDecimal baseFare;
        private BigDecimal surgeMultiplier;
        private BigDecimal surgeAmount;
        private BigDecimal taxAmount;
        private BigDecimal discountAmount;
        private BigDecimal totalFare;
    }
    @lombok.Builder
    @lombok.Data
    public static class DriverInfo {
        private Long driverId;
        private String name;
        private String phoneNumber;
        private Double rating;
        private boolean isVerified;
    }
    @lombok.Builder
    @lombok.Data
    public static class VehicleInfo {
        private Long cabId;
        private String cabNumber;
        private String model;
        private String brand;
        private VehicleType vehicleType;
        private String color;
        private Boolean acAvailable;
    }
    @lombok.Builder
    @lombok.Data
    public static class ActiveBookingCheckResponse {
        private boolean hasActiveBooking;
        private List<Booking> activeBookings;
    }
}
