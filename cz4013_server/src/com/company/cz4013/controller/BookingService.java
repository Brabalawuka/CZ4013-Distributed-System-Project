package com.company.cz4013.controller;

import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.dto.FacilityAvailabilityQuery;
import com.company.cz4013.dto.FacilityAvailabilityResponse;
import com.company.cz4013.dto.FacilityNamelistResponse;
import com.company.cz4013.Data;

import java.util.*;

public class BookingService {

    public BookingService() {
    }

    private String ptrsToTime(int ptr) {
        int h = ptr / 60;
        int m = ptr % 60;

        return String.format("%02d:%02d", h, m);
    }

    public BaseXYZZObject getFacilityAvailibity(FacilityAvailabilityQuery query) throws Exception {
        if (!Data.facilityList.containsKey(query.getFacilityName())) {
            throw new Exception("Facility Not Found");
        }
        ArrayList<String> availability = new ArrayList<>();
        for (String day: query.getDays()) {
            Integer offset = Data.dayNameToIdxOffset.get(day);
            BitSet rawAvailability = Data.facilityAvailibity.get(query.getFacilityName());

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
                availIntervalList.add(ptrsToTime(start_ptr - offset) + "-" + ptrsToTime(ptr - offset - 1));
            }

            availability.add(String.join("|", availIntervalList));
        }
        return new FacilityAvailabilityResponse(availability);
    }

    public BaseXYZZObject getFacilityNameList() {
        return new FacilityNamelistResponse(new ArrayList<>(Data.facilityList.keySet()));
    }

}
