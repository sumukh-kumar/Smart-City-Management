package com.example;

// Add imports for the Controller and Service
import com.example.controller.UtilityController;
// Update the import for UtilityService to the 'model' package
import com.example.model.UtilityService;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Hr; // Import for a visual separator
import com.vaadin.flow.component.notification.Notification; // Import for showing messages
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;

@Route("utility")
public class UtilityManagementView extends VerticalLayout {

    // UI Components remain
    private final TextArea trackingOutput;
    private final TextArea reportOutput;
    private final Button trackButton;
    private final Button reportButton;
    // Add the new delete button
    private final Button deleteButton;

    // Removed reference to UtilityService

    public UtilityManagementView() {
        // Removed service instantiation

        H2 title = new H2("Utility Management");
        Paragraph description = new Paragraph("Track power consumption, detect faults, and generate reports.");

        // --- UI Component Initialization ---
        trackButton = new Button("Track Power Consumption & Faults");
        trackingOutput = new TextArea("Live Tracking Status");
        trackingOutput.setWidthFull();
        trackingOutput.setReadOnly(true);
        trackingOutput.setValue("Click 'Track Power' to see the latest status...");

        reportButton = new Button("Generate Monthly Power Report");
        reportOutput = new TextArea("Monthly Report");
        reportOutput.setWidthFull();
        reportOutput.setReadOnly(true);
        reportOutput.setValue("Click 'Generate Report' to view analysis...");

        // Initialize the delete button
        deleteButton = new Button("Delete Old Data (Before Latest Month)");
        deleteButton.getStyle().set("margin-top", "20px"); // Add some space

        // --- Layout ---
        // Add the delete button and a separator to the layout
        add(title, description,
            trackButton, trackingOutput,
            reportButton, reportOutput,
            new Hr(), // Add a horizontal rule for visual separation
            deleteButton);
        setAlignItems(Alignment.CENTER);
        setWidth("80%");
        getStyle().set("margin", "0 auto");

        // Instantiate the Controller, passing Model and View
        new UtilityController(UtilityService.getInstance(), this);
    }

    // --- Methods for Controller Interaction ---

    /**
     * Adds a listener to the track button.
     * @param listener The listener to add.
     */
    public void addTrackButtonListener(ComponentEventListener<ClickEvent<Button>> listener) {
        trackButton.addClickListener(listener);
    }

    /**
     * Adds a listener to the report button.
     * @param listener The listener to add.
     */
    public void addReportButtonListener(ComponentEventListener<ClickEvent<Button>> listener) {
        reportButton.addClickListener(listener);
    }

    /**
     * Adds a listener to the delete button.
     * @param listener The listener to add.
     */
    public void addDeleteButtonListener(ComponentEventListener<ClickEvent<Button>> listener) {
        deleteButton.addClickListener(listener);
    }

    /**
     * Updates the content of the tracking output area.
     * @param status The text to display.
     */
    public void setTrackingStatus(String status) {
        trackingOutput.setValue(status);
    }

    /**
     * Updates the content of the report output area.
     * @param report The text to display.
     */
    public void setReportContent(String report) {
        reportOutput.setValue(report);
    }

    /**
     * Shows a notification message to the user.
     * @param message The message to display.
     * @param isError True if the message indicates an error, false otherwise.
     */
    public void showNotification(String message, boolean isError) {
        Notification notification = Notification.show(message, 3000, Notification.Position.MIDDLE);
        if (isError) {
            notification.addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_ERROR);
        } else {
            notification.addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_SUCCESS);
        }
    }

    // Removed presenter methods (displayTrackingStatus, displayMonthlyReport)
}