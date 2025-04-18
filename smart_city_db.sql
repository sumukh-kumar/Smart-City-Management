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
