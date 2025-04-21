package com.example;

import com.example.service.TrafficService;
import com.example.service.TrafficService.Direction;
import com.example.service.TrafficService.LightState;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility; // For utility classes

import java.util.EnumMap;
import java.util.Map;

@Route("traffic")
@PageTitle("Traffic Simulation")
public class TrafficManagementView extends VerticalLayout {

    private final TrafficService trafficService;
    private Button simulateButton;
    private Button backButton;

    // Layout containers for different parts of the intersection
    private VerticalLayout northApproach;
    private VerticalLayout southApproach;
    private HorizontalLayout westApproach;
    private HorizontalLayout eastApproach;
    private Div centerIntersection;

    // Divs for the traffic lights (could be more complex later)
    private Map<Direction, Div> lightDisplays = new EnumMap<>(Direction.class);

    private static final int NUM_LANES = 4; // Must match service
    private static final String CAR_BLOCK_STYLE = "car-block";
    private static final String LANE_STYLE = "lane";

    private HorizontalLayout middleRow; // Declare as field
    private Div intersectionContainer; // Declare as field

    public TrafficManagementView() {
        this.trafficService = TrafficService.getInstance();

        addClassName("traffic-simulation-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Inject CSS styles for lanes and cars
        injectCustomCss();

        add(new H2("Density-Based Traffic Simulation"));

        // Create the visual layout for the intersection
        // This method will now fully assemble the intersectionContainer
        createIntersectionLayout();

        configureButtons();

        // --- Main Layout Assembly ---
        // REMOVE the assembly logic from here as it's now inside createIntersectionLayout
        // HorizontalLayout middleRow = new HorizontalLayout(westApproach, centerIntersection, eastApproach);
        // middleRow.setAlignItems(Alignment.CENTER);
        // middleRow.setSpacing(false); // No space between approaches and center
        //
        // Div intersectionContainer = new Div(northApproach, middleRow, southApproach);
        // // Style the container (optional)
        // intersectionContainer.addClassName("intersection-container");
        // intersectionContainer.getStyle()
        //     .set("display", "inline-block") // Allow centering
        //     .set("padding", "20px")
        //     .set("background-color", "rgba(200, 200, 200, 0.8)")
        //     .set("border-radius", "10px");


        HorizontalLayout buttonLayout = new HorizontalLayout(simulateButton, backButton);
        buttonLayout.setSpacing(true);
        buttonLayout.addClassName(LumoUtility.Margin.Top.MEDIUM); // Add space above buttons

        // Add the fully assembled intersectionContainer and buttons
        // to the main VerticalLayout (this class)
        add(intersectionContainer, buttonLayout); // Add the field intersectionContainer

        updateVisuals(); // Initial display
    }

    private void injectCustomCss() {
        String css = """
            <style>
                .lane {
                    display: flex;
                    border: 1px solid grey;
                    background-color: lightgrey;
                    min-height: 80px; /* N/S lane height */
                    min-width: 25px;  /* N/S lane width */
                    margin: 1px;
                    box-sizing: border-box;
                    flex-shrink: 0; /* Prevent individual lanes from shrinking */
                }
                .vertical-lanes .lane {
                    flex-direction: column-reverse;
                    justify-content: flex-start;
                }
                 .horizontal-lanes .lane {
                    flex-direction: row-reverse;
                     align-items: center;
                     justify-content: flex-start;
                     min-height: 25px; /* E/W lane height */
                     min-width: 80px; /* E/W lane width */
                }
                .car-block {
                    width: 12px;
                    height: 8px;
                    background-color: blue;
                    border: 1px solid darkblue;
                    margin: 1px;
                    flex-shrink: 0;
                }
                .traffic-light {
                    width: 25px;
                    height: 25px;
                    border: 1px solid black;
                    border-radius: 50%;
                    background-color: grey;
                    flex-shrink: 0;
                    margin: 5px;
                }
                .intersection-center {
                     width: 130px;
                     height: 130px;
                     background-color: darkgrey;
                     display: flex;
                     align-items: center;
                     justify-content: center;
                     font-size: small;
                     color: white;
                     flex-shrink: 0;
                }

                /* Style the approach containers themselves */
                 .vertical-lanes, .horizontal-lanes {
                    padding: 0;
                    margin: 0;
                    flex-shrink: 0; /* Prevent the whole approach block from shrinking */
                    /* display: inline-flex; /* Try inline-flex to see if it helps sizing */
                 }

                 /* Target the specific Layout components holding the lanes AGAIN */
                 /* N/S Lanes Container (HorizontalLayout inside VerticalLayout .vertical-lanes) */
                 .vertical-lanes > vaadin-horizontal-layout {
                     display: inline-flex; /* Use inline-flex, might respect content size better */
                     flex-wrap: nowrap !important; /* Force no wrapping */
                     padding: 0;
                     gap: 0;
                     box-sizing: border-box; /* Ensure padding/border included */
                 }
                  /* E/W Lanes Container (VerticalLayout inside HorizontalLayout .horizontal-lanes) */
                 .horizontal-lanes > vaadin-vertical-layout {
                      display: inline-flex; /* Use inline-flex */
                      flex-direction: column;
                      flex-wrap: nowrap !important; /* Force no wrapping */
                      padding: 0;
                      gap: 0;
                      box-sizing: border-box;
                  }

            </style>
        """;
        Div cssDiv = new Div();
        cssDiv.getElement().setProperty("innerHTML", css);
        getElement().appendChild(cssDiv.getElement());
    }


    // REMOVE these constants
    // private static final String NS_LANE_CONTAINER_WIDTH = (NUM_LANES * 25 + (NUM_LANES * 2) + 10) + "px";
    // private static final String EW_LANE_CONTAINER_HEIGHT = (NUM_LANES * 25 + (NUM_LANES * 2) + 10) + "px";


    private void createIntersectionLayout() {
        // --- Create Lights First ---
        createLightDiv(Direction.NORTH);
        createLightDiv(Direction.SOUTH);
        createLightDiv(Direction.WEST);
        createLightDiv(Direction.EAST);

        // --- North Approach (Vertical Lanes) ---
        northApproach = new VerticalLayout();
        northApproach.setSpacing(false);
        northApproach.setPadding(false);
        northApproach.setAlignItems(Alignment.CENTER);
        northApproach.addClassName("vertical-lanes");
        HorizontalLayout northLanes = new HorizontalLayout();
        northLanes.setSpacing(false);
        northLanes.setPadding(false);
        northLanes.getStyle().set("flex-wrap", "nowrap"); // Keep this
        // REMOVE setMinWidth
        // northLanes.setMinWidth(NS_LANE_CONTAINER_WIDTH);
        for (int i = 0; i < NUM_LANES; i++) {
            northLanes.add(createLaneDiv(Direction.NORTH, i));
        }
        northApproach.add(northLanes);

        // --- South Approach (Vertical Lanes) ---
        southApproach = new VerticalLayout();
        southApproach.setSpacing(false);
        southApproach.setPadding(false);
        southApproach.setAlignItems(Alignment.CENTER);
        southApproach.addClassName("vertical-lanes");
        HorizontalLayout southLanes = new HorizontalLayout();
        southLanes.setSpacing(false);
        southLanes.setPadding(false);
        southLanes.getStyle().set("flex-wrap", "nowrap"); // Keep this
        // REMOVE setMinWidth
        // southLanes.setMinWidth(NS_LANE_CONTAINER_WIDTH);
        for (int i = 0; i < NUM_LANES; i++) {
            southLanes.add(createLaneDiv(Direction.SOUTH, i));
        }
        southApproach.add(southLanes);


        // --- West Approach (Horizontal Lanes) ---
        westApproach = new HorizontalLayout();
        westApproach.setSpacing(false);
        westApproach.setPadding(false);
        westApproach.setJustifyContentMode(JustifyContentMode.CENTER);
        westApproach.addClassName("horizontal-lanes");
        VerticalLayout westLanes = new VerticalLayout();
        westLanes.setSpacing(false);
        westLanes.setPadding(false);
        westLanes.getStyle().set("flex-wrap", "nowrap"); // Keep this
        // REMOVE setMinHeight
        // westLanes.setMinHeight(EW_LANE_CONTAINER_HEIGHT);
        for (int i = 0; i < NUM_LANES; i++) {
            westLanes.add(createLaneDiv(Direction.WEST, i));
        }
        westApproach.add(westLanes);


        // --- East Approach (Horizontal Lanes) ---
        eastApproach = new HorizontalLayout();
        eastApproach.setSpacing(false);
        eastApproach.setPadding(false);
        eastApproach.setJustifyContentMode(JustifyContentMode.CENTER);
        eastApproach.addClassName("horizontal-lanes");
        VerticalLayout eastLanes = new VerticalLayout();
        eastLanes.setSpacing(false);
        eastLanes.setPadding(false);
        eastLanes.getStyle().set("flex-wrap", "nowrap"); // Keep this
        // REMOVE setMinHeight
        // eastLanes.setMinHeight(EW_LANE_CONTAINER_HEIGHT);
        for (int i = 0; i < NUM_LANES; i++) {
            eastLanes.add(createLaneDiv(Direction.EAST, i));
        }
        eastApproach.add(eastLanes);


        // --- Center Intersection Placeholder ---
        centerIntersection = new Div();
        centerIntersection.addClassName("intersection-center");
        centerIntersection.setText("Intersection");

        // --- Assemble Middle Row with Lights (inside this method) ---
        // Use the field middleRow
        middleRow = new HorizontalLayout(westApproach, lightDisplays.get(Direction.WEST), centerIntersection, lightDisplays.get(Direction.EAST), eastApproach);
        middleRow.setAlignItems(Alignment.CENTER);
        middleRow.setSpacing(true); // Add some space around lights/center
        middleRow.setJustifyContentMode(JustifyContentMode.CENTER);

        // --- Assemble Intersection Container with Lights (inside this method) ---
        // Use the field intersectionContainer
        intersectionContainer = new Div(lightDisplays.get(Direction.NORTH), middleRow, lightDisplays.get(Direction.SOUTH));
        intersectionContainer.addClassName("intersection-container");
        // Apply styles to center the whole block and add padding/background
        intersectionContainer.getStyle()
            .set("display", "flex") // Use flexbox for alignment
            .set("flex-direction", "column") // Stack N light, middle, S light vertically
            .set("align-items", "center") // Center items horizontally
            .set("gap", "5px") // Space between N light, middle, S light
            .set("padding", "20px")
            .set("background-color", "rgba(200, 200, 200, 0.8)")
            .set("border-radius", "10px");

        // No need to set alignment here, done in constructor for the main layout
        // setAlignItems(Alignment.CENTER);
    }

    // Helper to create a lane Div (ensure this exists)
    private Div createLaneDiv(Direction dir, int laneIndex) {
        Div lane = new Div();
        lane.addClassName(LANE_STYLE);
        lane.getElement().setAttribute("data-direction", dir.name());
        lane.getElement().setAttribute("data-lane", String.valueOf(laneIndex));
        return lane;
    }

     // Helper to create a traffic light Div (ensure this exists)
    private Div createLightDiv(Direction dir) {
        Div light = new Div();
        light.addClassName("traffic-light");
        lightDisplays.put(dir, light);
        return light;
    }

    private void configureButtons() {
        simulateButton = new Button("Simulate Step"); // Renamed button
        simulateButton.addClickListener(e -> {
            trafficService.simulateStep(); // Advance the simulation state
            updateVisuals(); // Update visuals based on new state
        });

        backButton = new Button("Back to Main Menu");
        backButton.addClickListener(e -> backButton.getUI().ifPresent(ui -> ui.navigate("")));
    }

    // Update both lights and car blocks
    private void updateVisuals() {
        updateLightDisplays();
        updateCarDisplays();
    }

    private void updateLightDisplays() {
        LightState currentState = trafficService.getCurrentLightState();
        // Determine color based on the overall state
        String nsColor = "grey"; // Default color when off or state unknown
        String ewColor = "grey"; // Default color when off or state unknown

        // Set colors based on the current simulation state
        switch (currentState) {
            case GREEN_NS:  nsColor = "lime"; ewColor = "red"; break; // North/South Green, East/West Red
            case YELLOW_NS: nsColor = "yellow"; ewColor = "red"; break; // North/South Yellow, East/West Red
            case GREEN_EW:  nsColor = "red"; ewColor = "lime"; break; // North/South Red, East/West Green
            case YELLOW_EW: nsColor = "red"; ewColor = "yellow"; break; // North/South Red, East/West Yellow
        }

        // Apply the determined colors to the light Divs
        lightDisplays.get(Direction.NORTH).getStyle().set("background-color", nsColor);
        lightDisplays.get(Direction.SOUTH).getStyle().set("background-color", nsColor);
        lightDisplays.get(Direction.EAST).getStyle().set("background-color", ewColor);
        lightDisplays.get(Direction.WEST).getStyle().set("background-color", ewColor);
    }

    private void updateCarDisplays() {
        Map<Direction, int[]> currentCars = trafficService.getCarsPerLane();
        // Update all approaches using the refactored helper
        updateCarsForApproach(northApproach, currentCars);
        updateCarsForApproach(southApproach, currentCars);
        updateCarsForApproach(westApproach, currentCars);
        updateCarsForApproach(eastApproach, currentCars);
    }

    // Refactored helper to update cars for any approach layout
    // Uses Component as the parameter type to accept both VerticalLayout and HorizontalLayout
    private void updateCarsForApproach(com.vaadin.flow.component.Component approachLayout, Map<Direction, int[]> currentCars) {
         // Find the layout containing the lanes (the first child that is HLayout or VLayout)
         approachLayout.getChildren()
             .filter(comp -> comp instanceof HorizontalLayout || comp instanceof VerticalLayout) // Find lane container
             .findFirst()
             .ifPresent(laneContainer -> {
                 // Iterate through the children of the lane container
                 laneContainer.getChildren()
                     .filter(comp -> comp instanceof Div && comp.getElement().hasAttribute("data-direction")) // Find lane Divs
                     .forEach(laneComp -> {
                         Div laneDiv = (Div) laneComp;
                         // Safely parse attributes
                         try {
                             Direction dir = Direction.valueOf(laneDiv.getElement().getAttribute("data-direction"));
                             int laneIndex = Integer.parseInt(laneDiv.getElement().getAttribute("data-lane"));

                             // Check if the direction and laneIndex are valid for the currentCars map
                             if (currentCars.containsKey(dir) && laneIndex >= 0 && laneIndex < currentCars.get(dir).length) {
                                 int carCount = currentCars.get(dir)[laneIndex];

                                 // Remove old car blocks
                                 laneDiv.removeAll();
                                 // Add new car blocks
                                 for (int i = 0; i < carCount; i++) {
                                     Div carBlock = new Div();
                                     carBlock.addClassName(CAR_BLOCK_STYLE);
                                     laneDiv.add(carBlock);
                                 }
                             } else {
                                 System.err.println("Warning: Invalid direction or lane index found in DOM: " + dir + ", " + laneIndex);
                                 laneDiv.removeAll(); // Clear potentially invalid lane
                             }
                         } catch (IllegalArgumentException | NullPointerException e) {
                             System.err.println("Error parsing lane attributes or finding data: " + e.getMessage());
                             laneDiv.removeAll(); // Clear potentially invalid lane
                         }
                     });
             });
    }

    // REMOVE the duplicate updateCarsForApproach(HorizontalLayout ...) method entirely.

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        updateVisuals(); // Refresh visuals when the view is displayed/attached
    }
}