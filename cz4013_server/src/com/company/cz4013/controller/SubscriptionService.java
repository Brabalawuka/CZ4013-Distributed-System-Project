package com.company.cz4013.controller;

import com.company.cz4013.Data;
import com.company.cz4013.dto.FacilityAvailSubscriptionResponse;
import com.company.cz4013.dto.FacilitySubscriptionQuery;
import com.company.cz4013.dto.FacilitySubscriptionRequest;

import java.net.InetAddress;
import java.util.*;

public class SubscriptionService {


    private static final Map<String, ArrayList<Subscription>> subscription = new HashMap<>();

    private static class Subscription{

        public InetAddress address;
        public int port;
        public long subscriptionEndTime;

        public Subscription(InetAddress address, int port, long subscriptionEndTime) {
            this.address = address;
            this.port = port;
            this.subscriptionEndTime = subscriptionEndTime;
        }
    }

    public SubscriptionService(){
        Data.facilityList.keySet().forEach(facilityName -> {
            subscription.put(facilityName, new ArrayList<>());
        });
    }

    public FacilityAvailSubscriptionResponse register(FacilitySubscriptionRequest request) throws Exception {
        FacilitySubscriptionQuery query = request.getData();
        if (!Data.facilityList.containsKey(query.getFacilityName())) {
            throw new Exception("Facility Not Found");
        }
        subscription.get(query.getFacilityName()).add(new Subscription(
                request.getAddress(),
                request.getPort(),
                new Date().getTime() + query.getSubscribeTime() * 1000
        ));
        return new FacilityAvailSubscriptionResponse();
    }

    public static void notify(String facilityName) {
        // TODO: notify client
        // TODO: (optional) remove expired subscribers
    }
}
