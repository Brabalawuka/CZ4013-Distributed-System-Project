package com.company.cz4013.dto.query;

import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZFieldAnnotation;

public class BookingEditingQuery extends BaseXYZZObject {

    @XYZZFieldAnnotation(order = 1)
    private String bookingId;
    @XYZZFieldAnnotation(order = 2)
    private Boolean postpone;
    @XYZZFieldAnnotation(order = 3)
    private int shiftTime;

    public BookingEditingQuery() {
    }

    public BookingEditingQuery(String bookingId, Boolean postpone, int shiftTime) {
        this.bookingId = bookingId;
        this.postpone = postpone;
        this.shiftTime = shiftTime;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Boolean getPostpone() {
        return postpone;
    }

    public void setPostpone(Boolean postpone) {
        this.postpone = postpone;
    }

    public int getShiftTime() {
        return shiftTime;
    }

    public void setShiftTime(int shiftTime) {
        this.shiftTime = shiftTime;
    }
}
