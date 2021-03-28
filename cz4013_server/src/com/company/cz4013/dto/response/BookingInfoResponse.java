package com.company.cz4013.dto.response;

import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZFieldAnnotation;
import com.company.cz4013.dto.model.Booking;

/**
 * Response containing the booking information
 * Attribute names explain for themselves
 */
public class BookingInfoResponse extends BaseXYZZObject {

    @XYZZFieldAnnotation(order = 1)
    private String facilityName;
    @XYZZFieldAnnotation(order = 2)
    private String startDay;
    @XYZZFieldAnnotation(order = 3)
    private String startTime;
    @XYZZFieldAnnotation(order = 4)
    private String endDay;
    @XYZZFieldAnnotation(order = 5)
    private String endTime;

    public BookingInfoResponse(){}

    public BookingInfoResponse(Booking booking) {
        this.facilityName = booking.getFacilityName();
        this.startDay = booking.getStartDay();
        this.startTime = booking.getStartTime();
        this.endDay = booking.getEndDay();
        this.endTime = booking.getEndTime();
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
