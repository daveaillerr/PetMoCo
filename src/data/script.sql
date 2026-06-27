-- ============================================================
-- PetMoCo Database Schema
-- Run this script once to set up the database.
-- ============================================================

CREATE DATABASE IF NOT EXISTS petmoco_db;
USE petmoco_db;

-- Drop existing tables to avoid schema mismatches with older versions
DROP TABLE IF EXISTS appointment_service;
DROP TABLE IF EXISTS appointment;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS pricing;
DROP TABLE IF EXISTS services;
DROP TABLE IF EXISTS pet;
DROP TABLE IF EXISTS pets;
DROP TABLE IF EXISTS pet_type;
DROP TABLE IF EXISTS pet_owner;
DROP TABLE IF EXISTS users;

-- Users table (for login/authentication)
CREATE TABLE IF NOT EXISTS users (
    user_id   INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50) UNIQUE NOT NULL,
    password  VARCHAR(64) NOT NULL,       -- SHA-256 hex hash
    role      ENUM('USER','ADMIN') DEFAULT 'USER'
);

-- Pet Owners table
-- Column names match what UserDAO.java queries:
--   name, email_address, contact_number, home_address
CREATE TABLE IF NOT EXISTS pet_owner (
    pet_owner_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id        INT UNIQUE,
    name           VARCHAR(100),
    email_address  VARCHAR(100),
    contact_number VARCHAR(15),
    home_address   VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Pet type table
CREATE TABLE IF NOT EXISTS pet_type (
    pet_type_id INT AUTO_INCREMENT PRIMARY KEY,
    pet_type    VARCHAR(50) NOT NULL,
    pet_breed   VARCHAR(50) NOT NULL,
    pet_size    VARCHAR(50) NOT NULL
);

-- Pet table
CREATE TABLE IF NOT EXISTS pet (
    pet_id        INT AUTO_INCREMENT PRIMARY KEY,
    pet_owner_id  INT          NOT NULL,
    pet_type_id   INT          NOT NULL,
    pet_name      VARCHAR(100) NOT NULL,
    pet_notes     VARCHAR(200),
    FOREIGN KEY (pet_owner_id) REFERENCES pet_owner(pet_owner_id) ON DELETE CASCADE,
    FOREIGN KEY (pet_type_id)  REFERENCES pet_type(pet_type_id)   ON DELETE CASCADE
);

-- Services table (must be created before appointment_service which references it)
CREATE TABLE IF NOT EXISTS services (
    service_id          INT AUTO_INCREMENT PRIMARY KEY,
    services_name       VARCHAR(100) NOT NULL,
    service_description VARCHAR(200),
    service_duration    INT NOT NULL
);

-- Pricing table (must be created before appointment_service which references it)
CREATE TABLE IF NOT EXISTS pricing (
    pricing_id INT AUTO_INCREMENT PRIMARY KEY,
    service_id INT NOT NULL,
    price      FLOAT NOT NULL,
    price_type VARCHAR(10) NOT NULL,   -- FIXED | VARIABLE
    price_size VARCHAR(20) NOT NULL,   -- SMALL | MEDIUM | LARGE | EXTRA LARGE
    FOREIGN KEY (service_id) REFERENCES services(service_id) ON DELETE CASCADE
);

-- Appointment table
CREATE TABLE IF NOT EXISTS appointment (
    appointment_id     INT AUTO_INCREMENT PRIMARY KEY,
    pet_id             INT         NOT NULL,
    pet_owner_id       INT         NOT NULL,
    appointment_date   DATE        NOT NULL,
    appointment_time   TIME        NOT NULL,
    appointment_status VARCHAR(20) DEFAULT 'PENDING',  -- PENDING | APPROVED | CANCELLED | DONE
    total_amount       FLOAT       NOT NULL,
    FOREIGN KEY (pet_id)       REFERENCES pet(pet_id)                   ON DELETE CASCADE,
    FOREIGN KEY (pet_owner_id) REFERENCES pet_owner(pet_owner_id)       ON DELETE CASCADE
);

-- Appointment service table (junction between appointment, services, and pricing)
CREATE TABLE IF NOT EXISTS appointment_service (
    appointment_service_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id         INT NOT NULL,
    service_id             INT NOT NULL,
    pricing_id             INT NOT NULL,
    is_confirmed           VARCHAR(10) DEFAULT 'NO',
    FOREIGN KEY (appointment_id) REFERENCES appointment(appointment_id) ON DELETE CASCADE,
    FOREIGN KEY (service_id)     REFERENCES services(service_id)        ON DELETE CASCADE,
    FOREIGN KEY (pricing_id)     REFERENCES pricing(pricing_id)         ON DELETE CASCADE
);

-- ============================================================
-- Seed Data: Services & Pricing
-- ============================================================

-- Services catalog
INSERT IGNORE INTO services (service_id, services_name, service_description, service_duration) VALUES
(1, 'Grooming',     'Full grooming: Bath, Haircut, Nail trim', 60),
(2, 'Pet Sitting',  'In-home pet sitting and care',            120),
(3, 'Pet Walking',  'Outdoor walking and exercise session',    45);

-- Pricing by service and pet size
INSERT IGNORE INTO pricing (pricing_id, service_id, price, price_type, price_size) VALUES
-- Grooming prices
(1,  1, 350.00, 'FIXED', 'SMALL'),
(2,  1, 500.00, 'FIXED', 'MEDIUM'),
(3,  1, 650.00, 'FIXED', 'LARGE'),
(4,  1, 800.00, 'FIXED', 'EXTRA LARGE'),
-- Pet Sitting prices
(5,  2, 500.00, 'FIXED', 'SMALL'),
(6,  2, 600.00, 'FIXED', 'MEDIUM'),
(7,  2, 700.00, 'FIXED', 'LARGE'),
(8,  2, 850.00, 'FIXED', 'EXTRA LARGE'),
-- Pet Walking prices
(9,  3, 200.00, 'FIXED', 'SMALL'),
(10, 3, 250.00, 'FIXED', 'MEDIUM'),
(11, 3, 300.00, 'FIXED', 'LARGE'),
(12, 3, 400.00, 'FIXED', 'EXTRA LARGE');
