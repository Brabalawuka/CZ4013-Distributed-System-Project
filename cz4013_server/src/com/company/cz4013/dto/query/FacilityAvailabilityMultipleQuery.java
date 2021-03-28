package com.company.cz4013.dto.query;

import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZFieldAnnotation;

import java.util.List;

public class FacilityAvailabilityMultipleQuery extends BaseXYZZObject {


    @XYZZFieldAnnotation(order = 1)
    private List<String> facilityName;
    @XYZZFieldAnnotation(order = 2)
    private List<String> days;


    public List<String> getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(List<String> facilityName) {
        this.facilityName = facilityName;
    }


    public FacilityAvailabilityMultipleQuery() {
    }

    public FacilityAvailabilityMultipleQuery(List<String> facilityName, List<String> days) {
        this.facilityName = facilityName;
        this.days = days;
    }



    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }
}
