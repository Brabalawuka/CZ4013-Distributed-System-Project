package com.company.cz4013;

import com.company.cz4013.controller.SubscriptionService;
import com.company.cz4013.model.Booking;

import java.text.SimpleDateFormat;
import java.util.*;

public class Data extends TimerTask{
    private static final Calendar calender = Calendar.getInstance();
    private static final int NUMBER_OF_MINUTE_IN_A_WEEK = 60 * 24 * 7;

    public static volatile Map<String, Boolean> facilityList = new HashMap<>(){{
        put("Tutorial Room 1", true);
        put("Tutorial Room 2", true);
        put("Tutorial Room 3", true);
        put("Tutorial Room 4", true);
        put("Tutorial Room 5", true);
        put("Tutorial Room 6", true);
        put("Tutorial Room 7", true);
    }};

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
    public static volatile List<String> dayKeywordsDisplaySequence = new ArrayList<>(dayKeywords);
    public static volatile Map<String, BitSet> facilityAvailability = new HashMap<>();
    public static volatile Map<String, Integer> dayNameToIdxOffset = new HashMap<>();
    public static volatile Map<String, Booking> bookingList = new HashMap<>();

    static {
        updatedayNameToIdxOffset();
        facilityList.keySet().forEach(facilityName -> {
            facilityAvailability.put(facilityName, new BitSet(NUMBER_OF_MINUTE_IN_A_WEEK));
        });
    }
    
    public static void editFacilityAvailabilityEntry(String facilityName) {
        // TODO: used at every booking, book changing and day switch
        // TODO: notify the users at the end of update
        // SubscriptionService.notify(facilityName);
    }

    private static void updateFacilityAvailibity() {
        // TODO: left shift all entries in facilityAvailability by 60 * 24 bits with padding 0s from right
        // TODO: notify the users at the end of update
    }

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

    private static void updateBookingDay() {
        // TODO update booking
        // called on Monday, start of a new week: any bookings with end time not starting with 'coming becomes invalid
        // all bookings with 'coming' days should drop the prefix
        // all bookings with past days as start days and coming days as end days should still be valid,
        // but the way of expressing start days should be changed
    }

    @Override
    public void run() {

        System.out.println("Updating Time Slots...");
        updateFacilityAvailibity();
        updatedayNameToIdxOffset();
        System.out.println("Finished Updating Time Slots!");

    }
}

