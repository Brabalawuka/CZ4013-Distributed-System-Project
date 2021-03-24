package com.company.cz4013.dto;

import com.company.cz4013.base.dto.BaseXYZZMessage;
import com.company.cz4013.base.dto.BaseXYZZObject;

import java.net.InetAddress;

public class FacilitySubscriptionRequest extends BaseXYZZMessage<FacilitySubscriptionQuery> {
    
    private InetAddress address;
    private int port;


    public FacilitySubscriptionRequest(BaseXYZZMessage baseXYZZMessage, FacilitySubscriptionQuery query, InetAddress address, int port){
        uuId = baseXYZZMessage.getUuId();
        methodName = baseXYZZMessage.getMethodName();
        type = baseXYZZMessage.getType();
        data = query;
        this.address = address;
        this.port = port;
    }



    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
