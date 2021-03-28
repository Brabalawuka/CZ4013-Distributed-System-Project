package com.company.cz4013.dto.response;

import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZFieldAnnotation;

/**
 * Response containing the newly created booking ID
 * Attribute names explain for themselves
 */
public class BookingCreationResponse extends BaseXYZZObject {

    @XYZZFieldAnnotation(order = 1)
    private String bookingId;

    public BookingCreationResponse() {
    }

    public BookingCreationResponse(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
}
