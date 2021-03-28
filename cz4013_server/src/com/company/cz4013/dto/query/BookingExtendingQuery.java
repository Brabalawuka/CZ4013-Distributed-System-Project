package com.company.cz4013.dto.query;

import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZFieldAnnotation;

public class BookingExtendingQuery extends BaseXYZZObject {

    @XYZZFieldAnnotation(order = 1)
    private String bookingId;
    @XYZZFieldAnnotation(order = 2)
    private int extendingTime;

    public BookingExtendingQuery() {
    }

    public BookingExtendingQuery(String bookingId, int extendingTime) {
        this.bookingId = bookingId;
        this.extendingTime = extendingTime;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }


    public int getExtendingTime() {
        return extendingTime;
    }

    public void setExtendingTime(int extendingTime) {
        this.extendingTime = extendingTime;
    }
}
