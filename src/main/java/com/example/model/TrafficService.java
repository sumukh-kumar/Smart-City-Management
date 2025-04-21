package com.example.model;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Import the correct POJO
import com.example.model.JunctionState;
import com.example.model.ParkingSpot;


public class TrafficService {

    // --- Load Environment Variables ---
    private static final Dotenv dotenv;
    private static final String DB_URL;
    private static final String DB_USER;
    private static final String DB_PASSWORD;
    // Update table name constant
    private static final String JUNCTION_TABLE_NAME; // Renamed from TRAFFIC_TABLE_NAME
    private static final String PARKING_TABLE_NAME;

    static {
        try {
            dotenv = Dotenv.configure().ignoreIfMissing().load();

            DB_URL = dotenv.get("DB_URL");
            DB_USER = dotenv.get("DB_USER");
            DB_PASSWORD = dotenv.get("DB_PASSWORD");
            // Use defaults if not found in .env, update default for junction table
            JUNCTION_TABLE_NAME = dotenv.get("DB_JUNCTION_TABLE", "junction_state"); // Updated key and default
            PARKING_TABLE_NAME = dotenv.get("DB_PARKING_TABLE", "parking_spots");

            // Basic validation
            if (DB_URL == null || DB_USER == null || DB_PASSWORD == null) {
                 throw new RuntimeException("Error: Required DB environment variables (DB_URL, DB_USER, DB_PASSWORD) are missing.");
            }
            // Update warning message
            if (JUNCTION_TABLE_NAME == null || PARKING_TABLE_NAME == null) {
                System.err.println("Warning: DB_JUNCTION_TABLE or DB_PARKING_TABLE not found in .env, using defaults.");
            }

            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (RuntimeException | ClassNotFoundException e) {
             System.err.println("Error during static initialization: " + e.getMessage());
             e.printStackTrace();
             throw new RuntimeException("Error during static initialization.", e);
        } catch (Exception e) {
            System.err.println("Unexpected error during static initialization: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Unexpected error during static initialization.", e);
        }
    }

    // --- Singleton Pattern ---
    private static TrafficService instance;

    private TrafficService() {
        // Private constructor
    }

    public static synchronized TrafficService getInstance() {
        if (instance == null) {
            instance = new TrafficService();
        }
        return instance;
    }

    // --- Helper method for DB Connection ---
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // --- Business Logic Methods ---

    /**
     * Gets the latest state for each unique junction.
     * Uses a subquery to find the max last_updated for each junction_id efficiently.
     * @return A Map where the key is junctionId and the value is the latest JunctionState.
     */
    public Map<String, JunctionState> getLatestJunctionStates() {
        Map<String, JunctionState> latestStates = new HashMap<>();
        // Updated SQL query: Removed t1.id from SELECT list
        String sql = String.format(
            "SELECT t1.junction_id, t1.lane_1_vehicles, t1.lane_2_vehicles, t1.lane_3_vehicles, t1.lane_4_vehicles, t1.green_lane_id, t1.last_updated " + // Removed t1.id
            "FROM %s t1 " +
            "INNER JOIN (" +
            "    SELECT junction_id, MAX(last_updated) AS max_last_updated " +
            "    FROM %s " +
            "    GROUP BY junction_id" +
            ") t2 ON t1.junction_id = t2.junction_id AND t1.last_updated = t2.max_last_updated",
            JUNCTION_TABLE_NAME, JUNCTION_TABLE_NAME);

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Create JunctionState object using constructor - Removed id argument
                JunctionState state = new JunctionState(
                        // rs.getInt("id"), // Removed id argument
                        rs.getString("junction_id"),
                        rs.getInt("lane_1_vehicles"),
                        rs.getInt("lane_2_vehicles"),
                        rs.getInt("lane_3_vehicles"),
                        rs.getInt("lane_4_vehicles"),
                        rs.getInt("green_lane_id"),
                        rs.getTimestamp("last_updated")
                );
                latestStates.put(state.getJunctionId(), state);
            }
        } catch (SQLException e) {
            // Log the specific SQL error
            System.err.println("Error fetching latest junction states: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for detailed debugging
        }
        return latestStates;
    }

    /**
     * Gets the current status of all parking spots.
     * @return A List of ParkingSpot objects.
     */
    public List<ParkingSpot> getAllParkingSpots() {
        List<ParkingSpot> spots = new ArrayList<>();
        // This query remains the same as it targets parking_spots
        String sql = String.format("SELECT spot_id, location_description, is_occupied, last_updated FROM %s ORDER BY spot_id", PARKING_TABLE_NAME);

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ParkingSpot spot = new ParkingSpot(
                        rs.getString("spot_id"),
                        rs.getString("location_description"),
                        rs.getBoolean("is_occupied"),
                        rs.getTimestamp("last_updated")
                );
                spots.add(spot);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching parking spots: " + e.getMessage());
            e.printStackTrace();
            // Return empty list or throw exception
        }
        return spots;
    }

    /**
     * Deletes junction state records older than the current day (keeps today's records).
     * @return A string message indicating the result (success with count or error).
     */
    public String deleteOldJunctionStates() {
        // Deletes records where the last_updated is strictly before the beginning of today.
        // Updated table name and column name in query
        String deleteSql = String.format("DELETE FROM %s WHERE last_updated < CURDATE()", JUNCTION_TABLE_NAME); // Use last_updated
        int rowsDeleted = 0;

        try (Connection conn = getConnection();
             PreparedStatement pstmtDelete = conn.prepareStatement(deleteSql)) {

            rowsDeleted = pstmtDelete.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting old junction state data: " + e.getMessage();
        }
        return String.format("Successfully deleted %d old junction state record(s) before today.", rowsDeleted);
    }

    // --- Update Operation Placeholder ---
    // Example: Could be used to manually override a green light (if needed)
    // public boolean setGreenLight(String junctionId, int laneId) {
    //     // Hypothetical: Updates the latest record for a specific junction
    //     String sql = String.format("UPDATE %s SET green_lane_id = ?, timestamp = NOW() WHERE junction_id = ? ORDER BY timestamp DESC LIMIT 1", JUNCTION_TABLE_NAME);
    //     try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
    //         pstmt.setInt(1, laneId);
    //         pstmt.setString(2, junctionId);
    //         int rowsAffected = pstmt.executeUpdate();
    //         return rowsAffected > 0;
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //         return false;
    //     }
    // }
}