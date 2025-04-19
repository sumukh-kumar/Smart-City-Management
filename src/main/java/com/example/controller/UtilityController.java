package com.example.controller;

import com.example.UtilityManagementView;
import com.example.model.PowerReading;
import com.example.model.UtilityService;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class UtilityController {

    private final UtilityService service;
    private final UtilityManagementView view;

    public UtilityController(UtilityService service, UtilityManagementView view) {
        this.service = service;
        this.view = view;

        // Attach listeners
        this.view.addTrackButtonListener(e -> handleTrackButtonClick());
        this.view.addReportButtonListener(e -> handleReportButtonClick());
        // Attach listener for the new delete button
        this.view.addDeleteButtonListener(e -> handleDeleteButtonClick());
    }

    private void handleTrackButtonClick() {
        // Remove logging
        // System.out.println("DEBUG: handleTrackButtonClick called.");
        Optional<PowerReading> latestReadingOpt = service.getLatestReading();
        // Remove logging
        // System.out.println("DEBUG: latestReadingOpt.isPresent() = " + latestReadingOpt.isPresent());

        if (latestReadingOpt.isPresent()) {
            PowerReading reading = latestReadingOpt.get();
            // Remove logging
            // System.out.println("DEBUG: Latest Reading Found: ID=" + reading.getId() + ", Date=" + reading.getDate());
            String status = String.format(
                "Latest Reading (ID: %d, Date: %s):\n - Power Consumed: %.2f kWh\n - Fault Detected: %s",
                reading.getId(),
                // Correct the method call here to use getDate()
                reading.getDate().format(DateTimeFormatter.ISO_DATE),
                reading.getPowerConsumed(),
                reading.isFaultDetected() ? "YES" : "NO"
            );
            view.setTrackingStatus(status);
        } else {
            // Remove logging
            // System.out.println("DEBUG: No latest reading found by service.");
            view.setTrackingStatus("No data available yet.");
        }
    }

    private void handleReportButtonClick() {
        // Generate report for the previous month for demonstration
        YearMonth monthToReport = YearMonth.now().minusMonths(1);
        String report = service.generateMonthlyReport(monthToReport);
        view.setReportContent(report);
    }

    // --- New Handler Method for Delete Button ---
    private void handleDeleteButtonClick() {
        // Call the service method to perform deletion
        String resultMessage = service.deleteReadingsBeforeLatestMonth();

        // Show the result to the user via a notification
        // Check if the message indicates an error
        boolean isError = resultMessage.toLowerCase().startsWith("error");
        view.showNotification(resultMessage, isError);

        // Optionally, refresh the tracking status or report if needed after deletion
        // handleTrackButtonClick(); // Uncomment if you want to refresh latest reading display
    }
}