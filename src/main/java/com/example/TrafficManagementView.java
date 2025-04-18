package com.example;

import com.example.model.TrafficSignal;
import com.example.service.TrafficService; // Import the service
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("traffic") // Route for this view
@PageTitle("Traffic Management")
public class TrafficManagementView extends VerticalLayout {

    private final TrafficService trafficService; // Use the singleton service
    private Grid<TrafficSignal> grid = new Grid<>(TrafficSignal.class);
    private Button simulateButton;
    private Button backButton;

    public TrafficManagementView() {
        // Get the singleton instance of the service
        this.trafficService = TrafficService.getInstance();

        addClassName("traffic-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        // Optional: Add background styling
        getStyle()
                .set("background-image", "url(images/trafficmgmet_bg.png)")
                .set("background-size", "cover")
                .set("background-position", "center")
                .set("background-repeat", "no-repeat")
                .set("min-height", "100vh");

        add(new H2("Traffic Management System"));

        configureGrid();
        configureButtons();

        // Layout components
        HorizontalLayout buttonLayout = new HorizontalLayout(simulateButton, backButton);
        buttonLayout.setSpacing(true);

        VerticalLayout content = new VerticalLayout(grid, buttonLayout);
        content.setAlignItems(Alignment.CENTER);
        content.getStyle()
           .set("background-color", "rgba(255, 255, 255, 0.8)")
           .set("padding", "20px")
           .set("border-radius", "10px");
        content.setWidth("70%"); // Adjust width as needed

        add(content);
        setJustifyContentMode(JustifyContentMode.CENTER);

        updateGrid(); // Initial data load
    }

    private void configureGrid() {
        grid.addClassName("traffic-grid");
        grid.setSizeFull();
        // Configure columns explicitly
        grid.setColumns("id", "location", "vehicleCount", "signalDuration");
        grid.getColumnByKey("signalDuration").setHeader("Duration (s)"); // Rename header

        grid.getColumns().forEach(col -> col.setAutoWidth(true).setSortable(true));
    }

    private void configureButtons() {
        simulateButton = new Button("Simulate Traffic Change");
        simulateButton.addClickListener(e -> {
            trafficService.simulateTrafficChange(); // Call the service method
            updateGrid(); // Refresh the grid to show changes
        });

        backButton = new Button("Back to Main Menu");
        // Assuming the main menu is at the root ""
        backButton.addClickListener(e -> backButton.getUI().ifPresent(ui -> ui.navigate("")));
    }

    private void updateGrid() {
        List<TrafficSignal> signals = trafficService.getAllSignals();
        grid.setItems(signals); // Update the grid data
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        // Refresh grid when the view is displayed/attached
        updateGrid();
    }
}