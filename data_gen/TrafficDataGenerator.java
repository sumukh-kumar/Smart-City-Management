import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

// Import Dotenv
import io.github.cdimascio.dotenv.Dotenv;

public class TrafficDataGenerator {

    // --- Data Generation Parameters ---
    private static final List<String> SIGNAL_LOCATIONS = Arrays.asList(
            "Main St & 1st Ave",
            "Oak St & Highway 101",
            "Maple Dr & Park Rd",
            "Elm St & 5th Ave"
    );
    private static final int LANES_PER_SIGNAL = 4;
    private static final int MAX_VEHICLES_PER_LANE = 30; // Max vehicles detected in a lane at once
    private static final int PARKING_SPOT_UPDATE_CHANCE_PERCENT = 10; // 10% chance each cycle to update a parking spot

    // --- Database Table Names (read from .env) ---
    private static String TRAFFIC_TABLE_NAME = "traffic_readings"; // Default, will be overridden by .env
    private static String PARKING_TABLE_NAME = "parking_spots";   // Default, will be overridden by .env

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = null;
        try {
            dotenv = Dotenv.configure().load();
        } catch (Exception e) {
            System.err.println("Error loading .env file. Make sure it exists in the project root.");
            System.err.println("Details: " + e.getMessage());
            System.exit(1);
        }

        // Get DB config from Dotenv
        final String dbUrl = dotenv.get("DB_URL");
        final String dbUser = dotenv.get("DB_USER");
        final String dbPassword = dotenv.get("DB_PASSWORD");
        // Get table names from .env, use defaults if not found
        TRAFFIC_TABLE_NAME = dotenv.get("DB_TRAFFIC_TABLE", TRAFFIC_TABLE_NAME);
        PARKING_TABLE_NAME = dotenv.get("DB_PARKING_TABLE", PARKING_TABLE_NAME);


        // Basic validation
        if (dbUrl == null || dbUser == null || dbPassword == null) {
            System.err.println("Error: One or more required environment variables (DB_URL, DB_USER, DB_PASSWORD) are missing in the .env file.");
            System.exit(1);
        }

        Random random = new Random();

        System.out.println("Starting traffic data generator...");
        System.out.println("Target Traffic Table: " + TRAFFIC_TABLE_NAME);
        System.out.println("Target Parking Table: " + PARKING_TABLE_NAME);
        System.out.println("Connecting to database: " + dbUrl);

        // Load the MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver not found. Make sure it's in the classpath.");
            e.printStackTrace();
            return;
        }

        // SQL Statements
        String insertTrafficSql = "INSERT INTO " + TRAFFIC_TABLE_NAME +
                                  " (signal_location, lane_id, vehicle_count, reading_timestamp) VALUES (?, ?, ?, ?)";
        // SQL to update a random parking spot's status
        String updateParkingSql = "UPDATE " + PARKING_TABLE_NAME +
                                  " SET is_occupied = ?, last_updated = CURRENT_TIMESTAMP" +
                                  " WHERE spot_id = (SELECT spot_id FROM (SELECT spot_id FROM " + PARKING_TABLE_NAME + " ORDER BY RAND() LIMIT 1) AS temp)";
        // SQL to delete old traffic readings (e.g., older than 1 day - adjust interval as needed)
        String deleteOldTrafficSql = "DELETE FROM " + TRAFFIC_TABLE_NAME +
                                     " WHERE reading_timestamp < DATE_SUB(NOW(), INTERVAL 1 DAY)";


        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            System.out.println("Database connection successful.");
            conn.setAutoCommit(false); // Use transactions

            try (PreparedStatement insertTrafficStmt = conn.prepareStatement(insertTrafficSql);
                 PreparedStatement updateParkingStmt = conn.prepareStatement(updateParkingSql);
                 PreparedStatement deleteTrafficStmt = conn.prepareStatement(deleteOldTrafficSql)) {

                int deleteCounter = 0; // Counter for periodic deletion

                while (true) {
                    Instant now = Instant.now();
                    Timestamp currentTimestamp = Timestamp.from(now);

                    // --- Generate Traffic Data ---
                    for (String location : SIGNAL_LOCATIONS) {
                        for (int lane = 1; lane <= LANES_PER_SIGNAL; lane++) {
                            int vehicleCount = random.nextInt(MAX_VEHICLES_PER_LANE + 1); // 0 to MAX_VEHICLES_PER_LANE

                            insertTrafficStmt.setString(1, location);
                            insertTrafficStmt.setInt(2, lane);
                            insertTrafficStmt.setInt(3, vehicleCount);
                            insertTrafficStmt.setTimestamp(4, currentTimestamp);

                            insertTrafficStmt.addBatch(); // Add to batch for efficiency

                            System.out.printf("Prepared: Loc: %s, Lane: %d, Count: %d, Time: %s%n",
                                    location, lane, vehicleCount, currentTimestamp);
                        }
                    }
                    int[] trafficResults = insertTrafficStmt.executeBatch(); // Execute batch insert
                    System.out.println("Inserted " + trafficResults.length + " traffic readings.");

                    // --- Simulate Parking Spot Update ---
                    if (random.nextInt(100) < PARKING_SPOT_UPDATE_CHANCE_PERCENT) {
                        boolean newOccupiedStatus = random.nextBoolean();
                        updateParkingStmt.setBoolean(1, newOccupiedStatus);
                        int updatedSpots = updateParkingStmt.executeUpdate();
                        if (updatedSpots > 0) {
                            System.out.println("Updated a random parking spot status to: " + newOccupiedStatus);
                        } else {
                             System.out.println("Attempted parking spot update, but no spots found or error occurred.");
                        }
                    }

                    // --- Periodic Cleanup ---
                    deleteCounter++;
                    // Run delete every ~50 cycles (adjust as needed)
                    if (deleteCounter >= 50) {
                        int deletedRows = deleteTrafficStmt.executeUpdate();
                        if (deletedRows > 0) {
                            System.out.println("Performed cleanup: Deleted " + deletedRows + " old traffic records.");
                        }
                        deleteCounter = 0;
                    }


                    conn.commit(); // Commit transaction

                    // Wait before generating next batch of data
                    TimeUnit.SECONDS.sleep(5); // Generate data every 5 seconds (adjust as needed)

                }
            } catch (SQLException e) {
                System.err.println("Error during statement execution. Rolling back transaction.");
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error during rollback: " + ex.getMessage());
                }
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