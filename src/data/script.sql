-- ============================================================
-- PetMoCo Database Schema
-- Run this script once to set up the database.
-- ============================================================

CREATE DATABASE IF NOT EXISTS petmoco_db;
USE petmoco_db;
	
-- Users table (for login/authentication)
CREATE TABLE IF NOT EXISTS users (
    user_id   INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50) UNIQUE NOT NULL,
    password  VARCHAR(64)  NOT NULL,          -- SHA-256 hex hash
    role      ENUM('USER','ADMIN') DEFAULT 'USER', -- Ensures customer & admin roles only
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

-- Pet Owners table
CREATE TABLE pet_owner (
    petOwnerID INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE, -- Connects to user_id for separate function
    name VARCHAR(100),
    email_address VARCHAR(100),
    contact_number VARCHAR(20),
    home_address VARCHAR(255),

    FOREIGN KEY (user_id)
    REFERENCES users(user_id)
);

-- Pets table
CREATE TABLE IF NOT EXISTS pets (
    pet_id        INT AUTO_INCREMENT PRIMARY KEY,
    pet_name      VARCHAR(100) NOT NULL,
    pet_type      VARCHAR(50)  NOT NULL,       -- e.g. Dog, Cat, Bird
    breed         VARCHAR(100),
    age           INT,
    owner_name    VARCHAR(100) NOT NULL,
    owner_contact VARCHAR(50)  NOT NULL,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- Appointments table
CREATE TABLE IF NOT EXISTS appointments (
    appointment_id   INT AUTO_INCREMENT PRIMARY KEY,
    pet_id           INT         NOT NULL,
    appointment_date DATE        NOT NULL,
    appointment_time TIME        NOT NULL,
    service_type     VARCHAR(20) NOT NULL,     -- GROOMING | SITTING | WALKING
    status           VARCHAR(20) DEFAULT 'SCHEDULED', -- SCHEDULED | CANCELLED
    notes            TEXT,
    created_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE
);

-- Optional: seed a default admin account (password = 'admin123')
-- SHA-256 of 'admin123' = 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a
INSERT IGNORE INTO users (username, password, role)
VALUES ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a', 'ADMIN');

SHOW TABLES;
DESCRIBE users;
DESCRIBE pets;
DESCRIBE appointments;
