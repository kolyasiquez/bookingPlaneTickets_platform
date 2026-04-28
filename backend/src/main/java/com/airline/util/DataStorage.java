package com.airline.util;

import com.airline.model.Flight;
import com.airline.model.Reservation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStorage {

    private static final String DATA_DIR = System.getProperty("user.home") + File.separator + ".airline_data";
    private static final String FLIGHTS_FILE = DATA_DIR + File.separator + "flights.json";
    private static final String RESERVATIONS_FILE = DATA_DIR + File.separator + "reservations.json";
    
    private static final Gson gson = new Gson();

    static {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static List<Flight> loadFlights() {
        File file = new File(FLIGHTS_FILE);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type listType = new TypeToken<ArrayList<Flight>>(){}.getType();
                List<Flight> flights = gson.fromJson(reader, listType);
                if (flights != null) {
                    return flights;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void saveFlights(List<Flight> flights) {
        try (FileWriter writer = new FileWriter(FLIGHTS_FILE)) {
            gson.toJson(flights, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Reservation> loadReservations() {
        File file = new File(RESERVATIONS_FILE);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type mapType = new TypeToken<ConcurrentHashMap<String, Reservation>>(){}.getType();
                Map<String, Reservation> reservations = gson.fromJson(reader, mapType);
                if (reservations != null) {
                    return reservations;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ConcurrentHashMap<>();
    }

    public static void saveReservations(Map<String, Reservation> reservations) {
        try (FileWriter writer = new FileWriter(RESERVATIONS_FILE)) {
            gson.toJson(reservations, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
