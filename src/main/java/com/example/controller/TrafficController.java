package com.example.controller;

import com.example.TrafficManagementView;
// Import correct model classes
import com.example.model.JunctionState;
import com.example.model.ParkingSpot;
import com.example.model.TrafficService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TrafficController {

    private final TrafficService service;
    private final TrafficManagementView view;

    // Formatter for display
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TrafficController(TrafficService service, TrafficManagementView view) {
        this.service = service;
        this.view = view;

        // Attach listeners from the view to controller methods
        this.view.addRefreshButtonListener(e -> handleRefreshClick());
        this.view.addParkingButtonListener(e -> handleParkingClick());
        // Update listener for delete button
        this.view.addDeleteButtonListener(e -> handleDeleteJunctionStatesClick());

        // Initial data load when the view is created
        handleRefreshClick(); // Load initial junction and parking data
    }

    // Handles clicks on the "Refresh" button
    private void handleRefreshClick() {
        // Fetch latest junction data
        Map<String, JunctionState> latestJunctions = service.getLatestJunctionStates(); // Updated service call
        view.updateJunctionDisplay(latestJunctions); // Update the view's junction display

        // Fetch parking data (or call handleParkingClick if preferred)
        handleParkingClick();
    }

    // Handles clicks on the "Show Parking Availability" button
    private void handleParkingClick() {
        List<ParkingSpot> spots = service.getAllParkingSpots();
        // Pass the list directly to the view's updated display method
        view.displayParkingInfo(spots);
    }

    // Handles clicks on the "Delete Old Data" button
    // Renamed method
    private void handleDeleteJunctionStatesClick() {
        // Call updated service method
        String resultMessage = service.deleteOldJunctionStates();
        boolean isError = resultMessage.toLowerCase().startsWith("error");
        view.showNotification(resultMessage, isError);

        // Optionally refresh the view after deletion
        handleRefreshClick();
    }

    // --- Method for Continuous Update (Placeholder for now) ---
    // This will be implemented later using background threads and UI.access()
    public void startContinuousUpdates() {
        // TODO: Implement background polling and UI updates via UI.access()
        System.out.println("Continuous update logic to be implemented here.");
    }
}