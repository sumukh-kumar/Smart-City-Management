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
// Removed Notification import (will be handled by Controller if needed)
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;

@Route("utility")
public class UtilityManagementView extends VerticalLayout {

    // UI Components remain
    private final TextArea trackingOutput;
    private final TextArea reportOutput;
    private final Button trackButton; // Make buttons fields to attach listeners
    private final Button reportButton;

    // Removed reference to UtilityService

    public UtilityManagementView() {
        // Removed service instantiation

        H2 title = new H2("Utility Management");
        Paragraph description = new Paragraph("Track power consumption, detect faults, and generate reports.");

        // --- UI Component Initialization ---
        trackButton = new Button("Track Power Consumption & Faults"); // Listener set by Controller
        trackingOutput = new TextArea("Live Tracking Status");
        trackingOutput.setWidthFull();
        trackingOutput.setReadOnly(true);
        trackingOutput.setValue("Click 'Track Power' to see the latest status...");

        reportButton = new Button("Generate Monthly Power Report"); // Listener set by Controller
        reportOutput = new TextArea("Monthly Report");
        reportOutput.setWidthFull();
        reportOutput.setReadOnly(true);
        reportOutput.setValue("Click 'Generate Report' to view analysis...");

        // --- Layout ---
        add(title, description, trackButton, trackingOutput, reportButton, reportOutput);
        setAlignItems(Alignment.CENTER);
        setWidth("80%");
        getStyle().set("margin", "0 auto");

        // Instantiate the Controller, passing Model and View
        // This line now uses the updated import for UtilityService
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

    // Removed presenter methods (displayTrackingStatus, displayMonthlyReport)
}