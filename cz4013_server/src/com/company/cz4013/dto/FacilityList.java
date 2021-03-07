package com.company.cz4013.dto;

import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZFieldAnnotation;

import java.util.List;

public class FacilityList extends BaseXYZZObject {

    @XYZZFieldAnnotation(order = 1)
    public int testingInt;

    @XYZZFieldAnnotation(order = 2)
    public List<String> facilityList;

    public FacilityList(){}

    public FacilityList(int testingInt, List<String> facilityList) {
        this.testingInt = testingInt;
        this.facilityList = facilityList;
    }



}
