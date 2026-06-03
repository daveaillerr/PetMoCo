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
    role      ENUM('USER','ADMIN') DEFAULT 'USER', 
);

-- Pet Owners table
CREATE TABLE IF NOT EXISTS pet_owner (
    pet_owner_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE, 
    pet_owner_name VARCHAR(100),
    pet_owner_email VARCHAR(100),
    pet_owner_phone VARCHAR(11),
    pet_owner_address VARCHAR(255),

    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Pet type table
CREATE TABLE IF NOT EXISTS pet_type( 
    pet_type_id INT AUTO_INCREMENT PRIMARY KEY,
    pet_type VARCHAR(50) NOT NULL,
    pet_breed VARCHAR(50) NOT NULL,
    pet_size VARCHAR(50) NOT NULL,
)

-- Pet table
CREATE TABLE IF NOT EXISTS pet (
    pet_id        INT AUTO_INCREMENT PRIMARY KEY,
    pet_owner_id  INT            NOT NULL,
    pet_type_id   INT            NOT NULL,
    pet_name      VARCHAR(100)   NOT NULL,
    pet_notes     VARCHAR(200),
    FOREIGN KEY (pet_owner_id) REFERENCES pet_owner(petOwnerID) ON DELETE CASCADE
    FOREIGN KEY (pet_type_id) REFERENCES pet_type(petTypeID) ON DELETE CASCADE
);

-- Appointment table
CREATE TABLE IF NOT EXISTS appointment (
    appointment_id   INT AUTO_INCREMENT PRIMARY KEY,
    pet_id           INT         NOT NULL,
    pet_owner_id     INT         NOT NULL,
    appointment_date DATE        NOT NULL,
    appointment_time TIME        NOT NULL,
    appointment_status           VARCHAR(20) DEFAULT 'PENDING', -- PENDING | APPROVED | CANCELLED | DONE
    total_amount    FLOAT        NOT NULL,
    FOREIGN KEY (pet_id) REFERENCES pet(pet_id) ON DELETE CASCADE
    FOREIGN KEY (pet_owner_id) REFERENCES pet_owner(petOwnerID) ON DELETE CASCADE
);

-- Appointment service table
CREATE TABLE IF NOT EXISTS appointment_service(
    appointment_service_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NOT NULL,
    service_id INT NOT NULL,
    pricing_id INT NOT NULL,
    is_confirmed VARCHAR(10) DEFAULT 'NO',
    FOREIGN KEY (appointment_id) REFERENCES appointment(appointment_id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(service_id) ON DELETE CASCADE
);

-- Services table
CREATE TABLE IF NOT EXISTS services(
    service_id INT AUTO_INCREMENT PRIMARY KEY,
    services_name VARCHAR(100) NOT NULL,
    service_description VARCHAR(200),
    service_duration INT NOT NULL,
);

-- Pricing table
CREATE TABLE IF NOT EXISTS pricing(
    pricing_id INT AUTO_INCREMENT PRIMARY KEY,
    service_id INT NOT NULL,
    price FLOAT NOT NULL,
    price_type VARCHAR(10) NOT NULL, -- FIXED | VARIABLE
    price_size VARCHAR(10) NOT NULL, -- SMALL | MEDIUM | LARGE | EXTRA LARGE
    FOREIGN KEY (service_id) REFERENCES services(service_id) ON DELETE CASCADE
);

