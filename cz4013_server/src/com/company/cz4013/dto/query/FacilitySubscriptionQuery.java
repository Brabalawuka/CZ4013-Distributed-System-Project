package com.company.cz4013.dto.query;

import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZFieldAnnotation;

import java.util.List;

/**
 * Query containing necessary fields to subscribe for the availability of a facility
 * Attribute names explain for themselves
 */
public class FacilitySubscriptionQuery extends BaseXYZZObject {

    @XYZZFieldAnnotation(order = 1)
    private String facilityName;
    @XYZZFieldAnnotation(order = 2)
    private int subscribeTime;


    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public int getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(int subscribeTime) {
        this.subscribeTime = subscribeTime;
    }
}
