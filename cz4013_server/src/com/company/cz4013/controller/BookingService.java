package com.company.cz4013.controller;

import com.company.cz4013.Data;
import com.company.cz4013.dto.query.BookingInfoQuery;
import com.company.cz4013.dto.response.BookingInfoResponse;

public class BookingService {

    public BookingService() {
    }

    public BookingInfoResponse getBookingInfo(BookingInfoQuery query) throws Exception {
        if (!Data.bookingList.containsKey(query.getBookingId())) {
            throw new Exception("No Booking Found With ID " + query.getBookingId());
        }
        return new BookingInfoResponse(Data.bookingList.get(query.getBookingId()));
    }

}
