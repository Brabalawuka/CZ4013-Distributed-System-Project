package com.company.cz4013.util;

/**
 * An Util Class to convert 24hr format time string to the pointer offset in BitSet
 * Method names explain for themselves
 */
public class TimePtrOffsetConverter {

    public static String ptrOffsetToTime(int ptr) {
        int h = ptr / 60;
        int m = ptr % 60;

        return String.format("%02d:%02d", h, m);
    }

    public static Integer timeToPtrOffset(String time) {
        String[] splitedTime = time.split(":");
        return Integer.parseInt(splitedTime[0]) * 60 + Integer.parseInt(splitedTime[1]);
    }

}
