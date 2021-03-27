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
            Integer originalStartPtr = Data.dayNameToIdxOffset.get(booking.getStartDay())
                    + TimePtrOffsetConverter.timeToPtrOffset(booking.getStartTime());
            Integer originalEndPtr = Data.dayNameToIdxOffset.get(booking.getEndDay())
                    + TimePtrOffsetConverter.timeToPtrOffset(booking.getEndTime());

            Integer newStartPrt;
            Integer newEndPrt;

            if (query.getPostpone()) {
                newStartPrt = originalStartPtr + query.getShiftTime();
                newEndPrt = originalEndPtr + query.getShiftTime();
            } else {
                newStartPrt = originalStartPtr - query.getShiftTime();
                newEndPrt = originalEndPtr - query.getShiftTime();
            }

            // TODO: this allows users to book 08:00 - 10:00 at 09:00
            if (newEndPrt > Data.getNumberOfMinuteInAWeek() || newStartPrt < 0) {
                throw new Exception("Bookings Should Be Within Seven Days From Now");
            }

            BitSet record = Data.facilityAvailability.get(booking.getFacilityName());

            if (query.getPostpone()) {
                Integer overlapping = originalEndPtr - newStartPrt;
                if (overlapping >= 0) {
                    BitSet duration = record.get(originalEndPtr + 1, newEndPrt + 1);
                    int occupied = duration.cardinality();

                    if (occupied > 0) {
                        throw new Exception("Facility Not Fully Available During The New Requested Period");
                    } else {
                        record.clear(originalStartPtr, newStartPrt);
                        record.set(originalEndPtr + 1, newEndPrt + 1);
                    }
                } else {
                    eraseOldAndSetNewBooking(originalStartPtr, originalEndPtr, newStartPrt, newEndPrt, record);
                }
            } else {
                Integer overlapping = newEndPrt - originalStartPtr;
                if (overlapping >= 0) {
                    BitSet duration = record.get(newStartPrt, originalStartPtr);
                    int occupied = duration.cardinality();

                    if (occupied > 0) {
                        throw new Exception("Facility Not Fully Available During The New Requested Period");
                    } else {
                        record.clear(newEndPrt + 1, originalEndPtr + 1);
                        record.set(newStartPrt, originalStartPtr);
                    }
                } else {
                    eraseOldAndSetNewBooking(originalStartPtr, originalEndPtr, newStartPrt, newEndPrt, record);
                }
            }
            booking.setStartDay(Data.dayKeywordsDisplaySequence.get(newStartPrt / 1440));
            booking.setEndDay(Data.dayKeywordsDisplaySequence.get(newEndPrt / 1440));
            booking.setStartTime(TimePtrOffsetConverter.ptrOffsetToTime(newStartPrt - Data.dayNameToIdxOffset.get(booking.getStartDay())));
            booking.setEndTime(TimePtrOffsetConverter.ptrOffsetToTime(newEndPrt - Data.dayNameToIdxOffset.get(booking.getEndDay())));

            SubscriptionService.notify(booking.getFacilityName());
            return new BookingInfoResponse(booking);
        } catch (Exception e) {
            throw new Exception("Booking Changes Failed: " + e.getMessage());
        }
    }

    private void eraseOldAndSetNewBooking(Integer originalStartPtr, Integer originalEndPtr, Integer newStartPrt, Integer newEndPrt, BitSet record) throws Exception {
        BitSet duration = record.get(newStartPrt, newEndPrt + 1);
        int occupied = duration.cardinality();

        if (occupied > 0) {
            throw new Exception("Facility Not Fully Available During The New Requested Period");
        } else {
            record.clear(originalStartPtr, originalEndPtr + 1);
            record.set(newStartPrt, newEndPrt + 1);
        }
    }

}
