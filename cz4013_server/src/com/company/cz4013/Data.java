package com.company.cz4013;

import com.company.cz4013.controller.SubscriptionService;
import com.company.cz4013.dto.model.Booking;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Data Pool to hold all data.
 * Serves as the in-memory database
 */
public class Data extends TimerTask{
    private static final Calendar calender = Calendar.getInstance();

    public static final int NUMBER_OF_MINUTE_IN_A_WEEK = 60 * 24 * 7;

    public static int getNumberOfMinuteInAWeek() {
        return NUMBER_OF_MINUTE_IN_A_WEEK;
    }

    /**
     * Name list of all the available facilities on server
     */
    public static volatile Map<String, Boolean> facilityList = new HashMap<>(){{
        put("Tut1", true);
        put("Tut2", true);
        put("Tut3", true);
        put("Tut4", true);
        put("LT1", true);
        put("LT2", true);
        put("LT3", true);
    }};

    /**
     * Days in string format
     */
    private static final List<String> dayKeywords = new ArrayList<>() {{
        add("Mon");
        add("Tue");
        add("Wed");
        add("Thu");
        add("Fri");
        add("Sat");
        add("Sun");
    }};

    /*
    The facility Availibity is coded using a bit set with length of total minutes in a week
     */
    /**
     * Augmented Days depending on the current time
     */
    public static volatile List<String> dayKeywordsDisplaySequence = new ArrayList<>(dayKeywords);
    /**
     * Facility availability of today and the coming 7 days
     */
    public static volatile Map<String, BitSet> facilityAvailability = new HashMap<>();
    /**
     * Maps day name to the 00:00 offsets of that day in the BitSet
     */
    public static volatile Map<String, Integer> dayNameToIdxOffset = new HashMap<>();
    public static volatile Map<String, Booking> bookingList = new HashMap<>();

    static {
        updatedayNameToIdxOffset();
        facilityList.keySet().forEach(facilityName -> {
            facilityAvailability.put(facilityName, new BitSet(NUMBER_OF_MINUTE_IN_A_WEEK));
        });
    }

    /**
     * Update the facility availability every day 00:00 by shifting bits
     */
    private static void updateFacilityAvailibity() {
        facilityAvailability.forEach(((key, value) -> {
            facilityAvailability.put(key, value.get(60 * 24, 60 * 24 + NUMBER_OF_MINUTE_IN_A_WEEK));
            SubscriptionService.notify(key);
        }));
    }

    /**
     * Update the day name to offset mapping every day 00:00
     */
    private static void updatedayNameToIdxOffset() {
        Date date = calender.getTime();
        String currentDay = new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime());
        int zeroOffSetPosition = dayKeywords.indexOf(currentDay);
        int i;
        String day;
        for (i = 0; i < dayKeywords.size(); i++) {
            day= dayKeywords.get(i);
            int newIdx;
            if (i < zeroOffSetPosition) {
                newIdx = i + 7 - zeroOffSetPosition;
                String keyName = "Coming " + day;
                dayNameToIdxOffset.put(keyName, newIdx* 24 * 60);
                dayKeywordsDisplaySequence.set(newIdx, keyName);
            } else {
                newIdx = i - zeroOffSetPosition;
                dayNameToIdxOffset.put(day, newIdx * 24 * 60);
                dayKeywordsDisplaySequence.set(newIdx, day);
            }
        }
        if (zeroOffSetPosition == 0) {
            updateBookingDay();
        }
    }

    /**
     * Update the name of startDay and endDay for each booking entries every Monday
     */
    private static void updateBookingDay() {
        // TODO to be tested
        Pattern pattern = Pattern.compile("^Coming", Pattern.CASE_INSENSITIVE);
        for (String bookingId: bookingList.keySet()) {
            Booking booking = bookingList.get(bookingId);
            boolean isComing = pattern.matcher(booking.getStartDay()).find();
            if (isComing) {
                booking.setStartDay(booking.getStartDay().split(" ")[0]);
            } else if (booking.getStartDay().length() == 3) {
                // Not starting with coming; Now have expired
                booking.setStartDay("Past " + booking.getStartDay());
            }  // Do nothing to already expired bookings

            isComing = pattern.matcher(booking.getEndDay()).find();
            if (isComing) {
                booking.setEndDay(booking.getEndDay().split(" ")[0]);
            } else if (booking.getEndDay().length() == 3) {
                booking.setEndDay("Past " + booking.getEndDay());
            }
        }
    }

    /**
     * Cron job to update the data
     */
    @Override
    public void run() {
        System.out.println("Updating Time Slots...");
        updatedayNameToIdxOffset();
        updateFacilityAvailibity();
        System.out.println("Finished Updating Time Slots!");

    }
}

