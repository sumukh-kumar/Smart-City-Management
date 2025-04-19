// Removed package declaration as it's outside src/main/java

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.TimeUnit;

// Renamed class
public class UtilityDataGenerator {

    // --- Database Configuration ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/smart_city_db"; // Replace smart_city_db with your DB name
    private static final String DB_USER = "root"; // Replace with your MySQL username
    private static final String DB_PASSWORD = "password"; // Replace with your MySQL password
    private static final String TABLE_NAME = "power_readings"; // Replace with your table name

    // --- Data Generation Parameters ---
    private static final double MIN_POWER = 10.0;
    private static final double MAX_POWER_RANGE = 5.0; // Max power = MIN_POWER + MAX_POWER_RANGE
    private static final int FAULT_CHANCE_PERCENT = 5; // 5% chance of fault

    public static void main(String[] args) {
        Random random = new Random();
        // Start simulation date remains the same logic
        LocalDate currentDate = LocalDate.now().minusMonths(3);

        System.out.println("Starting utility data generator...");
        System.out.println("Connecting to database: " + DB_URL);

        // Load the MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver not found. Make sure it's in the classpath.");
            e.printStackTrace();
            return;
        }

        // SQL Statements remain the same
        String insertSql = "INSERT INTO " + TABLE_NAME + " (reading_date, power_consumed, fault_detected) VALUES (?, ?, ?)";
        String deleteSql = "DELETE FROM " + TABLE_NAME + " WHERE reading_date < DATE_SUB(CURDATE(), INTERVAL 1 YEAR)";


        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
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