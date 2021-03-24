package com.company.cz4013;

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

    public static volatile Map<String, Booking> bookingList = new HashMap<>();

    /*
    The facility Availibity is coded using a bit set with length of total minutes in a week
     */
    public static volatile Map<String, BitSet> facilityAvailibity = new HashMap<>();

    static {
        updatedayNameToIdxOffset();
        facilityList.keySet().forEach(facilityName -> {
            facilityAvailibity.put(facilityName, new BitSet(NUMBER_OF_MINUTE_IN_A_WEEK));
        });
    }

    private static final List<String> dayKeywords = new ArrayList<>() {{
        add("Mon");
        add("Tue");
        add("Wed");
        add("Thu");
        add("Fri");
        add("Sat");
        add("Sun");
    }};

    public static volatile Map<String, Integer> dayNameToIdxOffset = new HashMap<>();


    private static void updateFacilityAvailibity() {
        // TODO to be tested
        facilityAvailibity.forEach((key,value) ->
                facilityAvailibity.put(key, value.get(60 * 24, Math.max(60 * 24, value.length()))));
    }

    private static void updatedayNameToIdxOffset() {
        Date date = calender.getTime();
        String currentDay = new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime());
        int zeroOffSetPosition = dayKeywords.indexOf(currentDay);
        int i;
        String day;
        for (i = 0; i < dayKeywords.size(); i++) {
            day= dayKeywords.get(i);
            if (i < zeroOffSetPosition) {
                dayNameToIdxOffset.put("Coming " + day, (i + 7 - zeroOffSetPosition) * 24 * 60);
            } else {
                dayNameToIdxOffset.put(day, (i - zeroOffSetPosition) * 24 * 60);
            }
        }
    }

    private static void updateBookingDay() {
        // TODO update booking i.e. delete out-dated booking and Coming XXX -> XXX on every monday
    }

    @Override
    public void run() {

        System.out.println("Updating Time Slots...");
        updateFacilityAvailibity();
        updatedayNameToIdxOffset();
        updateBookingDay();
        System.out.println("Finished Updating Time Slots!");

    }
}

