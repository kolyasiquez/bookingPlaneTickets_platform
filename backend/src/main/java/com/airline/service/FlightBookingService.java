package com.airline.service;

import com.airline.model.Flight;
import com.airline.model.Reservation;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;

import javax.activation.DataHandler;
import java.util.List;

@WebService
@MTOM
public interface FlightBookingService {

    @WebMethod
    List<Flight> searchFlights(@WebParam(name = "cityFrom") String cityFrom, 
                               @WebParam(name = "cityTo") String cityTo, 
                               @WebParam(name = "date") String date);

    @WebMethod
    String bookTicket(@WebParam(name = "flightId") Long flightId, 
                      @WebParam(name = "passengerName") String passengerName);

    @WebMethod
    Reservation checkReservation(@WebParam(name = "reservationId") String reservationId);

    @WebMethod
    DataHandler getTicketPDF(@WebParam(name = "reservationId") String reservationId);

    @WebMethod
    DataHandler getTicketQRCode(@WebParam(name = "reservationId") String reservationId);
}
