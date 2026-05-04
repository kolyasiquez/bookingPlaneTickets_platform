package com.airline.service;

import com.airline.model.Flight;
import com.airline.model.Reservation;
import com.airline.util.DataStorage;
import com.airline.util.TicketPdfGenerator;

import javax.activation.DataHandler;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.mail.util.ByteArrayDataSource;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@WebService(endpointInterface = "com.airline.service.FlightBookingService")
@HandlerChain(file = "handlers.xml")
public class FlightBookingServiceImpl implements FlightBookingService {

    private static final List<Flight> flights = new ArrayList<>();
    private static final Map<String, Reservation> reservations = new ConcurrentHashMap<>();

    static {
        List<Flight> loadedFlights = DataStorage.loadFlights();
        if (loadedFlights != null && !loadedFlights.isEmpty()) {
            flights.addAll(loadedFlights);
        } else {
            flights.add(new Flight(1L, "Warsaw", "London", "2026-05-01", "10:00", 150.0));
            flights.add(new Flight(2L, "Warsaw", "London", "2026-05-01", "18:00", 200.0));
            flights.add(new Flight(3L, "London", "Warsaw", "2026-05-10", "12:00", 180.0));
            flights.add(new Flight(4L, "Paris", "Berlin", "2026-06-15", "09:30", 120.0));
            DataStorage.saveFlights(flights);
        }

        Map<String, Reservation> loadedReservations = DataStorage.loadReservations();
        if (loadedReservations != null) {
            reservations.putAll(loadedReservations);
        }
    }

    @Override
    public List<Flight> searchFlights(String cityFrom, String cityTo, String date) {
        List<Flight> currentFlights = DataStorage.loadFlights();
        if (currentFlights == null) {
            currentFlights = flights;
        }
        
        return currentFlights.stream()
                .filter(f -> (cityFrom == null || cityFrom.isEmpty() || f.getCityFrom().equalsIgnoreCase(cityFrom)) &&
                             (cityTo == null || cityTo.isEmpty() || f.getCityTo().equalsIgnoreCase(cityTo)) &&
                             (date == null || date.isEmpty() || f.getDate().equals(date)))
                .collect(Collectors.toList());
    }

    @Override
    public String bookTicket(Long flightId, String passengerName) {
        List<Flight> currentFlights = DataStorage.loadFlights();
        if (currentFlights == null) {
            currentFlights = flights;
        }
        
        Flight flight = currentFlights.stream().filter(f -> f.getId().equals(flightId)).findFirst().orElse(null);
        if (flight == null) {
            return "Error: Flight not found.";
        }
        
        String reservationId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Reservation reservation = new Reservation(reservationId, flight, passengerName);
        
        Map<String, Reservation> currentReservations = DataStorage.loadReservations();
        if (currentReservations != null) {
            reservations.clear();
            reservations.putAll(currentReservations);
        }
        
        reservations.put(reservationId, reservation);
        
        DataStorage.saveReservations(reservations);
        
        // Notify Module 3 (Notification Service)
        sendNotification(reservationId, passengerName, flight.getCityFrom() + " -> " + flight.getCityTo());
        
        return reservationId;
    }

    private void sendNotification(String resId, String name, String details) {
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost:5001/notify");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setDoOutput(true);

                String jsonInputString = String.format(
                    "{\"reservationId\": \"%s\", \"passengerName\": \"%s\", \"flightDetails\": \"%s\"}",
                    resId, name, details
                );

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                System.out.println("Notification Service response: " + responseCode);
            } catch (Exception e) {
                System.err.println("Failed to send notification: " + e.getMessage());
            }
        }).start();
    }

    @Override
    public Reservation checkReservation(String reservationId) {
        Map<String, Reservation> currentReservations = DataStorage.loadReservations();
        if (currentReservations != null) {
            reservations.clear();
            reservations.putAll(currentReservations);
        }
        return reservations.get(reservationId);
    }

    @Override
    public DataHandler getTicketPDF(String reservationId) {
        Map<String, Reservation> currentReservations = DataStorage.loadReservations();
        if (currentReservations != null) {
            reservations.clear();
            reservations.putAll(currentReservations);
        }
        
        Reservation reservation = reservations.get(reservationId);
        if (reservation == null) {
            return null;
        }

        byte[] pdfBytes = TicketPdfGenerator.generateTicket(reservation);
        if (pdfBytes != null) {
            return new DataHandler(new ByteArrayDataSource(pdfBytes, "application/pdf"));
        }
        return null;
    }

    @Override
    public DataHandler getTicketQRCode(String reservationId) {
        Map<String, Reservation> currentReservations = DataStorage.loadReservations();
        if (currentReservations != null) {
            reservations.clear();
            reservations.putAll(currentReservations);
        }
        
        Reservation reservation = reservations.get(reservationId);
        if (reservation == null) {
            return null;
        }

        byte[] qrBytes = com.airline.util.TicketQrCodeGenerator.generateQRCode(reservation);
        if (qrBytes != null) {
            return new DataHandler(new ByteArrayDataSource(qrBytes, "image/png"));
        }
        return null;
    }
}
