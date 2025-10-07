package model;

import java.time.LocalDateTime;

public class Booking {
    private User user;
    private Cab cab;
    private String pickupLocation;
    private String dropLocation;
    private double distance;
    private double fare;
    private LocalDateTime bookingTime;

    public Booking(User user, Cab cab, String pickupLocation, String dropLocation, double distance, double fare) {
        this.user = user;
        this.cab = cab;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.distance = distance;
        this.fare = fare;
        this.bookingTime = LocalDateTime.now();
    }

    public User getUser() {
        return user;
    }

    public Cab getCab() {
        return cab;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public double getDistance() {
        return distance;
    }

    public double getFare() {
        return fare;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    @Override
    public String toString() {
        return "Booking Details:\n" +
                "User: " + user.getName() + "\n" +
                "Cab Type: " + cab.getCabType() + "\n" +
                "Pickup: " + pickupLocation + "\n" +
                "Drop: " + dropLocation + "\n" +
                "Distance: " + distance + " km\n" +
                "Fare: $" + fare + "\n" +
                "Booking Time: " + bookingTime;
    }
}
