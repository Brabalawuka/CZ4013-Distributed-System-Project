package com.company.cz4013;

import com.company.cz4013.controller.SubscriptionService;
import com.company.cz4013.dto.model.Booking;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

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
        facilityAvailability.forEach(((key, value) -> {
            facilityAvailability.put(key, value.get(60 * 24, 60 * 24 + NUMBER_OF_MINUTE_IN_A_WEEK));
            SubscriptionService.notify(key);
        }));
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

    @Override
    public void run() {
        System.out.println("Updating Time Slots...");
        updatedayNameToIdxOffset();
        updateFacilityAvailibity();
        System.out.println("Finished Updating Time Slots!");

    }
}

