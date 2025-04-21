package com.example;

// Removed imports for TrafficSignal and TrafficService
// import com.example.model.TrafficSignal;
// import com.example.service.TrafficService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
// Removed import for Grid
// import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph; // Keep Paragraph
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

// Removed import for List
// import java.util.List;

@Route("traffic") // Route for this view
@PageTitle("Traffic Management")
public class TrafficManagementView extends VerticalLayout {

    // Removed fields related to TrafficService and Grid
    // private final TrafficService trafficService;
    // private Grid<TrafficSignal> grid = new Grid<>(TrafficSignal.class);
    // private Button simulateButton; // Removed simulate button
    private Button backButton;
    private Paragraph statusMessage; // Keep status message for placeholder

    public TrafficManagementView() {
        // Removed instantiation of TrafficService
        // this.trafficService = TrafficService.getInstance();

        addClassName("traffic-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        // Optional: Add background styling (kept as is)
        getStyle()
                .set("background-image", "url(images/trafficmgmet_bg.png)")
                .set("background-size", "cover")
                .set("background-position", "center")
                .set("background-repeat", "no-repeat")
                .set("min-height", "100vh");

        add(new H2("Traffic Management System"));

        // Initialize the status message paragraph with a placeholder
        statusMessage = new Paragraph("Traffic Management feature is currently under development."); // Updated placeholder text
        statusMessage.getStyle().set("font-style", "italic"); // Optional styling

        // Removed call to configureGrid()
        // configureGrid();
        configureButtons(); // Keep configuring the back button

        // Layout components - removed simulateButton
        HorizontalLayout buttonLayout = new HorizontalLayout(backButton);
        buttonLayout.setSpacing(true);

        // Add the status message to the content layout - removed grid
        VerticalLayout content = new VerticalLayout(statusMessage, buttonLayout);
        content.setAlignItems(Alignment.CENTER);
        content.getStyle()
           .set("background-color", "rgba(255, 255, 255, 0.8)")
           .set("padding", "20px")
           .set("border-radius", "10px");
        content.setWidth("70%"); // Adjust width as needed

        add(content);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Removed initial call to updateGrid()
        // updateGrid();
    }

    // Removed configureGrid method
    // private void configureGrid() { ... }

    private void configureButtons() {
        // Removed simulateButton logic
        // simulateButton = new Button("Simulate Traffic Change");
        // simulateButton.addClickListener(e -> { ... });

        backButton = new Button("Back to Main Menu");
        // Assuming the main menu is at the root ""
        backButton.addClickListener(e -> backButton.getUI().ifPresent(ui -> ui.navigate("")));
    }

    // Removed updateGrid method
    // private void updateGrid() { ... }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        // Removed call to updateGrid()
        // updateGrid();
    }
}