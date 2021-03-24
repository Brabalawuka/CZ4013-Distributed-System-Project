package com.company.cz4013.dto;

import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZFieldAnnotation;

import java.util.List;

public class FacilityAvailSubscriptionResponse extends BaseXYZZObject {

    @XYZZFieldAnnotation(order = 1)
    public Boolean success = true;

    public FacilityAvailSubscriptionResponse(){}
}
