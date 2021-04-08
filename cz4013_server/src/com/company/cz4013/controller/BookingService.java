package com.company.cz4013.controller;

import com.company.cz4013.Data;
import com.company.cz4013.dto.model.Booking;
import com.company.cz4013.dto.query.BookingCreationQuery;
import com.company.cz4013.dto.query.BookingEditingQuery;
import com.company.cz4013.dto.query.BookingExtendingQuery;
import com.company.cz4013.dto.query.BookingInfoQuery;
import com.company.cz4013.dto.response.BookingCreationResponse;
import com.company.cz4013.dto.response.BookingInfoResponse;
import com.company.cz4013.util.TimePtrOffsetConverter;


import java.util.BitSet;

/**
 * Service for booking related operations
 */
public class BookingService {

    public BookingService() {
    }

    /**
     * Get the information of a booking
     * @param query A query containing the required field for lookup
     * @return A response containing the required booking information
     * @throws Exception Thrown when bad request encountered (e.g. No Booking Found)
     */
    public BookingInfoResponse getBookingInfo(BookingInfoQuery query) throws Exception {
        if (!Data.bookingList.containsKey(query.getBookingId())) {
            throw new Exception("No Booking Found With ID " + query.getBookingId());
        }
        return new BookingInfoResponse(Data.bookingList.get(query.getBookingId()));
    }

    /**
     * Create a new booking entry
     * @param query A query containing the required field to make a booking
     * @return A response containing the id of the newly created booking
     * @throws Exception Thrown when bad request encountered (e.g. No Corresponding Facility Found)
     */
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

    /**
     * Make shift amendments to an exciting booking
     * @param query A query containing the required field for lookup and editing
     * @return A response containing the updated information about the booking
     * @throws Exception Thrown when bad request encountered (e.g. Facility Not Available)
     */
    public BookingInfoResponse editBooking(BookingEditingQuery query) throws Exception {
        Booking booking = Data.bookingList.getOrDefault(query.getBookingId(),null);
        if(booking == null){
            throw new Exception("No Booking Found With ID " + query.getBookingId());
        }
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
                throw new Exception("Booking's StartTime and EndTime Should Be Within Seven Days From Now");
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

    /**
     * Erase the outdated booking history and update the booking by writing new bits
     * @param originalStartPtr original starting position of the booking in BitSet
     * @param originalEndPtr original ending position of the booking in BitSet
     * @param newStartPrt new starting position of the booking in BitSet
     * @param newEndPrt new ending position of the booking in BitSet
     * @param record BitSet to be checked and changed
     * @throws Exception Thrown when the BitMap already has some bit set between newStartPrt and newEndPrt
     */
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

    /**
     * Extend a booking
     * @param query A query containing the required field for lookup and editing
     * @return A response containing the updated information about the booking
     * @throws Exception Thrown when bad request encountered (e.g. Facility Not Available)
     */
    public BookingInfoResponse extendBooking(BookingExtendingQuery query) throws Exception {

        Booking booking = Data.bookingList.getOrDefault(query.getBookingId(),null);
        if(booking == null){
            throw new Exception("No Booking Found With ID " + query.getBookingId());
        }

        if (!(Data.dayNameToIdxOffset.containsKey(booking.getStartDay()) &&
                Data.dayNameToIdxOffset.containsKey(booking.getEndDay()))) {
            throw new Exception("No Changes On Expired/Effected Booking Allowed");
        }

        try {
            Integer originalStartPtr = Data.dayNameToIdxOffset.get(booking.getStartDay())
                    + TimePtrOffsetConverter.timeToPtrOffset(booking.getStartTime());
            Integer originalEndPtr = Data.dayNameToIdxOffset.get(booking.getEndDay())
                    + TimePtrOffsetConverter.timeToPtrOffset(booking.getEndTime());

            Integer newEndPrt = originalEndPtr + query.getExtendingTime();

            if (newEndPrt > Data.getNumberOfMinuteInAWeek()) {
                throw new Exception("Booking's StartTime and EndTime Should Be Within Seven Days From Now");
            }

            BitSet record = Data.facilityAvailability.get(booking.getFacilityName());
            BitSet duration = record.get(originalEndPtr + 1, newEndPrt);
            if (duration.cardinality() > 0) {
                throw new Exception("Facility Not Fully Available During The New Requested Period");
            } else {
                record.set(originalStartPtr, newEndPrt + 1);
            }

            booking.setStartDay(Data.dayKeywordsDisplaySequence.get(originalStartPtr / 1440));
            booking.setEndDay(Data.dayKeywordsDisplaySequence.get(newEndPrt / 1440));
            booking.setStartTime(TimePtrOffsetConverter.ptrOffsetToTime(originalStartPtr - Data.dayNameToIdxOffset.get(booking.getStartDay())));
            booking.setEndTime(TimePtrOffsetConverter.ptrOffsetToTime(newEndPrt - Data.dayNameToIdxOffset.get(booking.getEndDay())));

            SubscriptionService.notify(booking.getFacilityName());
            return new BookingInfoResponse(booking);
        } catch (Exception e) {
            throw new Exception("Booking Changes Failed: " + e.getMessage());
        }
    }

}
