package com.company.cz4013.dto.response;

import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZFieldAnnotation;

import java.util.List;
import java.util.UUID;

public class FacilityAvailSubscriptionResponse extends BaseXYZZObject {
    @XYZZFieldAnnotation(order = 1)
    private String subscriptionId;

    public FacilityAvailSubscriptionResponse(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }
}
