package com.example;

import com.example.controller.UtilityController;
import com.example.model.UtilityService;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout; // Import HorizontalLayout
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;

@Route("utility")
public class UtilityManagementView extends VerticalLayout {

    private final TextArea trackingOutput;
    private final TextArea reportOutput;
    private final Button trackButton;
    private final Button reportButton;
    private final Button deleteButton;

    public UtilityManagementView() {
        // Add padding to the main layout and center it
        setPadding(true);
        setSpacing(true); // Add space between major sections

        setMaxWidth("1200px"); // Increase max width to accommodate two sections side-by-side
        setWidthFull();
        getStyle().set("margin", "0 auto");

        // --- Header Section ---
        H2 title = new H2("Utility Management Dashboard");
        Paragraph description = new Paragraph(
                "Manage power consumption tracking, fault detection, and reporting."
        );
        // Align header elements to the left
        setAlignSelf(Alignment.STRETCH, title, description);

        add(title, description);

        // Add a separator below the header
        Hr headerSeparator = new Hr();
        headerSeparator.getStyle().set("margin", "var(--lumo-space-l) 0"); // Add vertical margin
        add(headerSeparator);


        // --- Content Layout: Tracking and Reporting Side-by-Side ---
        HorizontalLayout contentLayout = new HorizontalLayout();
        contentLayout.setWidthFull(); // Allow the horizontal layout to take full width
        contentLayout.setSpacing(true); // Add spacing between the tracking and reporting sections

        // --- Tracking Section (Vertical Layout) ---
        H3 trackingHeader = new H3("Tracking & Fault Detection");
        VerticalLayout trackingSection = new VerticalLayout(trackingHeader);
        trackingSection.setPadding(true);
        trackingSection.setSpacing(true);
        trackingSection.setWidthFull(); // Make tracking section take full width *within the horizontal layout*
        contentLayout.setFlexGrow(1, trackingSection); // Allow tracking section to grow and take available space
        // Optional: Add a light border for visual separation
        trackingSection.getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");
        trackingSection.getStyle().set("border-radius", "var(--lumo-border-radius-m)");

        trackButton = new Button("Refresh");
        trackButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        trackingOutput = new TextArea("Status");
        trackingOutput.setWidthFull(); // Takes full width within its parent (trackingSection)
        trackingOutput.setReadOnly(true);
        trackingOutput.setPlaceholder("Click 'Start Tracking' to see the latest status...");
        trackingOutput.setHeight("200px"); // Give it sufficient height

        trackingSection.add(trackButton, trackingOutput);


        // --- Reporting Section (Vertical Layout) ---
        H3 reportingHeader = new H3("Monthly Power Report");
        VerticalLayout reportingSection = new VerticalLayout(reportingHeader);
        reportingSection.setPadding(true);
        reportingSection.setSpacing(true);
        reportingSection.setWidthFull(); // Make reporting section take full width *within the horizontal layout*
        contentLayout.setFlexGrow(1, reportingSection); // Allow reporting section to grow and take available space
        // Optional: Add a light border for visual separation
        reportingSection.getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");
        reportingSection.getStyle().set("border-radius", "var(--lumo-border-radius-m)");

        reportButton = new Button("Generate Report");
        reportButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        reportOutput = new TextArea("Report Content");
        reportOutput.setWidthFull(); // Takes full width within its parent (reportingSection)
        reportOutput.setReadOnly(true);
        reportOutput.setPlaceholder("Click 'Generate Report' to view analysis...");
        reportOutput.setHeight("250px"); // Potentially more height for the report

        reportingSection.add(reportButton, reportOutput);

        // Add the two sections to the horizontal layout
        contentLayout.add(trackingSection, reportingSection);

        // Add the horizontal layout to the main vertical layout
        add(contentLayout);


        // Add a separator below the horizontal layout
        Hr contentSeparator = new Hr();
        contentSeparator.getStyle().set("margin", "var(--lumo-space-l) 0"); // Add vertical margin
        add(contentSeparator);

        // --- Data Management Section (Vertical Layout) ---
        H3 dataManagementHeader = new H3("Data Management");
        VerticalLayout dataManagementSection = new VerticalLayout(dataManagementHeader);
        dataManagementSection.setPadding(true);
        dataManagementSection.setSpacing(true);
        dataManagementSection.setWidthFull(); // Make data management section take full width
        // Optional: Add a light border for visual separation
        dataManagementSection.getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");
        dataManagementSection.getStyle().set("border-radius", "var(--lumo-border-radius-m)");


        Paragraph deleteWarning = new Paragraph("Caution: This action will permanently delete historical data before the latest month.");
        deleteWarning.getStyle().set("color", "var(--lumo-error-text-color)");

        deleteButton = new Button("Delete Old Data");
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("margin-top", "var(--lumo-space-s)");

        dataManagementSection.add(deleteWarning, deleteButton);

        // Add the data management section to the main vertical layout
        add(dataManagementSection);

        // Instantiate the Controller (using singleton pattern as in original code)
        new UtilityController(UtilityService.getInstance(), this);
    }

    // --- Methods for Controller Interaction ---

    public void addTrackButtonListener(ComponentEventListener<ClickEvent<Button>> listener) {
        trackButton.addClickListener(listener);
    }

    public void addReportButtonListener(ComponentEventListener<ClickEvent<Button>> listener) {
        reportButton.addClickListener(listener);
    }

    public void addDeleteButtonListener(ComponentEventListener<ClickEvent<Button>> listener) {
        deleteButton.addClickListener(listener);
    }

    public void setTrackingStatus(String status) {
        trackingOutput.setValue(status);
    }

    public void setReportContent(String report) {
        reportOutput.setValue(report);
    }

    public void showNotification(String message, boolean isError) {
        Notification notification = Notification.show(message, 3000, Notification.Position.MIDDLE);
        if (isError) {
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }
    }
}