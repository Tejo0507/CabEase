import model.Booking;
import model.Cab;
import model.User;
import service.BookingService;
import service.EmailService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter your email: ");
        String email = scanner.nextLine();

        System.out.print("Enter your phone number: ");
        String phone = scanner.nextLine();

        System.out.print("Enter pickup location: ");
        String pickup = scanner.nextLine();

        System.out.print("Enter drop location: ");
        String drop = scanner.nextLine();

        System.out.print("Enter distance in km: ");
        double distance = scanner.nextDouble();
        scanner.nextLine(); 

        System.out.print("Choose cab type (Mini, Sedan, SUV): ");
        String cabType = scanner.nextLine();

        User user = new User(name, email, phone);

        double ratePerKm;
        switch (cabType.toLowerCase()) {
            case "sedan":
                ratePerKm = 1.2;
                break;
            case "suv":
                ratePerKm = 1.8;
                break;
            default:
                ratePerKm = 1.0;
                cabType = "Mini";
                break;
        }
        Cab cab = new Cab(cabType, ratePerKm);

        BookingService bookingService = new BookingService();
        Booking booking = bookingService.createBooking(user, cab, pickup, drop, distance);

        System.out.println("\n--- Booking Details ---");
        System.out.println(booking);

        // Replace with your actual email and password
        EmailService emailService = new EmailService("your-email@gmail.com", "your-password");
        String emailSubject = "Cab Booking Confirmation";
        String emailMessage = "Dear " + user.getName() + ",\n\nYour cab booking is confirmed.\n\n" + booking.toString();

        emailService.sendBookingEmail(user.getEmail(), emailSubject, emailMessage);
        System.out.println("\nBooking confirmation email sent successfully.");

        scanner.close();
    }
}
