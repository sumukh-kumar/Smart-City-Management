package com.example;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("traffic") // Route for this view
public class TrafficManagementView extends VerticalLayout {

    public TrafficManagementView() {
        add(new H2("Traffic Management"));
        add(new Paragraph("Placeholder content for traffic management features."));
        // Add specific components for this view later
        setAlignItems(Alignment.CENTER);
    }
}