package com.example.service;

import java.util.*;
import java.util.stream.IntStream;

public class TrafficService {

    // --- Singleton Pattern ---
    private static TrafficService instance;
    private final Random random = new Random();
    private static final int NUM_LANES = 4; // Define number of lanes per direction

    // --- State ---
    public enum LightState { RED, YELLOW_NS, YELLOW_EW, GREEN_NS, GREEN_EW } // More specific green/yellow states
    public enum Direction { NORTH, SOUTH, EAST, WEST }

    private Map<Direction, int[]> carsPerLane; // Stores car count for each lane
    private LightState currentLightState;
    private int greenTimer = 0; // How long the current green light has been active
    private final int MIN_GREEN_TIME = 5; // Minimum steps a light stays green
    private final int YELLOW_TIME = 2;  // Steps for yellow light

    private TrafficService() {
        carsPerLane = new EnumMap<>(Direction.class);
        for (Direction dir : Direction.values()) {
            carsPerLane.put(dir, new int[NUM_LANES]);
            // Initialize with some random cars
            for(int i = 0; i < NUM_LANES; i++) {
                carsPerLane.get(dir)[i] = random.nextInt(5); // 0-4 cars initially per lane
            }
        }
        // Initial state: N/S Green
        currentLightState = LightState.GREEN_NS;
        greenTimer = MIN_GREEN_TIME + random.nextInt(5); // Initial green duration
        System.out.println("Initial State: " + currentLightState + ", Cars: " + carsPerLane);
    }

    public static synchronized TrafficService getInstance() {
        if (instance == null) {
            instance = new TrafficService();
        }
        return instance;
    }

    // --- Simulation Logic ---

    /**
     * Simulates one step: cars arrive/leave, lights potentially change based on density.
     */
    public void simulateStep() {
        // 1. Simulate car arrival/departure (simple random changes)
        simulateCarChanges();

        // 2. Update light state based on timer and density
        updateLightState();

        System.out.println("State after step: " + currentLightState + ", Timer: " + greenTimer + ", Cars: " + carsPerLane);
    }

    private void simulateCarChanges() {
        for (Direction dir : Direction.values()) {
            for (int i = 0; i < NUM_LANES; i++) {
                // Chance to add a car
                if (random.nextInt(10) < 3) { // 30% chance
                    carsPerLane.get(dir)[i] = Math.min(10, carsPerLane.get(dir)[i] + 1); // Max 10 cars per lane
                }
                // Chance for a car to leave (only if light is green for that direction)
                boolean isGreen = (currentLightState == LightState.GREEN_NS && (dir == Direction.NORTH || dir == Direction.SOUTH)) ||
                                  (currentLightState == LightState.GREEN_EW && (dir == Direction.EAST || dir == Direction.WEST));
                if (isGreen && carsPerLane.get(dir)[i] > 0 && random.nextInt(10) < 5) { // 50% chance if green
                     carsPerLane.get(dir)[i]--;
                }
            }
        }
    }

    private void updateLightState() {
        greenTimer--;

        switch (currentLightState) {
            case GREEN_NS:
                if (greenTimer <= 0) { // Time to consider switching
                   currentLightState = LightState.YELLOW_NS; // Switch to Yellow N/S
                   greenTimer = YELLOW_TIME;
                }
                break;
            case YELLOW_NS:
                 if (greenTimer <= 0) { // Yellow time finished
                    currentLightState = LightState.GREEN_EW; // Switch to E/W Green
                    // Set green time based on density (simple example)
                    int ewWaiting = getTotalCars(Direction.EAST) + getTotalCars(Direction.WEST);
                    greenTimer = MIN_GREEN_TIME + (ewWaiting / 2); // Longer green for more cars
                 }
                break;
            case GREEN_EW:
                 if (greenTimer <= 0) { // Time to consider switching
                    currentLightState = LightState.YELLOW_EW; // Switch to Yellow E/W
                    greenTimer = YELLOW_TIME;
                 }
                break;
            case YELLOW_EW:
                 if (greenTimer <= 0) { // Yellow time finished
                    currentLightState = LightState.GREEN_NS; // Switch to N/S Green
                    // Set green time based on density
                    int nsWaiting = getTotalCars(Direction.NORTH) + getTotalCars(Direction.SOUTH);
                    greenTimer = MIN_GREEN_TIME + (nsWaiting / 2);
                 }
                break;
        }
    }

    // --- Data Access ---

    public Map<Direction, int[]> getCarsPerLane() {
        // Return a deep copy to prevent external modification
        Map<Direction, int[]> copy = new EnumMap<>(Direction.class);
        for(Map.Entry<Direction, int[]> entry : carsPerLane.entrySet()) {
            copy.put(entry.getKey(), Arrays.copyOf(entry.getValue(), entry.getValue().length));
        }
        return copy;
    }

    public LightState getCurrentLightState() {
        return currentLightState;
    }

    private int getTotalCars(Direction dir) {
        return IntStream.of(carsPerLane.get(dir)).sum();
    }

    // --- Old methods removed ---
    // public Map<Direction, LightState> simulateLightChange() { ... }
    // public Map<Direction, LightState> getCurrentLightStates() { ... }
}