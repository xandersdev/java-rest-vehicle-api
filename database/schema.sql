CREATE DATABASE IF NOT EXISTS vehicle_api
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE vehicle_api;

CREATE TABLE IF NOT EXISTS vehicles (
    id INT NOT NULL PRIMARY KEY,
    brand VARCHAR(40) NOT NULL,
    model VARCHAR(60) NOT NULL,
    type VARCHAR(20),
    color VARCHAR(30),
    engine VARCHAR(30),
    displacement INT,
    vehicle_date DATE NOT NULL,
    registration VARCHAR(20) NOT NULL UNIQUE,
    extras VARCHAR(160),
    price DECIMAL(12, 2) NOT NULL
);
