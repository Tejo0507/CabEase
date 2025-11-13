package com.cabease.models;
import java.time.LocalDateTime;
public class Booking {
    private User user;
    private Cab cab;
    private String source;
    private String destination;
    private LocalDateTime bookingTime;
    public Booking(User user, Cab cab, String source, String destination, LocalDateTime bookingTime) {
        this.user = user;
        this.cab = cab;
        this.source = source;
        this.destination = destination;
        this.bookingTime = bookingTime;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Cab getCab() {
        return cab;
    }
    public void setCab(Cab cab) {
        this.cab = cab;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }
    public LocalDateTime getBookingTime() {
        return bookingTime;
    }
    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }
}
