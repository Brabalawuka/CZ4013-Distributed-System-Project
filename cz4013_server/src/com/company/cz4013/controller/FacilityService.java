package com.company.cz4013.controller;

import com.company.cz4013.dto.query.FacilityAvailabilityMultipleQuery;
import com.company.cz4013.dto.query.FacilityAvailabilityQuery;
import com.company.cz4013.dto.response.FacilityAvailabilityResponse;
import com.company.cz4013.dto.response.FacilityNamelistResponse;
import com.company.cz4013.Data;
import com.company.cz4013.util.TimePtrOffsetConverter;

import java.util.*;

public class FacilityService {

    public FacilityService() {
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
                availIntervalList.add(TimePtrOffsetConverter.ptrOffsetToTime(start_ptr - offset) + "-" +
                        TimePtrOffsetConverter.ptrOffsetToTime(ptr - offset - 1));
            }

            availability.add(String.join("|", availIntervalList));
        }
        return new FacilityAvailabilityResponse(availability);
    }
    public FacilityAvailabilityResponse getMultipleFacilityAvailibity(FacilityAvailabilityMultipleQuery query) throws Exception {
        for (String name : query.getFacilityName()) {
            if (!Data.facilityList.containsKey(name)) {
                throw new Exception("Facility Not Found");
            }
        }
        ArrayList<String> availability = new ArrayList<>();
        for (String day: query.getDays()) {
            Integer offset = Data.dayNameToIdxOffset.get(day);
            BitSet rawAvailabilityForAll = new BitSet(Data.NUMBER_OF_MINUTE_IN_A_WEEK);
            for (String name : query.getFacilityName()) {
                rawAvailabilityForAll.or(Data.facilityAvailability.get(name));
            }

            // TODO parse rawAvailability to the desired format
            ArrayList<String> availIntervalList = new ArrayList<>();
            int ptr = offset;
            while (ptr < offset + 60 * 24) {
                if (rawAvailabilityForAll.get(ptr)) {
                    ptr ++;
                    continue;
                }
                int start_ptr = ptr;
                while (!rawAvailabilityForAll.get(ptr) && ptr < offset + 60 * 24) {
                    ptr ++;
                }
                availIntervalList.add(TimePtrOffsetConverter.ptrOffsetToTime(start_ptr - offset) + "-" +
                        TimePtrOffsetConverter.ptrOffsetToTime(ptr - offset - 1));
            }

            availability.add(String.join("|", availIntervalList));
        }
        return new FacilityAvailabilityResponse(availability);
    }

    public FacilityNamelistResponse getFacilityNameList() {
        return new FacilityNamelistResponse(new ArrayList<>(Data.facilityList.keySet()));
    }

}
