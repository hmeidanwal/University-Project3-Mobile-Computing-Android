-- Database structure SQL script
-- Generated from LoopBack models
-- Table names match LoopBack model names (singular)

-- Drop tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS store;
DROP TABLE IF EXISTS "user";
DROP TABLE IF EXISTS admin;
DROP TABLE IF EXISTS test;

-- Create store table
CREATE TABLE store (
    store_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL
);

-- Create product table
CREATE TABLE product (
    product_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    store_id INT NOT NULL,
    FOREIGN KEY (store_id) REFERENCES store(store_id) ON DELETE CASCADE
);

-- Create user table (quoted because "user" is a reserved word in PostgreSQL)
CREATE TABLE "user" (
    user_id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    photo_url VARCHAR(500) NOT NULL,
    age INT NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    location_lat VARCHAR(50) NOT NULL,
    location_lng VARCHAR(50) NOT NULL,
    notification_enabled BOOLEAN NOT NULL DEFAULT FALSE
);

-- Create admin table
CREATE TABLE admin (
    admin_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);

-- Create test table
CREATE TABLE test (
    test_id SERIAL PRIMARY KEY,
    Name VARCHAR(255)
);

-- Create indexes for better query performance
CREATE INDEX idx_product_store_id ON product(store_id);
CREATE INDEX idx_user_email ON "user"(email);

