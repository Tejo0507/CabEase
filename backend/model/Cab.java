package model;

public class Cab {
    private int id;
    private String cabType;
    private String driverName;
    private String cabNumber;
    private double pricePerKm;

    public Cab(int id, String cabType, String driverName, String cabNumber, double pricePerKm) {
        this.id = id;
        this.cabType = cabType;
        this.driverName = driverName;
        this.cabNumber = cabNumber;
        this.pricePerKm = pricePerKm;
    }

    // Overloaded constructor for creation without ID
    public Cab(String cabType, double ratePerKm) {
        this.cabType = cabType;
        this.pricePerKm = ratePerKm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCabType() {
        return cabType;
    }

    public void setCabType(String cabType) {
        this.cabType = cabType;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getCabNumber() {
        return cabNumber;
    }

    public void setCabNumber(String cabNumber) {
        this.cabNumber = cabNumber;
    }

    public double getPricePerKm() {
        return pricePerKm;
    }

    public void setPricePerKm(double pricePerKm) {
        this.pricePerKm = pricePerKm;
    }

    @Override
    public String toString() {
        return "Cab{"
                + "id=" + id + ", "
                + "cabType='" + cabType + "'" + ", "
                + "driverName='" + driverName + "'" + ", "
                + "cabNumber='" + cabNumber + "'" + ", "
                + "pricePerKm=" + pricePerKm + 
                '}';
    }
}