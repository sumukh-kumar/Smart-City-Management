// Removed package declaration as it's outside src/main/java

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.TimeUnit;

// Import Dotenv
import io.github.cdimascio.dotenv.Dotenv;
// import io.github.cdimascio.dotenv.DotenvException; // Commented out as requested


// Renamed class
public class UtilityDataGenerator {

    // Remove hardcoded DB constants
    // private static final String DB_URL = ...
    // private static final String DB_USER = ...
    // private static final String DB_PASSWORD = ...
    // private static final String TABLE_NAME = ...


    // --- Data Generation Parameters ---
    private static final double MIN_POWER = 10.0;
    private static final double MAX_POWER_RANGE = 5.0; // Max power = MIN_POWER + MAX_POWER_RANGE
    private static final int FAULT_CHANCE_PERCENT = 5; // 5% chance of fault

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = null;
        try {
            // Load .env file from the current directory (project root)
            dotenv = Dotenv.configure().load();
        // } catch (DotenvException e) { // Original catch block
        } catch (Exception e) { // Catch general Exception instead
            System.err.println("Error loading .env file. Make sure it exists in the project root.");
            // Consider logging the specific exception type if needed for debugging
            // System.err.println("Exception type: " + e.getClass().getName());
            System.err.println("Details: " + e.getMessage());
            System.exit(1); // Exit if config is missing
        }

        // Get DB config from Dotenv
        final String dbUrl = dotenv.get("DB_URL");
        final String dbUser = dotenv.get("DB_USER");
        final String dbPassword = dotenv.get("DB_PASSWORD");
        final String tableName = dotenv.get("DB_TABLE");

        // Basic validation
        if (dbUrl == null || dbUser == null || dbPassword == null || tableName == null) {
            System.err.println("Error: One or more required environment variables (DB_URL, DB_USER, DB_PASSWORD, DB_TABLE) are missing in the .env file.");
            System.exit(1);
        }


        Random random = new Random();
        // Start simulation date remains the same logic
        LocalDate currentDate = LocalDate.now().minusMonths(3);

        System.out.println("Starting utility data generator...");
        System.out.println("Connecting to database: " + dbUrl); // Use loaded variable

        // Load the MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver not found. Make sure it's in the classpath.");
            e.printStackTrace();
            return;
        }

        // SQL Statements remain the same, using loaded tableName
        String insertSql = "INSERT INTO " + tableName + " (reading_date, power_consumed, fault_detected) VALUES (?, ?, ?)";
        String deleteSql = "DELETE FROM " + tableName + " WHERE reading_date < DATE_SUB(CURDATE(), INTERVAL 1 YEAR)";


        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) { // Use loaded variables
            System.out.println("Database connection successful.");
            conn.setAutoCommit(false); // Use transactions

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                 PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {

                int deleteCounter = 0;

                while (true) {
                    // ... (data generation logic remains the same) ...
                    double dailyConsumption = MIN_POWER + random.nextDouble() * MAX_POWER_RANGE;
                    boolean fault = random.nextInt(100) < FAULT_CHANCE_PERCENT;

                    // ... (prepare insert statement remains the same) ...
                    insertStmt.setDate(1, java.sql.Date.valueOf(currentDate));
                    insertStmt.setDouble(2, dailyConsumption);
                    insertStmt.setBoolean(3, fault);

                    insertStmt.executeUpdate();
                    System.out.printf("Inserted: Date: %s, Power: %.2f kWh, Fault: %s%n",
                            currentDate, dailyConsumption, fault);

                    // ... (periodic delete logic remains the same) ...
                    deleteCounter++;
                    if (deleteCounter >= 100) {
                        int deletedRows = deleteStmt.executeUpdate();
                        if (deletedRows > 0) {
                            System.out.println("Performed cleanup: Deleted " + deletedRows + " records older than 1 year.");
                        }
                        deleteCounter = 0;
                    }

                    conn.commit(); // Commit transaction

                    // ... (move to next day and sleep remains the same) ...
                    currentDate = currentDate.plusDays(1);
                    TimeUnit.SECONDS.sleep(1);

                }
            } catch (SQLException e) {
                System.err.println("Error during statement execution. Rolling back transaction.");
                conn.rollback();
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.err.println("Data generator interrupted.");
                Thread.currentThread().interrupt();
            }

        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}