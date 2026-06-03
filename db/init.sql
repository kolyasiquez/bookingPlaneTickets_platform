CREATE TABLE IF NOT EXISTS flights (
    id SERIAL PRIMARY KEY,
    city_from VARCHAR(100) NOT NULL,
    city_to VARCHAR(100) NOT NULL,
    date VARCHAR(50) NOT NULL,
    time VARCHAR(50) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    CONSTRAINT unique_flight UNIQUE (city_from, city_to, date, time, price)
);

CREATE TABLE IF NOT EXISTS reservations (
    id VARCHAR(50) PRIMARY KEY,
    flight_id INT REFERENCES flights(id) ON DELETE CASCADE,
    passenger_name VARCHAR(100) NOT NULL,
    passenger_photo VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);

-- Pre-populate flights
INSERT INTO flights (city_from, city_to, date, time, price) VALUES 
('Warsaw', 'London', '2026-05-01', '10:00', 150.0),
('Warsaw', 'London', '2026-05-01', '18:00', 200.0),
('London', 'Warsaw', '2026-05-10', '12:00', 180.0),
('Paris', 'Berlin', '2026-06-15', '09:30', 120.0)
ON CONFLICT (city_from, city_to, date, time, price) DO NOTHING;
