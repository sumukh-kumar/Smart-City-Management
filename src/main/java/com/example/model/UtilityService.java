// Update the package declaration to match the new location
package com.example.model;

// PowerReading is now in the same package, no import needed
// import com.example.model.PowerReading; // Remove or comment out this line

// Import necessary SQL classes
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth; // Ensure YearMonth is imported
import java.time.format.DateTimeFormatter;
// import java.time.temporal.TemporalAdjusters; // Import for finding first day of month - Not explicitly needed with YearMonth.atDay(1)
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
// Remove unused imports related to placeholder data
// import java.util.Random;

// Import Dotenv
import io.github.cdimascio.dotenv.Dotenv;
// Keep the import for DotenvException for now, even if commented out below
// import io.github.cdimascio.dotenv.DotenvException;


public class UtilityService {

    // --- Load Environment Variables ---
    private static final Dotenv dotenv;
    private static final String DB_URL;
    private static final String DB_USER;
    private static final String DB_PASSWORD;
    private static final String TABLE_NAME;

    static {
        try {
            // Load .env file. Assumes the app runs with project root as working directory.
            // ignoreIfMissing() might be safer for deployment, but throws RuntimeException if required keys are null later
            dotenv = Dotenv.configure().ignoreIfMissing().load();

            DB_URL = dotenv.get("DB_URL");
            DB_USER = dotenv.get("DB_USER");
            DB_PASSWORD = dotenv.get("DB_PASSWORD");
            TABLE_NAME = dotenv.get("DB_TABLE");

            // Basic validation
            if (DB_URL == null || DB_USER == null || DB_PASSWORD == null || TABLE_NAME == null) {
                 throw new RuntimeException("Error: One or more required environment variables (DB_URL, DB_USER, DB_PASSWORD, DB_TABLE) are missing. Check .env file or system environment.");
            }

            // Load the MySQL JDBC driver once during class loading
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (RuntimeException e) { // Catch RuntimeException for validation errors from .env
             System.err.println("Error during static initialization (potentially .env loading or validation): " + e.getMessage());
             e.printStackTrace(); // Print stack trace to see original exception
             throw new RuntimeException("Error during static initialization.", e); // Re-throw
        } catch (ClassNotFoundException e) {
            // In a real app, handle this more gracefully (logging, custom exception)
            throw new RuntimeException("Error: MySQL JDBC Driver not found.", e);
        } catch (Exception e) { // Add a general catch-all just in case
            System.err.println("Unexpected error during static initialization: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Unexpected error during static initialization.", e);
        }
    }


    // Remove placeholder data storage
    // private List<PowerReading> placeholderData;

    // Keep singleton pattern for now as views might depend on it
    private static UtilityService instance;

    private UtilityService() {
        // Constructor is now empty, initialization happens in static block
    }

    public static synchronized UtilityService getInstance() {
        if (instance == null) {
            instance = new UtilityService();
        }
        return instance;
    }

    // Remove data generation logic
    // private void generatePlaceholderData() { ... }


    // Helper method to get a database connection
    private Connection getConnection() throws SQLException {
        // Use the loaded static final variables
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }


    // --- Business Logic Methods (Updated for DB) ---

    /**
     * Gets the latest power reading from the database.
     * @return Optional containing the latest PowerReading, or empty if no data.
     */
    public Optional<PowerReading> getLatestReading() {
        // Query uses the loaded static final TABLE_NAME
        String sql = "SELECT id, reading_date, power_consumed, fault_detected FROM " + TABLE_NAME +
                     " ORDER BY reading_date DESC, id DESC LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                // Extract data from ResultSet
                int id = rs.getInt("id");
                LocalDate date = rs.getDate("reading_date").toLocalDate();
                double powerConsumed = rs.getDouble("power_consumed");
                boolean faultDetected = rs.getBoolean("fault_detected");

                // Create PowerReading object
                PowerReading latestReading = new PowerReading(id, date, powerConsumed, faultDetected);
                return Optional.of(latestReading);
            } else {
                // No records found
                return Optional.empty();
            }

        } catch (SQLException e) {
            // Log the error in a real application
            e.printStackTrace();
            // Return empty or throw a custom exception depending on desired error handling
            return Optional.empty();
        }
    }

    /**
     * Generates a report string for the specified month by querying the database.
     * @param month The YearMonth to generate the report for.
     * @return String containing the formatted report, or a message indicating no data/error.
     */
    public String generateMonthlyReport(YearMonth month) {
        // Calculate start and end dates for the given month
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        // Query uses the loaded static final TABLE_NAME
        String sql = "SELECT reading_date, power_consumed, fault_detected FROM " + TABLE_NAME +
                     " WHERE reading_date BETWEEN ? AND ?";

        List<PowerReading> monthData = new ArrayList<>(); // Keep list to count days easily
        double totalConsumption = 0;
        long faultCount = 0;
        int daysRecorded = 0; // Use int for days count

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set query parameters
            pstmt.setDate(1, java.sql.Date.valueOf(startDate));
            pstmt.setDate(2, java.sql.Date.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Extract data - ID not strictly needed for report, but could fetch if desired
                    LocalDate date = rs.getDate("reading_date").toLocalDate();
                    double powerConsumed = rs.getDouble("power_consumed");
                    boolean faultDetected = rs.getBoolean("fault_detected");

                    // Accumulate data for report
                    totalConsumption += powerConsumed;
                    if (faultDetected) {
                        faultCount++;
                    }
                    // We don't actually need the PowerReading objects here anymore, just the count
                    // monthData.add(new PowerReading(0, date, powerConsumed, faultDetected));
                    daysRecorded++; // Increment day count directly
                }
            }

        } catch (SQLException e) {
            // Log the error
            e.printStackTrace();
            return "Error generating report: Database query failed.";
        }

        // --- Report Generation (same logic as before, using fetched data) ---
        if (daysRecorded == 0) { // Check daysRecorded instead of monthData.isEmpty()
            return "No data available for " + month.format(DateTimeFormatter.ofPattern("MMMM yyyy")) + ".";
        }

        double averageConsumption = totalConsumption / daysRecorded;

        // --- Update Statistics Table ---
        // Call the new method to insert or update the stats table
        upsertMonthlyStats(month, totalConsumption, faultCount, daysRecorded, averageConsumption);
        // --- End Update ---


        // Format the report string (remains the same)
        return String.format("Power Consumption Report for %s:\n" +
                             "--------------------------------------------------\n" +
                             "Total Days Recorded: %d\n" +
                             "Total Consumption: %.2f kWh\n" +
                             "Average Daily Consumption: %.2f kWh\n" +
                             "Total Fault Days: %d\n" +
                             "--------------------------------------------------",
                             month.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                             daysRecorded, // Use daysRecorded
                             totalConsumption,
                             averageConsumption,
                             faultCount);
    }

    /**
     * Inserts or updates a record in the power_stats table for the given month.
     * This acts as our "Update" operation in CRUD.
     *
     * @param month The month being summarized.
     * @param totalConsumption Total power consumed.
     * @param faultCount Number of days with faults.
     * @param daysRecorded Number of days data was recorded for.
     * @param averageConsumption Calculated average consumption.
     */
    private void upsertMonthlyStats(YearMonth month, double totalConsumption, long faultCount, int daysRecorded, double averageConsumption) {
        String yearMonthStr = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        // Use MySQL's INSERT ... ON DUPLICATE KEY UPDATE syntax for upsert
        String upsertSql = "INSERT INTO power_stats (`year_month`, `total_consumption`, `fault_count`, `days_recorded`, `average_consumption`, `last_updated`) "
                  + "VALUES (?, ?, ?, ?, ?, NOW()) "
                  + "ON DUPLICATE KEY UPDATE "
                  + "`total_consumption` = VALUES(`total_consumption`), "
                  + "`fault_count` = VALUES(`fault_count`), "
                  + "`days_recorded` = VALUES(`days_recorded`), "
                  + "`average_consumption` = VALUES(`average_consumption`), "
                  + "`last_updated` = NOW();";



        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(upsertSql)) {

            pstmt.setString(1, yearMonthStr);
            pstmt.setDouble(2, totalConsumption);
            pstmt.setLong(3, faultCount);
            pstmt.setInt(4, daysRecorded);
            pstmt.setDouble(5, averageConsumption);

            int rowsAffected = pstmt.executeUpdate();
            // Optional: Log if a row was inserted (1) or updated (2)
            // System.out.println("Upserted stats for " + yearMonthStr + ". Rows affected: " + rowsAffected);

        } catch (SQLException e) {
            // Log the error, but don't stop report generation if stats update fails
            System.err.println("Error updating power_stats table for month " + yearMonthStr + ": " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Finds the latest month with data in the database and generates a report for it.
     *
     * @return A string containing the report for the latest month, or a message
     * indicating no data was found.
     */
    public String generateLatestMonthlyReport() {
        String findLatestDateSql = "SELECT MAX(reading_date) FROM " + TABLE_NAME;
        LocalDate latestDate = null;

        // Step 1: Find the latest date in the table
        try (Connection conn = getConnection();
             PreparedStatement pstmtLatest = conn.prepareStatement(findLatestDateSql);
             ResultSet rs = pstmtLatest.executeQuery()) {

            if (rs.next()) {
                java.sql.Date sqlDate = rs.getDate(1);
                if (sqlDate != null) {
                    latestDate = sqlDate.toLocalDate();
                } else {
                    // Table is empty
                    return "No data found in the table. Cannot generate report.";
                }
            } else {
                   // Should not happen if MAX() is used, but handle defensively
                   return "No data found in the table. Cannot generate report.";
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log error
            return "Error finding the latest date for report generation: " + e.getMessage();
        }

        // Step 2: Determine the YearMonth from the latest date
        YearMonth latestMonth = YearMonth.from(latestDate);

        // Step 3: Call the existing report generation method for that month
        return generateMonthlyReport(latestMonth);
    }


    /**
     * Deletes all utility readings recorded before the start of the month
     * corresponding to the most recent reading date in the database.
     *
     * @return A string message indicating the result (success with count or error).
     */
    public String deleteReadingsBeforeLatestMonth() {
        String findLatestDateSql = "SELECT MAX(reading_date) FROM " + TABLE_NAME;
        LocalDate latestDate = null;

        // Step 1: Find the latest date in the table
        try (Connection conn = getConnection();
             PreparedStatement pstmtLatest = conn.prepareStatement(findLatestDateSql);
             ResultSet rs = pstmtLatest.executeQuery()) {

            if (rs.next()) {
                java.sql.Date sqlDate = rs.getDate(1);
                if (sqlDate != null) {
                    latestDate = sqlDate.toLocalDate();
                } else {
                    // Table is empty or no dates recorded
                    return "No data found in the table. Nothing to delete.";
                }
            } else {
                // Should not happen with MAX(), but handle defensively
                return "No data found in the table. Nothing to delete.";
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log error
            return "Error finding the latest date for deletion: " + e.getMessage();
        }

        // Step 2: Determine the date threshold (start of the latest month)
        LocalDate deleteBeforeDate = YearMonth.from(latestDate).atDay(1);

        // If the latest date is already the first day of the month, there's nothing older *before* the start of that month
        if (latestDate.equals(deleteBeforeDate)) {
             return "Latest data is from the first day of the month. No older data to delete before this month.";
        }

        // Step 3: Execute the DELETE query
        // Delete all records where reading_date is strictly BEFORE the start of the latest month
        String deleteSql = "DELETE FROM " + TABLE_NAME + " WHERE reading_date < ?";
        int rowsDeleted = 0;

        try (Connection conn = getConnection();
             PreparedStatement pstmtDelete = conn.prepareStatement(deleteSql)) {

            pstmtDelete.setDate(1, java.sql.Date.valueOf(deleteBeforeDate));

            rowsDeleted = pstmtDelete.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace(); // Log error
            return "Error deleting old data: " + e.getMessage();
        }

        return String.format("Successfully deleted %d reading(s) before %s.",
                             rowsDeleted,
                             deleteBeforeDate.format(DateTimeFormatter.ISO_DATE));
    }


    // --- Add methods for other CRUD operations as needed ---
    // Example: Find readings with faults in the last N days
    public List<PowerReading> findRecentFaults(int days) {
        List<PowerReading> faultReadings = new ArrayList<>();
        LocalDate sinceDate = LocalDate.now().minusDays(days);
        // Query uses the loaded static final TABLE_NAME
        String sql = "SELECT id, reading_date, power_consumed, fault_detected FROM " + TABLE_NAME +
                     " WHERE fault_detected = true AND reading_date >= ? ORDER BY reading_date DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, java.sql.Date.valueOf(sinceDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    faultReadings.add(new PowerReading(
                         rs.getInt("id"),
                         rs.getDate("reading_date").toLocalDate(),
                         rs.getDouble("power_consumed"),
                         rs.getBoolean("fault_detected")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log error
        }
        return faultReadings;
    }


    // Example: Update operation (Mark a fault as acknowledged - requires adding 'fault_acknowledged' column to DB)
    /*
    public boolean acknowledgeFault(int readingId) {
        // Assumes a 'fault_acknowledged BOOLEAN DEFAULT false' column exists
        // Query uses the loaded static final TABLE_NAME
        String sql = "UPDATE " + TABLE_NAME + " SET fault_acknowledged = true WHERE id = ? AND fault_detected = true";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, readingId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Return true if update was successful
        } catch (SQLException e) {
            e.printStackTrace(); // Log error
            return false;
        }
    }
    */
}

// Assuming PowerReading class exists in the same package (com.example.model)
// public class PowerReading {
//     private int id;
//     private LocalDate readingDate;
//     private double powerConsumed;
//     private boolean faultDetected;

//     public PowerReading(int id, LocalDate readingDate, double powerConsumed, boolean faultDetected) {
//         this.id = id;
//         this.readingDate = readingDate;
//         this.powerConsumed = powerConsumed;
//         this.faultDetected = faultDetected;
//     }

//     // Getters
//     public int getId() { return id; }
//     public LocalDate getReadingDate() { return readingDate; }
//     public double getPowerConsumed() { return powerConsumed; }
//     public boolean isFaultDetected() { return faultDetected; }

//     @Override
//     public String toString() {
//         return "PowerReading{" +
//                "id=" + id +
//                ", readingDate=" + readingDate +
//                ", powerConsumed=" + powerConsumed +
//                ", faultDetected=" + faultDetected +
//                '}';
//     }
// }