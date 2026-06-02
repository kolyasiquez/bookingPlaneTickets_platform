package com.airline.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Reservation {
    private String id;
    private Flight flight;
    private String passengerName;
    private List<Link> links = new ArrayList<>();

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

    public List<Link> getLinks() { return links; }
    public void setLinks(List<Link> links) { this.links = links; }

    public void addLink(String rel, String href) {
        this.links.add(new Link(rel, href));
    }
}
