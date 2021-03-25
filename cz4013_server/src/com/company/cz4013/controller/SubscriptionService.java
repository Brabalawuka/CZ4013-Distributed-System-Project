package com.company.cz4013.controller;

import com.company.cz4013.Data;
import com.company.cz4013.Main;
import com.company.cz4013.base.client.BaseUdpMsg;
import com.company.cz4013.base.dto.BaseXYZZMessage;
import com.company.cz4013.base.dto.XYZZMessageType;
import com.company.cz4013.dto.*;

import java.lang.reflect.Method;
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
        try {
            FacilityAvailabilityQuery query = new FacilityAvailabilityQuery(facilityName, Data.dayKeywordsDisplaySequence);
            FacilityAvailabilityResponse message = new BookingService().getFacilityAvailibity(query);
            List<Subscription> expiredSubscription = new ArrayList<>();
            for (Subscription s: subscription.get(facilityName)) {
                if (s.subscriptionEndTime >= new Date().getTime()) {
                    BaseUdpMsg msg = new BaseUdpMsg(s.address, s.port, null);
                    BaseXYZZMessage<FacilityAvailabilityResponse> baseXYZZMessage = new BaseXYZZMessage<>();
                    baseXYZZMessage.setUuId(s.subscriptionID);
                    baseXYZZMessage.setData(message);
                    baseXYZZMessage.setType(XYZZMessageType.NOTIFY);
                    msg.message = baseXYZZMessage;
                    Main.mainUDPServer.sendMessage(msg);
                } else {
                    expiredSubscription.add(s);
                }
            }
            subscription.get(facilityName).removeAll(expiredSubscription);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Notification Failed...");
        }
    }
}
