package com.company.cz4013.dto.response;

import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZFieldAnnotation;

import java.util.List;

/**
 * Response containing the list of all facility names
 * Attribute names explain for themselves
 */
public class FacilityNamelistResponse extends BaseXYZZObject {

    @XYZZFieldAnnotation(order = 1)
    public List<String> facilityList;

    public FacilityNamelistResponse(){}

    public FacilityNamelistResponse(List<String> facilityList) {
        this.facilityList = facilityList;
    }

    public List<String> getFacilityList() {
        return facilityList;
    }

}
