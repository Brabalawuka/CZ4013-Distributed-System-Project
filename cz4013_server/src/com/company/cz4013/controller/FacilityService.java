package com.company.cz4013.controller;

import com.company.cz4013.dto.query.FacilityAvailabilityQuery;
import com.company.cz4013.dto.response.FacilityAvailabilityResponse;
import com.company.cz4013.dto.response.FacilityNamelistResponse;
import com.company.cz4013.Data;

import java.util.*;

public class FacilityService {

    public FacilityService() {
    }

    private String ptrOffsetToTime(int ptr) {
        int h = ptr / 60;
        int m = ptr % 60;

        return String.format("%02d:%02d", h, m);
    }

    private Integer timeToPtrOffset(String time) {
        String[] splitedTime = time.split(":");
        return Integer.parseInt(splitedTime[0]) * 60 + Integer.parseInt(splitedTime[1]);
    }

    public FacilityAvailabilityResponse getFacilityAvailibity(FacilityAvailabilityQuery query) throws Exception {
        if (!Data.facilityList.containsKey(query.getFacilityName())) {
            throw new Exception("Facility Not Found");
        }
        ArrayList<String> availability = new ArrayList<>();
        for (String day: query.getDays()) {
            Integer offset = Data.dayNameToIdxOffset.get(day);
            BitSet rawAvailability = Data.facilityAvailability.get(query.getFacilityName());

            // TODO parse rawAvailability to the desired format
            ArrayList<String> availIntervalList = new ArrayList<>();
            int ptr = offset;
            while (ptr < offset + 60 * 24) {
                if (rawAvailability.get(ptr)) {
                    ptr ++;
                    continue;
                }
                int start_ptr = ptr;
                while (!rawAvailability.get(ptr) && ptr < offset + 60 * 24) {
                    ptr ++;
                }
                availIntervalList.add(ptrOffsetToTime(start_ptr - offset) + "-" + ptrOffsetToTime(ptr - offset - 1));
            }

            availability.add(String.join("|", availIntervalList));
        }
        return new FacilityAvailabilityResponse(availability);
    }

    public FacilityNamelistResponse getFacilityNameList() {
        return new FacilityNamelistResponse(new ArrayList<>(Data.facilityList.keySet()));
    }

}
