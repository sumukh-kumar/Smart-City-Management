package com.example.model;

public class TrafficSignal {
    private static long idCounter = 0;
    private Long id;
    private String location;
    private int vehicleCount;
    private int signalDuration; // Duration in seconds

    public TrafficSignal(String location, int initialVehicleCount) {
        this.id = ++idCounter;
        this.location = location;
        this.vehicleCount = initialVehicleCount;
        // Initial duration calculation (example)
        this.signalDuration = calculateDuration(initialVehicleCount);
    }

    // Basic logic to calculate duration based on vehicle count
    private int calculateDuration(int count) {
        // Example: 30 seconds base + 1 second per vehicle, max 120s
        return Math.min(30 + count, 120);
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public int getVehicleCount() {
        return vehicleCount;
    }

    public int getSignalDuration() {
        return signalDuration;
    }

    // Setters needed for updates
    public void setVehicleCount(int vehicleCount) {
        this.vehicleCount = vehicleCount;
        // Recalculate duration whenever vehicle count changes
        this.signalDuration = calculateDuration(vehicleCount);
    }

    // No setter for signalDuration directly, it's derived from vehicleCount

    @Override
    public String toString() {
        return "TrafficSignal{" +
               "id=" + id +
               ", location='" + location + '\'' +
               ", vehicleCount=" + vehicleCount +
               ", signalDuration=" + signalDuration +
               '}';
    }
}