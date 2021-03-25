package com.company.cz4013.dto.query;

import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZFieldAnnotation;
import com.company.cz4013.dto.model.Booking;

public class BookingEditingQuery extends BaseXYZZObject {

    @XYZZFieldAnnotation(order = 1)
    private String bookingId;
    @XYZZFieldAnnotation(order = 2)
    private Booking forward;
    @XYZZFieldAnnotation(order = 3)
    private int shiftTime;

    public BookingEditingQuery() {
    }

    public BookingEditingQuery(String bookingId, Booking forward, int shiftTime) {
        this.bookingId = bookingId;
        this.forward = forward;
        this.shiftTime = shiftTime;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Booking getForward() {
        return forward;
    }

    public void setForward(Booking forward) {
        this.forward = forward;
    }

    public int getShiftTime() {
        return shiftTime;
    }

    public void setShiftTime(int shiftTime) {
        this.shiftTime = shiftTime;
    }
}
