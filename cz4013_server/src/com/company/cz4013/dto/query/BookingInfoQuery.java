package com.company.cz4013.dto.query;

import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZFieldAnnotation;

import java.util.List;

/**
 * Query containing necessary fields for checking a particular booking information
 * Attribute names explain for themselves
 */
public class BookingInfoQuery extends BaseXYZZObject {

    @XYZZFieldAnnotation(order = 1)
    private String bookingId;

    public BookingInfoQuery() {
    }

    public BookingInfoQuery(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

}
