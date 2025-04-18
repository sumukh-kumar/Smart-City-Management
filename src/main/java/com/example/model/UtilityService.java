// Update the package declaration to match the new location
package com.example.model;

// PowerReading is now in the same package, no import needed
// import com.example.model.PowerReading; // Remove or comment out this line

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

// Service class to handle data and business logic
// Now resides in the 'model' package
public class UtilityService {

    // Placeholder data storage - moved from View
    private List<PowerReading> placeholderData;

    // Simulate a singleton pattern or use Spring @Service later
    private static UtilityService instance;

    private UtilityService() {
        generatePlaceholderData();
    }

    public static synchronized UtilityService getInstance() {
        if (instance == null) {
            instance = new UtilityService();
        }
        return instance;
    }

    // Data generation logic - moved from View
    private void generatePlaceholderData() {
        placeholderData = new ArrayList<>();
        Random random = new Random();
        LocalDate startDate = LocalDate.now().minusMonths(2); // Start data from 2 months ago
        LocalDate endDate = LocalDate.now();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            double dailyConsumption = 10 + random.nextDouble() * 5; // Random consumption between 10 and 15 kWh
            boolean fault = random.nextInt(100) < 5; // 5% chance of a fault
            placeholderData.add(new PowerReading(date, dailyConsumption, fault));
        }
        // In a real app, this would fetch from a database (e.g., using JDBC/JPA)
    }

    // --- Business Logic Methods ---

    /**
     * Gets the latest power reading.
     * @return Optional containing the latest PowerReading, or empty if no data.
     */
    public Optional<PowerReading> getLatestReading() {
        if (placeholderData == null || placeholderData.isEmpty()) {
            return Optional.empty();
        }
        // Return the last element
        return Optional.of(placeholderData.get(placeholderData.size() - 1));
    }

    /**
     * Generates a report string for the specified month.
     * @param month The YearMonth to generate the report for.
     * @return String containing the formatted report, or a message indicating no data.
     */
    public String generateMonthlyReport(YearMonth month) {
        if (placeholderData == null) {
             return "Error: Data not initialized.";
        }

        List<PowerReading> monthData = placeholderData.stream()
                .filter(reading -> YearMonth.from(reading.getDate()).equals(month))
                .collect(Collectors.toList());

        if (monthData.isEmpty()) {
            return "No data available for " + month.format(DateTimeFormatter.ofPattern("MMMM yyyy")) + ".";
        }

        double totalConsumption = monthData.stream().mapToDouble(PowerReading::getPowerConsumed).sum();
        double averageConsumption = totalConsumption / monthData.size();
        long faultCount = monthData.stream().filter(PowerReading::isFaultDetected).count();

        return String.format("Power Consumption Report for %s:\n" +
                        "--------------------------------------------------\n" +
                        " - Total Days Recorded: %d\n" +
                        " - Total Power Consumed: %.2f kWh\n" +
                        " - Average Daily Consumption: %.2f kWh\n" +
                        " - Number of Faults Detected: %d\n" +
                        "--------------------------------------------------",
                month.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                monthData.size(),
                totalConsumption,
                averageConsumption,
                faultCount);
    }
}