package com.example;

import com.example.controller.EnvironmentalController;
import com.example.model.EnvironmentalService;
import com.example.model.EnvironmentalService.AirQualityReading;
import com.example.model.EnvironmentalService.NoiseLevelReading;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Route("environment") // Route for this view
public class EnvironmentalManagementView extends VerticalLayout {

    // UI Components
    private final Grid<AirQualityReading> airQualityGrid;
    private final Grid<NoiseLevelReading> noiseLevelGrid;
    private final TextArea alertsArea;
    private final TextArea reportArea;
    private final Button refreshButton;
    private final Button alertsButton;
    private final Button reportButton;
    private final Button deleteButton;
    private final ComboBox<String> locationComboBox;
    private final IntegerField daysField;
    
    // Tabs for different sections
    private final VerticalLayout airQualityLayout;
    private final VerticalLayout noiseLevelLayout;
    private final VerticalLayout reportsLayout;

    // Date formatter for display
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EnvironmentalManagementView() {
        // Main title
        H2 title = new H2("Environmental Management");
        Paragraph description = new Paragraph("Monitor air quality and noise pollution levels across the city.");
        
        // Create tabs for different sections
        Tab airQualityTab = new Tab("Air Quality Monitoring");
        Tab noiseLevelTab = new Tab("Noise Pollution Tracking");
        Tab reportsTab = new Tab("Reports & Alerts");
        Tabs tabs = new Tabs(airQualityTab, noiseLevelTab, reportsTab);
        tabs.setWidthFull();
        
        // Create layouts for each tab
        airQualityLayout = new VerticalLayout();
        noiseLevelLayout = new VerticalLayout();
        reportsLayout = new VerticalLayout();
        
        // Initially hide noise and reports layouts
        noiseLevelLayout.setVisible(false);
        reportsLayout.setVisible(false);
        
        // --- Air Quality Monitoring Components ---
        airQualityGrid = new Grid<>(AirQualityReading.class);
        airQualityGrid.setColumns("location", "timestamp", "pm25Level", "pm10Level", "ozoneLevel", "qualityIndex");
        airQualityGrid.getColumnByKey("timestamp").setHeader("Time");
        airQualityGrid.getColumnByKey("location").setHeader("Location");
        airQualityGrid.getColumnByKey("pm25Level").setHeader("PM2.5 (μg/m³)");
        airQualityGrid.getColumnByKey("pm10Level").setHeader("PM10 (μg/m³)");
        airQualityGrid.getColumnByKey("ozoneLevel").setHeader("Ozone (ppb)");
        airQualityGrid.getColumnByKey("qualityIndex").setHeader("Quality");
        
        // Format the timestamp column
        airQualityGrid.getColumnByKey("timestamp").setRenderer(
            new com.vaadin.flow.data.renderer.TextRenderer<>(
                reading -> reading.getTimestamp().format(dtf)
            )
        );
        
        // Customize the quality index column to show colors
        airQualityGrid.getColumnByKey("qualityIndex").setRenderer(
            new com.vaadin.flow.data.renderer.ComponentRenderer<>(reading -> {
                com.vaadin.flow.component.html.Span span = new com.vaadin.flow.component.html.Span(reading.getQualityIndex());
                
                switch (reading.getQualityIndex()) {
                    case "Good":
                        span.getStyle().set("color", "green");
                        break;
                    case "Moderate":
                        span.getStyle().set("color", "orange");
                        break;
                    case "Poor":
                        span.getStyle().set("color", "red");
                        break;
                    case "Hazardous":
                        span.getStyle().set("color", "darkred");
                        span.getStyle().set("font-weight", "bold");
                        break;
                }
                
                return span;
            })
        );
        
        // --- Noise Level Monitoring Components ---
        noiseLevelGrid = new Grid<>(NoiseLevelReading.class);
        noiseLevelGrid.setColumns("location", "timestamp", "decibelLevel", "zoneType", "exceedsLimit");
        noiseLevelGrid.getColumnByKey("timestamp").setHeader("Time");
        noiseLevelGrid.getColumnByKey("location").setHeader("Location");
        noiseLevelGrid.getColumnByKey("decibelLevel").setHeader("Decibel Level (dB)");
        noiseLevelGrid.getColumnByKey("zoneType").setHeader("Zone Type");
        noiseLevelGrid.getColumnByKey("exceedsLimit").setHeader("Exceeds Limit");
        
        // Format the timestamp column
        noiseLevelGrid.getColumnByKey("timestamp").setRenderer(
            new com.vaadin.flow.data.renderer.TextRenderer<>(
                reading -> reading.getTimestamp().format(dtf)
            )
        );
        
        // Customize the exceeds limit column
        noiseLevelGrid.getColumnByKey("exceedsLimit").setRenderer(
            new com.vaadin.flow.data.renderer.ComponentRenderer<>(reading -> {
                com.vaadin.flow.component.html.Span span = new com.vaadin.flow.component.html.Span(
                    reading.isExceedsLimit() ? "YES" : "NO"
                );
                
                if (reading.isExceedsLimit()) {
                    span.getStyle().set("color", "red");
                    span.getStyle().set("font-weight", "bold");
                } else {
                    span.getStyle().set("color", "green");
                }
                
                return span;
            })
        );
        
        // --- Reports & Alerts Components ---
        alertsArea = new TextArea("Air Quality Alerts");
        alertsArea.setWidthFull();
        alertsArea.setReadOnly(true);
        alertsArea.setHeight("200px");
        
        reportArea = new TextArea("Location Report");
        reportArea.setWidthFull();
        reportArea.setReadOnly(true);
        reportArea.setHeight("300px");
        
        // --- Control Components ---
        refreshButton = new Button("Refresh Data");
        
        // Location selector for reports
        locationComboBox = new ComboBox<>("Location");
        locationComboBox.setPlaceholder("Select location");
        locationComboBox.setWidthFull();
        
        // Days input for reports and alerts
        daysField = new IntegerField("Days to Include");
        daysField.setValue(7);
        daysField.setMin(1);
        daysField.setMax(30);
        daysField.setStep(1);
        daysField.setWidthFull();
        
        // Buttons for reports and alerts
        alertsButton = new Button("Show Air Quality Alerts");
        reportButton = new Button("Generate Location Report");
        
        // Delete old data button
        deleteButton = new Button("Delete Old Data (Keep 30 Days)");
        
        // --- Layout Construction ---
        // Air Quality Tab
        airQualityLayout.add(
            new H3("Current Air Quality Readings"),
            airQualityGrid
        );
        airQualityLayout.setWidthFull();
        
        // Noise Level Tab
        noiseLevelLayout.add(
            new H3("Current Noise Level Readings"),
            noiseLevelGrid
        );
        noiseLevelLayout.setWidthFull();
        
        // Reports Tab
        HorizontalLayout reportControlsLayout = new HorizontalLayout(locationComboBox, daysField);
        reportControlsLayout.setWidthFull();
        
        reportsLayout.add(
            new H3("Environmental Reports & Alerts"),
            alertsButton, alertsArea,
            new Hr(),
            reportControlsLayout,
            reportButton, reportArea
        );
        reportsLayout.setWidthFull();
        
        // Main layout with tabs
        add(
            title, description,
            tabs,
            airQualityLayout, noiseLevelLayout, reportsLayout,
            new Hr(),
            refreshButton,
            deleteButton
        );
        
        // Tab change listener
        tabs.addSelectedChangeListener(event -> {
            // Hide all layouts first
            airQualityLayout.setVisible(false);
            noiseLevelLayout.setVisible(false);
            reportsLayout.setVisible(false);
            
            // Show the selected layout
            Tab selectedTab = event.getSelectedTab();
            if (selectedTab.equals(airQualityTab)) {
                airQualityLayout.setVisible(true);
            } else if (selectedTab.equals(noiseLevelTab)) {
                noiseLevelLayout.setVisible(true);
            } else if (selectedTab.equals(reportsTab)) {
                reportsLayout.setVisible(true);
            }
        });
        
        // Set overall layout properties
        setAlignItems(Alignment.CENTER);
        setWidth("90%");
        getStyle().set("margin", "0 auto");
        
        // Initialize the controller
        new EnvironmentalController(EnvironmentalService.getInstance(), this);
    }
    
    // --- Methods for Controller Interaction ---
    
    public void addRefreshButtonListener(ComponentEventListener<ClickEvent<Button>> listener) {
        refreshButton.addClickListener(listener);
    }
    
    public void addAlertsButtonListener(ComponentEventListener<ClickEvent<Button>> listener) {
        alertsButton.addClickListener(listener);
    }
    
    public void addReportButtonListener(ComponentEventListener<ClickEvent<Button>> listener) {
        reportButton.addClickListener(listener);
    }
    
    public void addDeleteButtonListener(ComponentEventListener<ClickEvent<Button>> listener) {
        deleteButton.addClickListener(listener);
    }
    
    public void updateAirQualityGrid(Map<String, AirQualityReading> readings) {
        airQualityGrid.setItems(readings.values());
        
        // Also update the location combo box for reports
        locationComboBox.setItems(readings.keySet());
        if (!readings.isEmpty() && locationComboBox.getValue() == null) {
            locationComboBox.setValue(readings.keySet().iterator().next());
        }
    }
    
    public void updateNoiseLevelGrid(List<NoiseLevelReading> readings) {
        noiseLevelGrid.setItems(readings);
    }
    
    public void setAlertsContent(String content) {
        alertsArea.setValue(content);
    }
    
    public void setReportContent(String content) {
        reportArea.setValue(content);
    }
    
    public String getSelectedLocation() {
        return locationComboBox.getValue();
    }
    
    public int getSelectedDays() {
        return daysField.getValue();
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