package com.cabease.service;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
@Service
public class LocationService {
    private static final Map<String, Coordinates> LOCATION_MAP = new HashMap<>();
    static {
        LOCATION_MAP.put("Adambakkam", new Coordinates(13.0067, 80.2042));
        LOCATION_MAP.put("Adyar", new Coordinates(13.0067, 80.2570));
        LOCATION_MAP.put("Alandur", new Coordinates(13.0025, 80.2060));
        LOCATION_MAP.put("Alwarpet", new Coordinates(13.0339, 80.2502));
        LOCATION_MAP.put("Ambattur", new Coordinates(13.1143, 80.1619));
        LOCATION_MAP.put("Aminjikarai", new Coordinates(13.0732, 80.2213));
        LOCATION_MAP.put("Anna Nagar", new Coordinates(13.0850, 80.2101));
        LOCATION_MAP.put("Arumbakkam", new Coordinates(13.0799, 80.2072));
        LOCATION_MAP.put("Ashok Nagar", new Coordinates(13.0358, 80.2098));
        LOCATION_MAP.put("Avadi", new Coordinates(13.1147, 80.1018));
        LOCATION_MAP.put("Besant Nagar", new Coordinates(13.0015, 80.2669));
        LOCATION_MAP.put("Chepauk", new Coordinates(13.0653, 80.2784));
        LOCATION_MAP.put("Chetput", new Coordinates(13.0732, 80.2437));
        LOCATION_MAP.put("Chrompet", new Coordinates(12.9517, 80.1414));
        LOCATION_MAP.put("Egmore", new Coordinates(13.0732, 80.2609));
        LOCATION_MAP.put("Guindy", new Coordinates(13.0067, 80.2206));
        LOCATION_MAP.put("IIT Madras", new Coordinates(12.9916, 80.2336));
        LOCATION_MAP.put("K.K. Nagar", new Coordinates(13.0382, 80.2052));
        LOCATION_MAP.put("Kilpauk", new Coordinates(13.0789, 80.2437));
        LOCATION_MAP.put("Kodambakkam", new Coordinates(13.0525, 80.2252));
        LOCATION_MAP.put("Koyambedu", new Coordinates(13.0732, 80.1948));
        LOCATION_MAP.put("Madipakkam", new Coordinates(12.9625, 80.1989));
        LOCATION_MAP.put("Maduravoyal", new Coordinates(13.0667, 80.1633));
        LOCATION_MAP.put("Medavakkam", new Coordinates(12.9207, 80.1921));
        LOCATION_MAP.put("Mylapore", new Coordinates(13.0339, 80.2671));
        LOCATION_MAP.put("Nandanam", new Coordinates(13.0323, 80.2433));
        LOCATION_MAP.put("Nungambakkam", new Coordinates(13.0569, 80.2424));
        LOCATION_MAP.put("Pallavaram", new Coordinates(12.9675, 80.1491));
        LOCATION_MAP.put("Porur", new Coordinates(13.0358, 80.1561));
        LOCATION_MAP.put("Saidapet", new Coordinates(13.0210, 80.2231));
        LOCATION_MAP.put("T Nagar", new Coordinates(13.0418, 80.2341));
        LOCATION_MAP.put("Tambaram", new Coordinates(12.9249, 80.1000));
        LOCATION_MAP.put("Teynampet", new Coordinates(13.0358, 80.2482));
        LOCATION_MAP.put("Thiruvanmiyur", new Coordinates(12.9833, 80.2583));
        LOCATION_MAP.put("Thoraipakkam", new Coordinates(12.9395, 80.2337));
        LOCATION_MAP.put("Velachery", new Coordinates(12.9756, 80.2169));
        LOCATION_MAP.put("Villivakkam", new Coordinates(13.1047, 80.2090));
        LOCATION_MAP.put("Virugambakkam", new Coordinates(13.0525, 80.2072));
        LOCATION_MAP.put("Vyasarpadi", new Coordinates(13.1047, 80.2639));
    }
    public Coordinates getCoordinates(String locationName) {
        return LOCATION_MAP.get(locationName);
    }
    public static class Coordinates {
        private final BigDecimal latitude;
        private final BigDecimal longitude;
        public Coordinates(double lat, double lng) {
            this.latitude = BigDecimal.valueOf(lat);
            this.longitude = BigDecimal.valueOf(lng);
        }
        public BigDecimal getLatitude() {
            return latitude;
        }
        public BigDecimal getLongitude() {
            return longitude;
        }
    }
}
