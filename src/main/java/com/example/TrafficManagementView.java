package com.example;

// Import Controller and Service
import com.example.controller.TrafficController;
// Import correct model classes
import com.example.model.JunctionState;
import com.example.model.ParkingSpot;
import com.example.model.TrafficService;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.html.Div; // Import Div for layout
import com.vaadin.flow.component.icon.Icon; // Import Icon
import com.vaadin.flow.component.icon.VaadinIcon; // Import VaadinIcon
import com.vaadin.flow.router.PageTitle; // Import PageTitle
import com.vaadin.flow.router.Route; // Import Route

// Import necessary collections and stream utilities
import java.time.format.DateTimeFormatter;
import java.util.Comparator; // Import for sorting
import java.util.List; // Import for List
import java.util.Map;
import java.util.stream.Collectors; // Import for Collectors

@Route("traffic") // Add the Route annotation back
@PageTitle("Traffic & Parking Management") // Update page title
public class TrafficManagementView extends VerticalLayout {

    // UI Components
    private final Button refreshButton;
    private final Button parkingButton;
    private final Button deleteButton;
    private final Button backButton;
    private final VerticalLayout junctionDisplayLayout;
    // Replace TextArea with a VerticalLayout for parking spots
    private final VerticalLayout parkingDisplayLayout; // Renamed from parkingDisplayArea

    // Formatter for display
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public TrafficManagementView() {
        addClassName("traffic-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER); // Center items horizontally

        // Keep background styling if desired
        getStyle()
                .set("background-image", "url(images/trafficmgmet_bg.png)") // Make sure this path is correct relative to webapp/frontend
                .set("background-size", "cover")
                .set("background-position", "center")
                .set("background-repeat", "no-repeat")
                .set("min-height", "100vh");

        // Update title
        H2 title = new H2("Smart City Traffic & Parking");

        // --- Initialize UI Components ---
        junctionDisplayLayout = new VerticalLayout();
        junctionDisplayLayout.setSpacing(false);
        junctionDisplayLayout.setPadding(false);
        junctionDisplayLayout.setWidth("100%");
        junctionDisplayLayout.add(new Paragraph("Click 'Refresh Data' to load junction status..."));

        // Initialize the new parking layout
        parkingDisplayLayout = new VerticalLayout();
        parkingDisplayLayout.setSpacing(true); // Add some space between spots
        parkingDisplayLayout.setPadding(false);
        parkingDisplayLayout.setWidth("100%");
        parkingDisplayLayout.add(new Paragraph("Click 'Show Parking' to load status...")); // Initial text
        // Optional: Add some max height and scrollbars if the list can be long
        parkingDisplayLayout.getStyle()
            .set("max-height", "400px") // Example max height
            .set("overflow-y", "auto"); // Enable vertical scrolling


        refreshButton = new Button("Refresh Data");
        parkingButton = new Button("Show Parking");
        deleteButton = new Button("Delete Old Junction Data");
        deleteButton.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR);
        backButton = new Button("Back to Main Menu");
        backButton.addClickListener(e -> backButton.getUI().ifPresent(ui -> ui.navigate("")));

        // --- Layout ---
        HorizontalLayout buttonBar = new HorizontalLayout(refreshButton, parkingButton, deleteButton, backButton);
        buttonBar.setSpacing(true);

        // Content Box (semi-transparent white background)
        VerticalLayout contentBox = new VerticalLayout(
                new H2("Live Junction Status"),
                junctionDisplayLayout,
                new H2("Parking Status"),
                parkingDisplayLayout, // Use the new parking layout
                buttonBar
        );
        contentBox.setAlignItems(Alignment.CENTER);
        contentBox.getStyle()
           .set("background-color", "rgba(255, 255, 255, 0.85)") // Slightly more opaque
           .set("padding", "20px")
           .set("border-radius", "10px");
        contentBox.setWidth("80%"); // Adjust width
        contentBox.setHeight("auto"); // Adjust height based on content

        // Add title and content box to the main layout
        add(title, contentBox);
        setJustifyContentMode(JustifyContentMode.CENTER); // Center the content box vertically

        // Instantiate the Controller, passing Model (via Service instance) and View
        // No change needed here as controller constructor remains the same
        new TrafficController(TrafficService.getInstance(), this);
    }

    // --- Methods for Controller Interaction ---

    /**
     * Updates the junction display area with the latest states.
     * @param latestJunctions A map of junctionId to JunctionState.
     */
    public void updateJunctionDisplay(Map<String, JunctionState> latestJunctions) {
        junctionDisplayLayout.removeAll();
        if (latestJunctions == null || latestJunctions.isEmpty()) {
            junctionDisplayLayout.add(new Paragraph("No junction data available."));
            return;
        }

        latestJunctions.values().stream()
            .sorted(Comparator.comparing(JunctionState::getJunctionId))
            .forEach(state -> {
                VerticalLayout junctionLayout = new VerticalLayout();
                junctionLayout.setSpacing(false);
                junctionLayout.setPadding(false);
                junctionLayout.getStyle().set("border", "1px solid #eee").set("padding", "10px").set("margin-bottom", "10px");

                // Display Junction ID and Timestamp using the correct getter
                junctionLayout.add(new Span("Junction: " + state.getJunctionId() +
                                            " (Updated: " + (state.getLastUpdated() != null ? state.getLastUpdated().format(dtf) : "N/A") + ")")); // Use getLastUpdated()

                // Display Lanes in a Horizontal Layout
                HorizontalLayout lanesLayout = new HorizontalLayout();
                lanesLayout.setSpacing(true);
                lanesLayout.setWidthFull(); // Make lanes layout take full width

                // Create spans for each lane, highlighting the green one
                Span lane1 = createLaneSpan(1, state.getLane1Vehicles(), state.getGreenLaneId());
                Span lane2 = createLaneSpan(2, state.getLane2Vehicles(), state.getGreenLaneId());
                Span lane3 = createLaneSpan(3, state.getLane3Vehicles(), state.getGreenLaneId());
                Span lane4 = createLaneSpan(4, state.getLane4Vehicles(), state.getGreenLaneId());

                lanesLayout.add(lane1, lane2, lane3, lane4);
                lanesLayout.setJustifyContentMode(JustifyContentMode.BETWEEN); // Space out lanes

                junctionLayout.add(lanesLayout);
                junctionDisplayLayout.add(junctionLayout);
            });
    }

    // Helper method to create styled spans for lanes
    private Span createLaneSpan(int laneId, int vehicleCount, int greenLaneId) {
        Span laneSpan = new Span(String.format("Lane %d: %d vehicles", laneId, vehicleCount));
        laneSpan.getStyle()
                .set("border", "1px solid #ccc")
                .set("padding", "8px")
                .set("border-radius", "4px")
                .set("text-align", "center")
                .set("flex-grow", "1"); // Allow spans to grow

        if (laneId == greenLaneId) {
            laneSpan.getStyle()
                    .set("background-color", "#90EE90") // Light green background
                    .set("font-weight", "bold")
                    .set("border-color", "#2E8B57"); // Darker green border
        } else {
            laneSpan.getStyle().set("background-color", "#f0f0f0"); // Light grey background
        }
        return laneSpan;
    }


    /**
     * Displays parking information dynamically using individual components.
     * @param spots The list of ParkingSpot objects.
     */
    // Updated method signature and logic
    public void displayParkingInfo(List<ParkingSpot> spots) {
        parkingDisplayLayout.removeAll(); // Clear previous content

        if (spots == null || spots.isEmpty()) {
            parkingDisplayLayout.add(new Paragraph("No parking spot data available."));
            return;
        }

        long availableCount = spots.stream().filter(s -> !s.isOccupied()).count();
        long totalCount = spots.size();

        // Add a summary header
        H2 parkingHeader = new H2(String.format("Parking Availability (%d / %d Available)", availableCount, totalCount));
        parkingDisplayLayout.add(parkingHeader);


        // Display each parking spot
        spots.stream()
             // Optional: Sort by spot ID
             .sorted(Comparator.comparing(ParkingSpot::getSpotId))
             .forEach(spot -> {
                 // Use HorizontalLayout for each spot's details
                 HorizontalLayout spotLayout = new HorizontalLayout();
                 spotLayout.setWidthFull();
                 spotLayout.setAlignItems(Alignment.CENTER); // Align items vertically
                 spotLayout.getStyle()
                         .set("border-bottom", "1px solid #eee") // Separator line
                         .set("padding", "10px 0"); // Add some padding

                 // Icon and Status Text
                 Icon statusIcon;
                 Span statusText = new Span();
                 statusText.getStyle().set("font-weight", "bold");

                 if (spot.isOccupied()) {
                     statusIcon = VaadinIcon.CLOSE_CIRCLE.create();
                     statusIcon.setColor("red");
                     statusText.setText("Occupied");
                     statusText.getStyle().set("color", "red");
                 } else {
                     statusIcon = VaadinIcon.CHECK_CIRCLE.create();
                     statusIcon.setColor("green");
                     statusText.setText("Available");
                     statusText.getStyle().set("color", "green");
                 }

                 // Spot ID and Description
                 String description = spot.getLocationDescription() != null && !spot.getLocationDescription().isEmpty()
                                      ? " (" + spot.getLocationDescription() + ")" : "";
                 Span spotIdSpan = new Span(spot.getSpotId() + description);
                 spotIdSpan.getStyle().set("flex-grow", "1"); // Allow ID to take up space

                 // Last Updated Time
                 String updatedTime = spot.getLastUpdated() != null
                                      ? spot.getLastUpdated().format(dtf) : "N/A";
                 Span timeSpan = new Span("Updated: " + updatedTime);
                 timeSpan.getStyle().set("font-size", "small").set("color", "gray");

                 // Add components to the spot layout
                 spotLayout.add(statusIcon, statusText, spotIdSpan, timeSpan);
                 parkingDisplayLayout.add(spotLayout); // Add the spot layout to the main parking layout
             });
    }


    /**
     * Shows a notification message to the user.
     * @param message The message to display.
     * @param isError True if the message represents an error.
     */
    public void showNotification(String message, boolean isError) {
        Notification notification = new Notification(message, 3000, Notification.Position.MIDDLE);
        if (isError) {
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }
        notification.open();
    }

    // --- Listener Adders ---

    public void addRefreshButtonListener(ComponentEventListener<ClickEvent<Button>> listener) {
        refreshButton.addClickListener(listener);
    }

    public void addParkingButtonListener(ComponentEventListener<ClickEvent<Button>> listener) {
        parkingButton.addClickListener(listener);
    }

    public void addDeleteButtonListener(ComponentEventListener<ClickEvent<Button>> listener) {
        deleteButton.addClickListener(listener);
    }

    // Removed onAttach and other unused methods/fields
}