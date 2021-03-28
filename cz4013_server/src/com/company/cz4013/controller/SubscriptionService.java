package com.company.cz4013.controller;

import com.company.cz4013.Data;
import com.company.cz4013.Main;
import com.company.cz4013.base.client.BaseUdpMsg;
import com.company.cz4013.base.dto.BaseXYZZMessage;
import com.company.cz4013.base.dto.XYZZMessageType;
import com.company.cz4013.dto.query.FacilityAvailabilityQuery;
import com.company.cz4013.dto.query.FacilitySubscriptionQuery;
import com.company.cz4013.dto.response.FacilityAvailSubscriptionResponse;
import com.company.cz4013.dto.response.FacilityAvailabilityResponse;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for subscription related operations
 */
public class SubscriptionService {

    /**
     * Pool of threads used for notification
     */
    private static final Map<UUID, Thread> notificationMap = new ConcurrentHashMap<>();


    /**
     * Subscription record for each facility
     */
    private static final Map<String, ArrayList<Subscription>> subscription = new HashMap<>();
    static {
        Data.facilityList.forEach((key, value) -> {
            subscription.put(key, new ArrayList<>());
        });
    }

    /**
     * Single subscription record entry
     */
    private static class Subscription{
        /**
         * Subscription ID
         */
        public String subscriptionID;
        /**
         * Client address
         */
        public InetAddress address;
        /**
         * Client port
         */
        public int port;
        /**
         * End time of the subscription
         */
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

    /**
     * Register a client for future availability notification
     * @param query A query containing the required field for subscription
     * @param clientAddress Address of the client
     * @param clientPort Port of the client
     * @return A response containing the subscription ID
     * @throws Exception Thrown when bad request encountered (e.g. Invalid Facility Name)
     */
    public FacilityAvailSubscriptionResponse register(FacilitySubscriptionQuery query, InetAddress clientAddress, int clientPort) throws Exception {
        if (!Data.facilityList.containsKey(query.getFacilityName())) {
            throw new Exception("Facility Not Found");
        }
        String subscriptionId = UUID.randomUUID().toString();

        subscription.get(query.getFacilityName()).add(new Subscription(
                subscriptionId,
                clientAddress,
                clientPort,
                new Date().getTime() + query.getSubscribeTime() * 1000L
        ));

        return new FacilityAvailSubscriptionResponse(subscriptionId);
    }

    /**
     * Stop sending messages to a client with the given ID
     * @param ackUUID ID of the subscription
     */
    public void notificationAck(UUID ackUUID) {
        System.out.println("Received Notification Acknowledgement: UUID: " + ackUUID.toString());
        Thread thread = notificationMap.getOrDefault(ackUUID, null);
        if (thread != null){
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        notificationMap.remove(ackUUID);
    }

    /**
     * Notify clients the availability change of a facility
     * @param facilityName
     */
    public static void notify(String facilityName) {

        try {
            FacilityAvailabilityQuery query = new FacilityAvailabilityQuery(facilityName, Data.dayKeywordsDisplaySequence);
            FacilityAvailabilityResponse message = new FacilityService().getFacilityAvailibity(query);
            List<Subscription> expiredSubscription = new ArrayList<>();
            BaseXYZZMessage<FacilityAvailabilityResponse> baseXYZZMessage = new BaseXYZZMessage<>();
            baseXYZZMessage.setData(message);
            baseXYZZMessage.setType(XYZZMessageType.REPLY);
            baseXYZZMessage.setMethodName("");
            for (Subscription s: subscription.get(facilityName)) {
                if (s.subscriptionEndTime >= new Date().getTime()) {
                    BaseUdpMsg msg = new BaseUdpMsg(s.address, s.port, null);
                    baseXYZZMessage.setUuId(s.subscriptionID);
                    msg.message = baseXYZZMessage;
                    Runnable runnable = new NotificationRunnable(msg);
                    Thread notification = new Thread(runnable);
                    //notificationMap.put(msg.message.getUuId(),notification);
                    notification.start();
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

    private static class NotificationRunnable implements Runnable{

        BaseUdpMsg msg;

        public NotificationRunnable(BaseUdpMsg msg){
            this.msg = msg;
        }

        /**
         * Notify a client with maximally five times and a 2000 ms interval
         */
        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                notificationMap.put(msg.message.getUuId(), Thread.currentThread());
                System.out.println("Sending Notification Msg: " + msg.returnAddress);
                Main.mainUDPServer.sendMessage(msg);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    return;
                }
            }
            notificationMap.remove(msg.message.getUuId());
            System.out.println("Failed To Notify Client: " + msg.returnAddress + ", after trying for 5 times");
        }
    }
}
