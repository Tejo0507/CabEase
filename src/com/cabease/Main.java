package com.cabease;
import com.cabease.models.User;
import com.cabease.models.Cab;
import com.cabease.models.Booking;
import com.cabease.services.BookingService;
import com.cabease.utils.DatabaseService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to CabEase!");
        DatabaseService.initializeDatabase();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1. Register a new user");
            System.out.println("2. View all registered users");
            System.out.println("3. Book a cab");
            System.out.println("4. Cancel a booking");
            System.out.println("5. Search for available cabs");
            System.out.println("6. Exit");
            String choice = reader.readLine();
            if (choice == null) {
                System.out.println("Invalid input. Please try again.");
                continue;
            }
            switch (choice) {
                case "1":
                    registerUser(reader);
                    break;
                case "2":
                    viewAllUsers();
                    break;
                case "3":
                    bookCab(reader);
                    break;
                case "4":
                    cancelBooking(reader);
                    break;
                case "5":
                    searchCabs(reader);
                    break;
                case "6":
                    System.out.println("Thank you for using CabEase!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    private static void registerUser(BufferedReader reader) throws IOException {
        System.out.print("Enter your username: ");
        String username = reader.readLine();
        System.out.print("Enter your password: ");
        String password = reader.readLine();
        System.out.print("Enter your email: ");
        String email = reader.readLine();
        User user = new User(username, password, email);
        DatabaseService dbService = new DatabaseService();
        dbService.saveUser(user);
    }
    private static void viewAllUsers() {
        DatabaseService dbService = new DatabaseService();
        java.util.List<User> users = dbService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No registered users found.");
            return;
        }
        System.out.println("\n--- Registered Users ---");
        for (User user : users) {
            System.out.println("Username: " + user.getUsername() + ", Email: " + user.getEmail());
        }
        System.out.println("----------------------");
    }
    private static void bookCab(BufferedReader reader) throws IOException {
        System.out.print("Enter your username: ");
        String username = reader.readLine();
        DatabaseService dbService = new DatabaseService();
        User user = dbService.getUserByUsername(username);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        System.out.println();
        searchCabs(reader);
        int cabId = -1;
        while (cabId == -1) {
            System.out.print("\nEnter the ID of the cab you want to book: ");
            try {
                String cabIdStr = reader.readLine();
                if (cabIdStr == null || cabIdStr.trim().isEmpty()) {
                    System.out.println("Invalid input. Please enter a number.");
                    continue;
                }
                cabId = Integer.parseInt(cabIdStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid cab ID. Please enter a number.");
            }
        }
        Cab cab = dbService.getCabById(cabId);
        if (cab == null || !cab.isAvailable()) {
            System.out.println("Cab not found or is not available.");
            return;
        }
        System.out.print("Enter source: ");
        String source = reader.readLine();
        System.out.print("Enter destination: ");
        String destination = reader.readLine();
        System.out.print("Enter booking time (yyyy-MM-dd HH:mm): ");
        LocalDateTime bookingTime = null;
        while (bookingTime == null) {
            try {
                String bookingTimeStr = reader.readLine();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime parsedTime = LocalDateTime.parse(bookingTimeStr, formatter);
                if (parsedTime.getYear() < LocalDateTime.now().getYear() ||
                    parsedTime.getYear() > LocalDateTime.now().getYear() + 10) {
                    System.out.println("Please enter a valid year between " +
                                    LocalDateTime.now().getYear() + " and " +
                                    (LocalDateTime.now().getYear() + 10));
                    continue;
                }
                if (parsedTime.isBefore(LocalDateTime.now())) {
                    System.out.println("Booking time must be in the future. Please try again.");
                    continue;
                }
                bookingTime = parsedTime;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd HH:mm format (e.g., 2025-10-15 13:44)");
            }
        }
        Booking booking = new Booking(user, cab, source, destination, bookingTime);
        BookingService bookingService = new BookingService();
        bookingService.bookCab(booking);
    }
    private static void cancelBooking(BufferedReader reader) throws IOException {
        System.out.print("Enter booking ID to cancel: ");
        try {
            String input = reader.readLine();
            if (input == null || input.trim().isEmpty()) {
                System.out.println("Invalid input. Please enter a booking ID number.");
                return;
            }
            int bookingId = Integer.parseInt(input.trim());
            BookingService bookingService = new BookingService();
            bookingService.cancelBooking(bookingId);
        } catch (NumberFormatException e) {
            System.out.println("Invalid booking ID. Please enter a valid number.");
        }
    }
    private static void searchCabs(BufferedReader reader) throws IOException {
        System.out.print("Enter cab type (Sedan, SUV, Hatchback) or leave blank for all: ");
        String type = reader.readLine();
        DatabaseService dbService = new DatabaseService();
        java.util.List<Cab> cabs = dbService.getAvailableCabs(type);
        if (cabs.isEmpty()) {
            System.out.println("No available cabs found.");
            return;
        }
            System.out.println("\n--- Available Cabs ---");
            for (Cab cab : cabs) {
                System.out.println("ID: " + cab.getId() + ", Cab Number: " + cab.getCabNumber() +
                                ", Model: " + cab.getModel() + ", Driver: " + cab.getDriverName() +
                                ", Contact: " + cab.getDriverContact() + ", Available: " + cab.isAvailable());
            }
            System.out.println("----------------------");
    }
}
