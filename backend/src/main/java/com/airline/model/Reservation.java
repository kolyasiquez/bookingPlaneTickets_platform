package com.airline.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Reservation {
    private String id;
    private Flight flight;
    private String passengerName;

    public Reservation() {
    }

    public Reservation(String id, Flight flight, String passengerName) {
        this.id = id;
        this.flight = flight;
        this.passengerName = passengerName;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
}
