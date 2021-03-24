package com.company.cz4013;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class Data {

    private static final int NUMBER_OF_MINUTE_IN_A_WEEK = 60 * 24 * 7;


    public static Map<String, Boolean> facilityList = new HashMap<>(){{
        put("Tutorial Room 1", true);
        put("Tutorial Room 2", true);
        put("Tutorial Room 3", true);
        put("Tutorial Room 4", true);
        put("Tutorial Room 5", true);
        put("Tutorial Room 6", true);
        put("Tutorial Room 7", true);
    }};


    /*
    The facility Availibity is coded using a bit set with length of total minutes in a week
     */
    public static Map<String, BitSet> facilityAvailibity = new HashMap<>(){{
        put("Tutorial Room 1", new BitSet(NUMBER_OF_MINUTE_IN_A_WEEK));
        put("Tutorial Room 2", new BitSet(NUMBER_OF_MINUTE_IN_A_WEEK));
        put("Tutorial Room 3", new BitSet(NUMBER_OF_MINUTE_IN_A_WEEK));
        put("Tutorial Room 4", new BitSet(NUMBER_OF_MINUTE_IN_A_WEEK));
        put("Tutorial Room 5", new BitSet(NUMBER_OF_MINUTE_IN_A_WEEK));
        put("Tutorial Room 6", new BitSet(NUMBER_OF_MINUTE_IN_A_WEEK));
        put("Tutorial Room 7", new BitSet(NUMBER_OF_MINUTE_IN_A_WEEK));
    }};
}
