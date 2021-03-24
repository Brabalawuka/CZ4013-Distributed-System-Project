package com.company.cz4013.controller;

import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.dto.FacilityAvailabilityQuery;
import com.company.cz4013.dto.FacilityNamelistResponse;
import com.company.cz4013.Data;

import java.util.ArrayList;

public class BookingService {


    public BookingService() {

    }



    public BaseXYZZObject getFacilityAvailibity(FacilityAvailabilityQuery query) {
        return null;

    }

    public BaseXYZZObject getFacilityNameList() {
        return new FacilityNamelistResponse(new ArrayList<>(Data.facilityList.keySet()));
    }

}
