package com.company.cz4013.dto.model;

import java.util.UUID;

/**
 * An OOP representation of a Booking entry
 * Attribute names explain for themselves
 */
public class Booking {
    private String bookingID;
    private String facilityName;
    private String startDay;
    private String startTime;
    private String endDay;
    private String endTime;

    public Booking(String bookingID, String facilityName, String startDay, String startTime, String endDay, String endTime) {
        this.bookingID = bookingID;
        this.facilityName = facilityName;
        this.startDay = startDay;
        this.startTime = startTime;
        this.endDay = endDay;
        this.endTime = endTime;
    }

    public Booking(String facilityName, String startDay, String startTime, String endDay, String endTime) {
        this.bookingID = UUID.randomUUID().toString();
        this.facilityName = facilityName;
        this.startDay = startDay;
        this.startTime = startTime;
        this.endDay = endDay;
        this.endTime = endTime;
    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

}
