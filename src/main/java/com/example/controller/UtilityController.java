package com.example.controller; // New package for controllers

import com.example.UtilityManagementView; // Import View
import com.example.model.PowerReading;    // Import Model structure
// Update the import for UtilityService to the 'model' package
import com.example.model.UtilityService;

import com.vaadin.flow.component.notification.Notification; // Import Notification

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

// Controller class linking View and Model
public class UtilityController {

    private final UtilityService model;
    private final UtilityManagementView view;

    public UtilityController(UtilityService model, UtilityManagementView view) {
        this.model = model;
        this.view = view;

        // Attach event listeners (Controller logic) to View components
        this.view.addTrackButtonListener(event -> handleTrackAction());
        this.view.addReportButtonListener(event -> handleReportAction());
    }

    // --- Event Handling Logic ---

    /**
     * Handles the action when the track button is clicked.
     * Fetches latest reading from the model and updates the view.
     */
    private void handleTrackAction() {
        Optional<PowerReading> latestReadingOpt = model.getLatestReading(); // Interact with Model

        if (latestReadingOpt.isPresent()) {
            PowerReading latestReading = latestReadingOpt.get();
            String status = String.format("Latest Reading (%s):\n - Power Consumed: %.2f kWh\n - Fault Detected: %s",
                    latestReading.getDate().format(DateTimeFormatter.ISO_DATE),
                    latestReading.getPowerConsumed(),
                    latestReading.isFaultDetected() ? "YES" : "NO");

            view.setTrackingStatus(status); // Update View

            // Optionally, the controller can also trigger UI feedback like notifications
            if (latestReading.isFaultDetected()) {
                Notification.show("ALERT: Fault detected in the latest power reading!", 5000, Notification.Position.TOP_CENTER);
            } else {
                Notification.show("Tracking updated. No faults detected.", 2000, Notification.Position.BOTTOM_START);
            }
        } else {
            view.setTrackingStatus("No power data available."); // Update View
            Notification.show("Could not retrieve tracking data.", 3000, Notification.Position.MIDDLE);
        }
    }

    /**
     * Handles the action when the report button is clicked.
     * Generates the report using the model and updates the view.
     */
    private void handleReportAction() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        String report = model.generateMonthlyReport(lastMonth); // Interact with Model

        view.setReportContent(report); // Update View

        // Optional notification feedback
        if (!report.startsWith("No data available") && !report.startsWith("Error:")) {
             Notification.show("Monthly report generated for " + lastMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")), 3000, Notification.Position.BOTTOM_START);
        } else {
             Notification.show(report, 3000, Notification.Position.MIDDLE); // Show error/no data message
        }
    }
}