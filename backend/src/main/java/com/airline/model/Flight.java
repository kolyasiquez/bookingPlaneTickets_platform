package com.airline.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Flight {
    private Long id;
    private String cityFrom;
    private String cityTo;
    private String date;
    private String time;
    private double price;

    public Flight() {
    }

    public Flight(Long id, String cityFrom, String cityTo, String date, String time, double price) {
        this.id = id;
        this.cityFrom = cityFrom;
        this.cityTo = cityTo;
        this.date = date;
        this.time = time;
        this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCityFrom() { return cityFrom; }
    public void setCityFrom(String cityFrom) { this.cityFrom = cityFrom; }

    public String getCityTo() { return cityTo; }
    public void setCityTo(String cityTo) { this.cityTo = cityTo; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
