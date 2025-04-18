package com.example.service;

import com.example.model.TrafficSignal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TrafficService {

    private List<TrafficSignal> trafficSignals;
    private static TrafficService instance;
    private final Random random = new Random();

    // Private constructor for singleton pattern
    private TrafficService() {
        generatePlaceholderData();
    }

    // Get singleton instance
    public static synchronized TrafficService getInstance() {
        if (instance == null) {
            instance = new TrafficService();
        }
        return instance;
    }

    // Generate initial simulated data
    private void generatePlaceholderData() {
        trafficSignals = new ArrayList<>();
        trafficSignals.add(new TrafficSignal("Main St & 1st Ave", 15));
        trafficSignals.add(new TrafficSignal("Oak St & Highway 101", 25));
        trafficSignals.add(new TrafficSignal("Maple Dr & Park Rd", 8));
        // Add more signals as needed
    }

    /**
     * Gets an unmodifiable list of all traffic signals.
     * @return List of TrafficSignal objects.
     */
    public List<TrafficSignal> getAllSignals() {
        return Collections.unmodifiableList(trafficSignals);
    }

    /**
     * Simulates a change in vehicle count for a random signal
     * and automatically adjusts its duration.
     */
    public void simulateTrafficChange() {
        if (trafficSignals == null || trafficSignals.isEmpty()) {
            System.out.println("No traffic signals to simulate.");
            return;
        }

        // Pick a random signal
        TrafficSignal signal = trafficSignals.get(random.nextInt(trafficSignals.size()));

        // Simulate a change in vehicle count (e.g., +/- 10 vehicles, min 0)
        int change = random.nextInt(21) - 10; // Random change between -10 and +10
        int newVehicleCount = Math.max(0, signal.getVehicleCount() + change); // Ensure count doesn't go below 0

        // Update the vehicle count (this will also trigger duration recalculation via the setter)
        signal.setVehicleCount(newVehicleCount);

        System.out.println("Simulated traffic change for signal " + signal.getId() + " (" + signal.getLocation() + "): New count = " + newVehicleCount + ", New duration = " + signal.getSignalDuration() + "s");
    }
}