
-- Delete all table to add them again updated
DROP TABLE IF EXISTS admins;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS stores;
DROP TABLE IF EXISTS users;



-- 1️ USER TABLE
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    photo BYTEA,
    age INT CHECK (age >= 8),
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    location_lat DECIMAL(10,8),
    location_lng DECIMAL(11,8),
    notifications_enabled BOOLEAN DEFAULT TRUE
);

-- 2️ STORE TABLE
CREATE TABLE stores (
    store_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(255),
    photo BYTEA,
    owner_user_id INT,
    CONSTRAINT fk_owner FOREIGN KEY (owner_user_id)
        REFERENCES users(user_id)
        ON DELETE SET NULL
);

-- 3️ PRODUCT TABLE
CREATE TABLE products (
    product_id SERIAL PRIMARY KEY,
    store_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) CHECK (price >= 0),
    photo BYTEA,
    CONSTRAINT fk_store FOREIGN KEY (store_id)
        REFERENCES stores(store_id)
        ON DELETE CASCADE
);

-- 4️ ADMIN TABLE
CREATE TABLE admins (
    admin_id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);
