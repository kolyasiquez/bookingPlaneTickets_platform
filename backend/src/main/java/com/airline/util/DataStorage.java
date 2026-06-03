package com.airline.util;

import com.airline.model.Flight;
import com.airline.model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStorage {

    private static Connection getConnection() throws SQLException {
        String url = System.getenv("DB_URL");
        if (url == null) {
            url = "jdbc:postgresql://db:5432/airline";
        }
        String user = System.getenv("DB_USER");
        if (user == null) {
            user = "airline_user";
        }
        String pass = System.getenv("DB_PASS");
        if (pass == null) {
            pass = "airline_pass";
        }
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL Driver not found in classpath", e);
        }
        return DriverManager.getConnection(url, user, pass);
    }

    public static List<Flight> loadFlights() {
        List<Flight> list = new ArrayList<>();
        String query = "SELECT id, city_from, city_to, date, time, price FROM flights";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Flight(
                    rs.getLong("id"),
                    rs.getString("city_from"),
                    rs.getString("city_to"),
                    rs.getString("date"),
                    rs.getString("time"),
                    rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading flights: " + e.getMessage());
        }
        return list;
    }

    public static void saveFlights(List<Flight> flights) {
        String query = "INSERT INTO flights (city_from, city_to, date, time, price) VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            for (Flight f : flights) {
                stmt.setString(1, f.getCityFrom());
                stmt.setString(2, f.getCityTo());
                stmt.setString(3, f.getDate());
                stmt.setString(4, f.getTime());
                stmt.setDouble(5, f.getPrice());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error saving flights: " + e.getMessage());
        }
    }

    public static Map<String, Reservation> loadReservations() {
        Map<String, Reservation> map = new ConcurrentHashMap<>();
        String query = "SELECT r.id, r.passenger_name, r.passenger_photo, " +
                       "f.id AS flight_id, f.city_from, f.city_to, f.date, f.time, f.price " +
                       "FROM reservations r " +
                       "JOIN flights f ON r.flight_id = f.id";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Flight flight = new Flight(
                    rs.getLong("flight_id"),
                    rs.getString("city_from"),
                    rs.getString("city_to"),
                    rs.getString("date"),
                    rs.getString("time"),
                    rs.getDouble("price")
                );
                Reservation res = new Reservation(
                    rs.getString("id"),
                    flight,
                    rs.getString("passenger_name"),
                    rs.getString("passenger_photo")
                );
                map.put(res.getId(), res);
            }
        } catch (SQLException e) {
            System.err.println("Error loading reservations: " + e.getMessage());
        }
        return map;
    }

    public static void saveReservations(Map<String, Reservation> reservations) {
        String query = "INSERT INTO reservations (id, flight_id, passenger_name, passenger_photo) VALUES (?, ?, ?, ?) " +
                       "ON CONFLICT (id) DO UPDATE SET flight_id = EXCLUDED.flight_id, passenger_name = EXCLUDED.passenger_name, passenger_photo = EXCLUDED.passenger_photo";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            for (Reservation r : reservations.values()) {
                stmt.setString(1, r.getId());
                stmt.setLong(2, r.getFlight().getId());
                stmt.setString(3, r.getPassengerName());
                stmt.setString(4, r.getPassengerPhoto());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error saving reservations: " + e.getMessage());
        }
    }
}
