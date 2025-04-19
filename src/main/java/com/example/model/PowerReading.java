package com.example.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Made public and moved to its own file in the model package
public class PowerReading {
    private int id; // Added ID field
    private LocalDate date;
    private double powerConsumed; // in kWh
    private boolean faultDetected;

    // Constructor - Keep the old one for potential compatibility if needed elsewhere temporarily
    public PowerReading(LocalDate date, double powerConsumed, boolean faultDetected) {
        // this.id = 0; // Or handle appropriately if needed
        this.date = date;
        this.powerConsumed = powerConsumed;
        this.faultDetected = faultDetected;
    }

    // New constructor including ID (useful when reading from DB)
    public PowerReading(int id, LocalDate date, double powerConsumed, boolean faultDetected) {
        this.id = id;
        this.date = date;
        this.powerConsumed = powerConsumed;
        this.faultDetected = faultDetected;
    }


    // Getters (needed for service layer access)
    public int getId() { // Getter for ID
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getPowerConsumed() {
        return powerConsumed;
    }

    public boolean isFaultDetected() {
        return faultDetected;
    }

    @Override
    public String toString() {
        // Include ID in toString for clarity
        return String.format("ID: %d, Date: %s, Power: %.2f kWh, Fault: %s",
                id, date.format(DateTimeFormatter.ISO_DATE), powerConsumed, faultDetected);
    }
}