package com.cabease.services;
import com.cabease.models.Booking;
import com.cabease.utils.DatabaseService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import com.cabease.services.email.EmailService;
public class BookingService {
    public void bookCab(Booking booking) {
        String sql = "INSERT INTO bookings (userId, cabId, source, destination, bookingTime, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, booking.getUser().getId());
            pstmt.setInt(2, booking.getCab().getId());
            pstmt.setString(3, booking.getSource());
            pstmt.setString(4, booking.getDestination());
            pstmt.setTimestamp(5, Timestamp.valueOf(booking.getBookingTime()));
            pstmt.setString(6, "BOOKED");
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int bookingId = rs.getInt(1);
                System.out.println("Booking ID: " + bookingId + " has been created.");
            }
            String updateCabSql = "UPDATE cabs SET isAvailable = false WHERE id = ?";
            try (PreparedStatement updateCabPstmt = conn.prepareStatement(updateCabSql)) {
                updateCabPstmt.setInt(1, booking.getCab().getId());
                updateCabPstmt.executeUpdate();
            }
            System.out.println("Cab booked for " + booking.getUser().getUsername());
            try {
                EmailService emailService = new EmailService();
                emailService.sendBookingConfirmation(booking);
            } catch (NoClassDefFoundError | UnsatisfiedLinkError e) {
                System.out.println("Email notifications are not available (Jakarta Mail dependency missing)");
            }
        } catch (SQLException e) {
            System.out.println("Error booking cab. Error: " + e.getMessage());
        }
    }
    public void cancelBooking(int bookingId) {
        String selectSql = "SELECT b.cabId, b.userId, b.source, b.destination, b.bookingTime, " +
                          "u.username, u.email, " +
                          "c.model, c.cabNumber " +
                          "FROM bookings b " +
                          "JOIN users u ON b.userId = u.id " +
                          "JOIN cabs c ON b.cabId = c.id " +
                          "WHERE b.id = ?";
        String updateSql = "UPDATE bookings SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseService.getConnection()) {
            Booking booking = null;
            int cabId = -1;
            try (PreparedStatement selectPstmt = conn.prepareStatement(selectSql)) {
                selectPstmt.setInt(1, bookingId);
                ResultSet rs = selectPstmt.executeQuery();
                if (rs.next()) {
                    cabId = rs.getInt("cabId");
                    com.cabease.models.User user = new com.cabease.models.User(
                        rs.getString("username"),
                        "",
                        rs.getString("email")
                    );
                    com.cabease.models.Cab cab = new com.cabease.models.Cab(
                        rs.getString("cabNumber"),
                        rs.getString("model"),
                        true,
                        "",
                        ""
                    );
                    booking = new Booking(
                        user,
                        cab,
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getTimestamp("bookingTime").toLocalDateTime()
                    );
                } else {
                    System.out.println("No booking found with ID: " + bookingId);
                    return;
                }
            }
            if (cabId != -1) {
                try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                    updatePstmt.setString(1, "CANCELLED");
                    updatePstmt.setInt(2, bookingId);
                    updatePstmt.executeUpdate();
                }
                String updateCabSql = "UPDATE cabs SET isAvailable = true WHERE id = ?";
                try (PreparedStatement updateCabPstmt = conn.prepareStatement(updateCabSql)) {
                    updateCabPstmt.setInt(1, cabId);
                    updateCabPstmt.executeUpdate();
                }
                System.out.println("Booking " + bookingId + " cancelled.");
                try {
                    EmailService emailService = new EmailService();
                    emailService.sendCancellationConfirmation(booking);
                } catch (NoClassDefFoundError | UnsatisfiedLinkError e) {
                    System.out.println("Email notifications are not available (Jakarta Mail dependency missing)");
                }
            } else {
                System.out.println("Booking not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error cancelling booking. Error: " + e.getMessage());
        }
    }
}
