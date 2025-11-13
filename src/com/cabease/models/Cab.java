package com.cabease.models;
public class Cab {
    private int id;
    private String cabNumber;
    private String model;
    private boolean isAvailable;
    private String driverName;
    private String driverContact;
    public Cab(String cabNumber, String model, boolean isAvailable, String driverName, String driverContact) {
        this.cabNumber = cabNumber;
        this.model = model;
        this.isAvailable = isAvailable;
        this.driverName = driverName;
        this.driverContact = driverContact;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getCabNumber() {
        return cabNumber;
    }
    public void setCabNumber(String cabNumber) {
        this.cabNumber = cabNumber;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public boolean isAvailable() {
        return isAvailable;
    }
    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
    public String getDriverName() {
        return driverName;
    }
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
    public String getDriverContact() {
        return driverContact;
    }
    public void setDriverContact(String driverContact) {
        this.driverContact = driverContact;
    }
}
