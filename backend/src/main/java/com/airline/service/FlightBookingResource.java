package com.airline.service;

import com.airline.model.Flight;
import com.airline.model.Reservation;
import com.airline.util.DataStorage;
import com.airline.util.TicketPdfGenerator;
import com.airline.util.TicketQrCodeGenerator;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
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

@Path("/booking")
public class FlightBookingResource {

    @Context
    private UriInfo uriInfo;

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

    private String getBaseUri() {
        if (uriInfo != null) {
            return uriInfo.getBaseUri().toString();
        }
        return "https://localhost:8181/airline-service/api/";
    }

    @GET
    @Path("/flights")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchFlights(
            @QueryParam("cityFrom") String cityFrom,
            @QueryParam("cityTo") String cityTo,
            @QueryParam("date") String date) {
        
        List<Flight> currentFlights = DataStorage.loadFlights();
        if (currentFlights == null) {
            currentFlights = flights;
        }

        List<Flight> results = currentFlights.stream()
                .filter(f -> (cityFrom == null || cityFrom.isEmpty() || f.getCityFrom().equalsIgnoreCase(cityFrom)) &&
                             (cityTo == null || cityTo.isEmpty() || f.getCityTo().equalsIgnoreCase(cityTo)) &&
                             (date == null || date.isEmpty() || f.getDate().equals(date)))
                .collect(Collectors.toList());

        // Inject HATEOAS links
        String base = getBaseUri();
        for (Flight f : results) {
            f.getLinks().clear();
            f.addLink("self", base + "booking/flights?cityFrom=" + f.getCityFrom() + "&cityTo=" + f.getCityTo() + "&date=" + f.getDate());
            f.addLink("book", base + "booking/book");
        }

        return Response.ok(results).build();
    }

    @POST
    @Path("/book")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response bookTicket(
            @FormDataParam("flightId") Long flightId,
            @FormDataParam("passengerName") String passengerName,
            @FormDataParam("photo") java.io.InputStream photoInputStream,
            @FormDataParam("photo") FormDataContentDisposition photoDisposition) {

        System.out.println("BACKEND: Received bookTicket request");
        System.out.println("BACKEND: flightId=" + flightId + ", passengerName=" + passengerName);
        System.out.println("BACKEND: photoInputStream=" + (photoInputStream != null ? "Not Null" : "Null"));
        System.out.println("BACKEND: photoDisposition=" + (photoDisposition != null ? "Not Null (filename=" + photoDisposition.getFileName() + ")" : "Null"));

        if (flightId == null || passengerName == null || passengerName.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Invalid input data.\"}").build();
        }

        List<Flight> currentFlights = DataStorage.loadFlights();
        Flight flight = currentFlights.stream().filter(f -> f.getId().equals(flightId)).findFirst().orElse(null);
        if (flight == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\": \"Flight not found.\"}").build();
        }

        // Save uploaded photo
        String photoFilename = savePhoto(photoInputStream, photoDisposition);
        System.out.println("BACKEND: savePhoto result photoFilename=" + photoFilename);

        String reservationId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Reservation reservation = new Reservation(reservationId, flight, passengerName, photoFilename);

        // Load, put, and save
        Map<String, Reservation> currentReservations = DataStorage.loadReservations();
        if (currentReservations != null) {
            reservations.clear();
            reservations.putAll(currentReservations);
        }
        reservations.put(reservationId, reservation);
        DataStorage.saveReservations(reservations);

        // Notify Module 3 (Notification Service)
        sendNotification(reservationId, passengerName, flight.getCityFrom() + " -> " + flight.getCityTo());

        BookResponse response = new BookResponse(reservationId);
        String base = getBaseUri();
        response.addLink("self", base + "booking/reservation/" + reservationId);
        response.addLink("pdf", base + "booking/reservation/" + reservationId + "/pdf");
        response.addLink("qrcode", base + "booking/reservation/" + reservationId + "/qrcode");

        return Response.ok(response).build();
    }

    private String savePhoto(java.io.InputStream is, FormDataContentDisposition fileDetail) {
        if (is == null) {
            System.err.println("BACKEND: savePhoto error - InputStream is null");
            return null;
        }
        if (fileDetail == null) {
            System.err.println("BACKEND: savePhoto error - FormDataContentDisposition is null");
            return null;
        }
        if (fileDetail.getFileName() == null || fileDetail.getFileName().isEmpty()) {
            System.err.println("BACKEND: savePhoto error - fileDetail filename is null or empty");
            return null;
        }
        String uploadsDir = System.getenv("UPLOADS_DIR");
        if (uploadsDir == null) {
            uploadsDir = "/app/uploads";
        }
        java.io.File dir = new java.io.File(uploadsDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            System.out.println("BACKEND: savePhoto created uploads dir? " + created);
        }
        
        String originalName = fileDetail.getFileName();
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalName.substring(dotIndex);
        }
        String uniqueName = UUID.randomUUID().toString() + extension;
        java.io.File file = new java.io.File(dir, uniqueName);
        System.out.println("BACKEND: savePhoto target file: " + file.getAbsolutePath());
        
        try (java.io.OutputStream os = new java.io.FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            System.out.println("BACKEND: savePhoto file written successfully!");
            return uniqueName;
        } catch (java.io.IOException e) {
            System.err.println("BACKEND: savePhoto IOException: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @GET
    @Path("/reservation/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkReservation(@PathParam("id") String reservationId) {
        Map<String, Reservation> currentReservations = DataStorage.loadReservations();
        if (currentReservations != null) {
            reservations.clear();
            reservations.putAll(currentReservations);
        }
        Reservation reservation = reservations.get(reservationId);
        if (reservation == null) {
            throw new NotFoundException();
        }

        // Inject HATEOAS links
        String base = getBaseUri();
        reservation.getLinks().clear();
        reservation.addLink("self", base + "booking/reservation/" + reservationId);
        reservation.addLink("pdf", base + "booking/reservation/" + reservationId + "/pdf");
        reservation.addLink("qrcode", base + "booking/reservation/" + reservationId + "/qrcode");
        
        // Also inject link in flight object nested
        Flight f = reservation.getFlight();
        if (f != null) {
            f.getLinks().clear();
            f.addLink("self", base + "booking/flights?cityFrom=" + f.getCityFrom() + "&cityTo=" + f.getCityTo() + "&date=" + f.getDate());
        }

        return Response.ok(reservation).build();
    }

    @GET
    @Path("/reservation/{id}/pdf")
    @Produces("application/pdf")
    public Response getTicketPDF(@PathParam("id") String reservationId) {
        Map<String, Reservation> currentReservations = DataStorage.loadReservations();
        if (currentReservations != null) {
            reservations.clear();
            reservations.putAll(currentReservations);
        }

        Reservation reservation = reservations.get(reservationId);
        if (reservation == null) {
            throw new NotFoundException();
        }

        byte[] pdfBytes = TicketPdfGenerator.generateTicket(reservation);
        if (pdfBytes != null) {
            return Response.ok(pdfBytes)
                    .header("Content-Disposition", "attachment; filename=\"ticket_" + reservationId + ".pdf\"")
                    .build();
        }
        return Response.serverError().build();
    }

    @GET
    @Path("/reservation/{id}/qrcode")
    @Produces("image/png")
    public Response getTicketQRCode(@PathParam("id") String reservationId) {
        Map<String, Reservation> currentReservations = DataStorage.loadReservations();
        if (currentReservations != null) {
            reservations.clear();
            reservations.putAll(currentReservations);
        }

        Reservation reservation = reservations.get(reservationId);
        if (reservation == null) {
            throw new NotFoundException();
        }

        byte[] qrBytes = TicketQrCodeGenerator.generateQRCode(reservation);
        if (qrBytes != null) {
            return Response.ok(qrBytes)
                    .header("Content-Disposition", "inline; filename=\"qrcode_" + reservationId + ".png\"")
                    .build();
        }
        return Response.serverError().build();
    }

    private void sendNotification(String resId, String name, String details) {
        new Thread(() -> {
            try {
                String notifUrl = System.getenv("NOTIFICATION_URL");
                if (notifUrl == null) {
                    notifUrl = "http://airline-notification:5001/notify";
                }
                URL url = new URL(notifUrl);
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

    public static class BookRequest {
        private Long flightId;
        private String passengerName;

        public BookRequest() {}

        public Long getFlightId() { return flightId; }
        public void setFlightId(Long flightId) { this.flightId = flightId; }

        public String getPassengerName() { return passengerName; }
        public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    }

    public static class BookResponse {
        private String reservationId;
        private List<com.airline.model.Link> links = new ArrayList<>();

        public BookResponse() {}
        public BookResponse(String reservationId) { this.reservationId = reservationId; }

        public String getReservationId() { return reservationId; }
        public void setReservationId(String reservationId) { this.reservationId = reservationId; }

        public List<com.airline.model.Link> getLinks() { return links; }
        public void setLinks(List<com.airline.model.Link> links) { this.links = links; }

        public void addLink(String rel, String href) {
            this.links.add(new com.airline.model.Link(rel, href));
        }
    }
}
