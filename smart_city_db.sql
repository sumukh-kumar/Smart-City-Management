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

-- Add after your existing power_readings and power_stats tables

-- Create table for emergency alerts
CREATE TABLE IF NOT EXISTS `emergencies` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `type` VARCHAR(100) NOT NULL,
    `location` VARCHAR(255) NOT NULL,
    `description` TEXT NOT NULL,
    `severity` INT NOT NULL,
    `timestamp` DATETIME NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    INDEX idx_status (status),
    INDEX idx_severity (severity)
);

-- Create table for weather alerts
CREATE TABLE IF NOT EXISTS `weather_alerts` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `alert_type` VARCHAR(100) NOT NULL,
    `description` TEXT NOT NULL,
    `severity` INT NOT NULL,
    `timestamp` DATETIME NOT NULL,
    `active` BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_active (active),
    INDEX idx_severity (severity)
);
