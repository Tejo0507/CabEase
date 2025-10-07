package service;

import model.Booking;
import model.Cab;
import model.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BookingService {
    public Booking createBooking(User user, Cab cab, String pickup, String drop, double distance) {
        double fare = FareCalculator.calculateFare(cab, distance);
        Booking booking = new Booking(user, cab, pickup, drop, distance, fare);

        try {
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            FileWriter writer = new FileWriter("data/bookings.txt", true);
            writer.write(booking.toString() + "\n--------------------\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return booking;
    }
}
