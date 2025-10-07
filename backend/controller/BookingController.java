package controller;

import model.Booking;
import model.Cab;
import model.User;
import org.springframework.web.bind.annotation.*;
import service.BookingService;
import service.EmailService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;
    private final EmailService emailService;

    public BookingController() {
        this.bookingService = new BookingService();
        // Replace with your actual email and password or use a properties file
        this.emailService = new EmailService("your-email@gmail.com", "your-password");
    }

    @PostMapping("/book")
    public Map<String, Object> bookCab(@RequestBody BookingRequest bookingRequest) {
        User user = new User(bookingRequest.getName(), bookingRequest.getEmail(), bookingRequest.getPhone());

        double ratePerKm = getRateForCabType(bookingRequest.getCabType());
        Cab cab = new Cab(bookingRequest.getCabType(), ratePerKm);

        Booking booking = bookingService.createBooking(user, cab, bookingRequest.getPickup(), bookingRequest.getDrop(), bookingRequest.getDistance());

        String emailSubject = "Cab Booking Confirmation";
        String emailMessage = "Dear " + user.getName() + ",\n\nYour cab has been booked successfully.\n\n" + booking.toString();
        emailService.sendBookingEmail(user.getEmail(), emailSubject, emailMessage);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Cab booked successfully");
        response.put("bookingDetails", booking);

        return response;
    }

    private double getRateForCabType(String cabType) {
        switch (cabType.toLowerCase()) {
            case "premium":
                return 1.5;
            case "suv":
                return 2.0;
            default:
                return 1.0;
        }
    }

    static class BookingRequest {
        private String name;
        private String email;
        private String phone;
        private String pickup;
        private String drop;
        private String cabType;
        private double distance;

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getPickup() {
            return pickup;
        }

        public String getDrop() {
            return drop;
        }

        public String getCabType() {
            return cabType;
        }

        public double getDistance() {
            return distance;
        }
    }
}
