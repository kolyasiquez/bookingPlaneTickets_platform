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
    private String passengerPhoto;
    private List<Link> links = new ArrayList<>();

    public Reservation() {
    }

    public Reservation(String id, Flight flight, String passengerName, String passengerPhoto) {
        this.id = id;
        this.flight = flight;
        this.passengerName = passengerName;
        this.passengerPhoto = passengerPhoto;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public List<Link> getLinks() { return links; }
    public void setLinks(List<Link> links) { this.links = links; }

    public String getPassengerPhoto() { return passengerPhoto; }
    public void setPassengerPhoto(String passengerPhoto) { this.passengerPhoto = passengerPhoto; }

    public void addLink(String rel, String href) {
        this.links.add(new Link(rel, href));
    }
}
