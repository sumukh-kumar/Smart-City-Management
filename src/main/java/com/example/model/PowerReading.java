package com.example.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Made public and moved to its own file in the model package
public class PowerReading {
    private LocalDate date;
    private double powerConsumed; // in kWh
    private boolean faultDetected;

    // Constructor
    public PowerReading(LocalDate date, double powerConsumed, boolean faultDetected) {
        this.date = date;
        this.powerConsumed = powerConsumed;
        this.faultDetected = faultDetected;
    }

    // Getters (needed for service layer access)
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
        return String.format("Date: %s, Power: %.2f kWh, Fault: %s",
                date.format(DateTimeFormatter.ISO_DATE), powerConsumed, faultDetected);
    }
}