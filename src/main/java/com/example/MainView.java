package com.example;

// Removed unused imports: Item, InventoryService, Grid, NumberField, TextField
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility; // Import for utility classes

/**
 * The main view acting as the entry point and navigation hub.
 */
@Route("") // Map this view to the root path
public class MainView extends VerticalLayout {

    public MainView() {
        // --- Title ---
        H1 title = new H1("Smart City Management");
        title.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.CENTER, LumoUtility.Margin.Top.MEDIUM); // Center the title

        // --- Navigation Buttons ---
        Button trafficButton = new Button("Traffic Management", e -> UI.getCurrent().navigate("traffic"));
        Button utilityButton = new Button("Utility Management", e -> UI.getCurrent().navigate("utility"));
        Button safetyButton = new Button("Public Safety Management", e -> UI.getCurrent().navigate("safety"));
        Button environmentButton = new Button("Environmental Management", e -> UI.getCurrent().navigate("environment"));

        // Style buttons for consistent width
        for (Button btn : new Button[]{trafficButton, utilityButton, safetyButton, environmentButton}) {
            btn.setWidth("250px"); // Set a fixed width or use CSS classes
            btn.addClassName(LumoUtility.Margin.Vertical.SMALL); // Add some vertical spacing
        }

        VerticalLayout buttonLayout = new VerticalLayout(
                trafficButton,
                utilityButton,
                safetyButton,
                environmentButton
        );
        buttonLayout.setPadding(false); // Remove default padding
        buttonLayout.setSpacing(false); // Remove default spacing if needed
        buttonLayout.setWidth(null); // Allow the layout to shrink to content width
        buttonLayout.addClassName(LumoUtility.Margin.Top.LARGE); // Add margin above buttons

        // --- Main Layout ---
        setAlignItems(Alignment.CENTER); // Center align items horizontally in the main layout
        add(title, buttonLayout);

        setSizeFull(); // Ensure the main layout takes full height
    }
}
