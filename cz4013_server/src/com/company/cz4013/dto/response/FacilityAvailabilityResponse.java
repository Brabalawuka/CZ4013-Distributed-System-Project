package com.company.cz4013.dto.response;

import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZFieldAnnotation;

import java.util.List;

public class FacilityAvailabilityResponse extends BaseXYZZObject {

    @XYZZFieldAnnotation(order = 1)
    public List<String> availabilityList;

    public FacilityAvailabilityResponse(){}

    public FacilityAvailabilityResponse(List<String> availabilityList) {
        this.availabilityList = availabilityList;
    }

    public List<String> getAvailabilityList() {
        return availabilityList;
    }
}
