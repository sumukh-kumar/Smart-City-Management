package com.example;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("environment") // Route for this view
public class EnvironmentalManagementView extends VerticalLayout {

    public EnvironmentalManagementView() {
        add(new H2("Environmental Management"));
        add(new Paragraph("Placeholder content for environmental monitoring (air quality, waste management)."));
        // Add specific components for this view later
        setAlignItems(Alignment.CENTER);
    }
}