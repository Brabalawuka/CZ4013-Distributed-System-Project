package com.company.cz4013.controller;

import com.company.cz4013.Data;
import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.dto.FacilityAvailSubscriptionResponse;
import com.company.cz4013.dto.FacilitySubscriptionQuery;

import java.net.InetAddress;
import java.util.*;

public class SubscriptionService {
    // TODO a better way to store subscribers?
    private static Map<String, ArrayList<InetAddress>> subscriptionAddress = new HashMap<>();
    private static Map<String, ArrayList<Integer>> subscriptionPort = new HashMap<>();
    private static Map<String, ArrayList<Date>> subscriptionEndTime = new HashMap<>();
    static {
        Data.facilityList.keySet().forEach(facilityName -> {
            subscriptionAddress.put(facilityName, new ArrayList<>());
            subscriptionPort.put(facilityName, new ArrayList<>());
            subscriptionEndTime.put(facilityName, new ArrayList<>());
        });
    }

    public static BaseXYZZObject register(InetAddress clientAddress, int portNum, FacilitySubscriptionQuery query) throws Exception {
        if (!Data.facilityList.containsKey(query.getFacilityName())) {
            throw new Exception("Facility Not Found");
        }
        subscriptionAddress.get(query.getFacilityName()).add(clientAddress);
        subscriptionPort.get(query.getFacilityName()).add(portNum);
        Date expireTime = new Date();
        expireTime.setTime(expireTime.getTime() + query.getSubscribeTime() * 1000);
        subscriptionEndTime.get(query.getFacilityName()).add(expireTime);
        return new FacilityAvailSubscriptionResponse();
    }

    public static void notify(String facilityName) {
        // TODO: notify client
        // TODO: (optional) remove expired subscribers
    }
}
