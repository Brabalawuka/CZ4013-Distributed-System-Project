package com.company.cz4013.dto.response;

import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZFieldAnnotation;

import java.util.List;

/**
 * Response containing the (common) available slots of facility/facilities by days
 * Attribute names explain for themselves
 */
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
