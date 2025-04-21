-- Create the database (if it doesn't exist)
CREATE DATABASE IF NOT EXISTS smart_city_db;

-- Use the database
USE smart_city_db;

-- Create the table for power readings
CREATE TABLE IF NOT EXISTS power_readings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reading_date DATE NOT NULL,
    power_consumed DOUBLE NOT NULL,
    fault_detected BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Optional: track when record was inserted
    INDEX idx_reading_date (reading_date) -- Add index for faster date filtering/deletion
);

-- --- New Table for Monthly Statistics ---
CREATE TABLE IF NOT EXISTS `power_stats` (
    `year_month` CHAR(7) NOT NULL PRIMARY KEY,
    `total_consumption` DOUBLE NOT NULL,
    `fault_count` INT NOT NULL,
    `days_recorded` INT NOT NULL,
    `average_consumption` DOUBLE NOT NULL,
    `last_updated` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- --- New Table for Traffic Signal Readings ---
CREATE TABLE IF NOT EXISTS traffic_readings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    signal_location VARCHAR(255) NOT NULL, -- e.g., "Main St & 1st Ave"
    lane_id INT NOT NULL,                  -- e.g., 1, 2, 3, 4
    vehicle_count INT NOT NULL,
    reading_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_signal_time (signal_location, reading_timestamp), -- Index for faster lookups
    INDEX idx_timestamp (reading_timestamp) -- Index for faster time-based filtering/deletion
);

-- --- New Table for Parking Spots ---
CREATE TABLE IF NOT EXISTS parking_spots (
    spot_id VARCHAR(50) PRIMARY KEY,       -- e.g., "P1-A01", "DowntownGarage-3B-12"
    location_description VARCHAR(255),     -- e.g., "Parking Lot 1, Row A, Spot 01"
    is_occupied BOOLEAN NOT NULL DEFAULT FALSE,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- --- Optional: Initial Parking Spot Data ---
-- You might want to pre-populate some parking spots
-- INSERT INTO parking_spots (spot_id, location_description, is_occupied) VALUES
-- ('P1-A01', 'Main St Lot, Row A, Spot 01', FALSE),
-- ('P1-A02', 'Main St Lot, Row A, Spot 02', TRUE),
-- ('P1-A03', 'Main St Lot, Row A, Spot 03', FALSE),
-- ('DG-1A-01', 'Downtown Garage, Level 1A, Spot 01', FALSE)
-- ON DUPLICATE KEY UPDATE spot_id=spot_id; -- Avoid errors if run multiple times
