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
        public String subscriptionID;
        public InetAddress address;
        public int port;
        public long subscriptionEndTime;

        public Subscription(String subscriptionID, InetAddress address, int port, long subscriptionEndTime) {
            this.subscriptionID = subscriptionID;
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
        String subscriptionId = UUID.randomUUID().toString();

        subscription.get(query.getFacilityName()).add(new Subscription(
                subscriptionId,
                request.getAddress(),
                request.getPort(),
                new Date().getTime() + query.getSubscribeTime() * 1000
        ));

        return new FacilityAvailSubscriptionResponse(subscriptionId);
    }

    public static void notify(String facilityName) {
        // TODO: notify client with OneWay msg and UUID as subscription ID
        // TODO: (optional) remove expired subscribers
    }
}
