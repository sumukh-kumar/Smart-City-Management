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
     * Simulates a change in vehicle count for a random signal,
     * adjusts its duration, and returns a message describing the change.
     * @return A String describing the simulated change, or null if no signals exist.
     */
    public String simulateTrafficChange() { // Changed return type to String
        if (trafficSignals == null || trafficSignals.isEmpty()) {
            System.out.println("No traffic signals to simulate.");
            return null; // Return null if no simulation happened
        }

        // Pick a random signal
        TrafficSignal signal = trafficSignals.get(random.nextInt(trafficSignals.size()));
        // System.out.println(">>> Simulating change for signal: " + signal); // DEBUG: Before change (optional)
        int oldVehicleCount = signal.getVehicleCount(); // Store old value for message

        // Simulate a change in vehicle count (e.g., +/- 10 vehicles, min 0)
        int change = random.nextInt(21) - 10; // Random change between -10 and +10
        int newVehicleCount = Math.max(0, signal.getVehicleCount() + change); // Ensure count doesn't go below 0

        // Update the vehicle count (this will also trigger duration recalculation via the setter)
        signal.setVehicleCount(newVehicleCount);

        // System.out.println("<<< Signal after change: " + signal); // DEBUG: After change (optional)

        // Construct the message to be returned
        String message = String.format(
            "Simulated change for signal %d (%s): Vehicle count changed from %d to %d. New duration: %d s.",
            signal.getId(),
            signal.getLocation(),
            oldVehicleCount,
            newVehicleCount,
            signal.getSignalDuration()
        );
        System.out.println(message); // Keep printing to console as well (optional)
        return message; // Return the message
    }
}