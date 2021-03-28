package com.company.cz4013.dto.query;

import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZFieldAnnotation;

import java.util.List;

/**
 * Query containing necessary fields for checking the available slots for a facility
 * Attribute names explain for themselves
 */
public class FacilityAvailabilityQuery extends BaseXYZZObject {

    @XYZZFieldAnnotation(order = 1)
    private String facilityName;
    @XYZZFieldAnnotation(order = 2)
    private List<String> days;

    public FacilityAvailabilityQuery() {
    }

    public FacilityAvailabilityQuery(String facilityName, List<String> days) {
        this.facilityName = facilityName;
        this.days = days;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }
}
