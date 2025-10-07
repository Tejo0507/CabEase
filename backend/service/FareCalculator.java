package service;

import model.Cab;

public class FareCalculator {
    public static double calculateFare(Cab cab, double distance) {
        return cab.getRatePerKm() * distance;
    }
}
