package com.company.cz4013.dto;

import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZFieldAnnotation;

import java.util.List;

public class FacilityNamelistResponse extends BaseXYZZObject {

    @XYZZFieldAnnotation(order = 1)
    public List<String> facilityList;

    public FacilityNamelistResponse(){}

    public FacilityNamelistResponse(List<String> facilityList) {
        this.facilityList = facilityList;
    }

}
