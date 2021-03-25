package com.company.cz4013.controller;

import com.company.cz4013.Data;
import com.company.cz4013.dto.model.Booking;
import com.company.cz4013.dto.query.BookingCreationQuery;
import com.company.cz4013.dto.query.BookingEditingQuery;
import com.company.cz4013.dto.query.BookingInfoQuery;
import com.company.cz4013.dto.response.BookingCreationResponse;
import com.company.cz4013.dto.response.BookingInfoResponse;
import com.company.cz4013.util.TimePtrOffsetConverter;

import java.util.BitSet;

public class BookingService {

    public BookingService() {
    }

    public BookingInfoResponse getBookingInfo(BookingInfoQuery query) throws Exception {
        if (!Data.bookingList.containsKey(query.getBookingId())) {
            throw new Exception("No Booking Found With ID " + query.getBookingId());
        }
        return new BookingInfoResponse(Data.bookingList.get(query.getBookingId()));
    }

    public BookingCreationResponse creatBooking(BookingCreationQuery query) throws Exception {
        if (!Data.facilityList.containsKey(query.getFacilityName())) {
            throw new Exception("Facility Not Found");
        }

        Integer start = Data.dayNameToIdxOffset.get(query.getStartDay());
        start += TimePtrOffsetConverter.timeToPtrOffset(query.getStartTime());

        Integer end = Data.dayNameToIdxOffset.get(query.getEndDay());
        end += TimePtrOffsetConverter.timeToPtrOffset(query.getEndTime());

        BitSet duration = Data.facilityAvailability.get(query.getFacilityName()).get(start, end + 1);
        int occupied = duration.cardinality();

        if (occupied > 0) {
            throw new Exception("Facility Is Not Fully Available During The Specified Period");
        }

        Booking booking = new Booking(query.getFacilityName(), query.getStartDay(), query.getStartTime(),
                query.getEndDay(), query.getEndTime());
        Data.facilityAvailability.get(query.getFacilityName()).set(start, end + 1);
        Data.bookingList.put(booking.getBookingID(), booking);

        // FIXME: this happens before replying to the booking user
        SubscriptionService.notify(query.getFacilityName());

        return new BookingCreationResponse(booking.getBookingID());
    }

    public BookingInfoResponse editBooking(BookingEditingQuery query) throws Exception {
        if (!Data.bookingList.containsKey(query.getBookingId())) {
            throw new Exception("No Booking Found With ID " + query.getBookingId());
        }

        Booking booking = Data.bookingList.get(query.getBookingId());

        if (!(Data.dayNameToIdxOffset.containsKey(booking.getStartDay()) &&
                Data.dayNameToIdxOffset.containsKey(booking.getEndDay()))) {
            throw new Exception("No Changes On Expired/Effected Booking Allowed");
        }

        try {
            // TODO delete old booking
            // TODO create new booking
            return null;
        } catch (Exception e) {
            // TODO recover old booking
            throw new Exception("Booking Changes Failed: " + e.getMessage());
        }
    }

}
