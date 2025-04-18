package com.example;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("safety") // Route for this view
public class PublicSafetyManagementView extends VerticalLayout {

    public PublicSafetyManagementView() {
        add(new H2("Public Safety Management"));
        add(new Paragraph("Placeholder content for public safety features (emergency services, surveillance)."));
        // Add specific components for this view later
        setAlignItems(Alignment.CENTER);
    }
}