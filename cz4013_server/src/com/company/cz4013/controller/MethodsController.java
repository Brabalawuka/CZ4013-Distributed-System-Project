package com.company.cz4013.controller;

import com.company.cz4013.base.client.BaseUdpMsg;
import com.company.cz4013.base.dto.XYZZMessageType;
import com.company.cz4013.base.event.BasePublisher;
import com.company.cz4013.dto.*;
import com.company.cz4013.util.SerialisationTool;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

public class MethodsController extends BasePublisher {

    private BookingService bookingService;
    private SubscriptionService subscriptionService;

    public MethodsController (){

        bookingService = new BookingService();
        subscriptionService = new SubscriptionService();
    }

    public static HashMap<String, String> methodHashMap = new HashMap<>(){{
        put("FACILITY_AVAIL_CHECKING", "checkFacilityAvailability");
        put("FACILITY_NAMELIST_CHECKING", "checkFacilityNameList");
        put("FACILITY_AVAIL_CHECKING_SUBSCRIPTION", "subscribeToFacilityAvailability");
    }};


    public BaseUdpMsg checkFacilityAvailability(BaseUdpMsg msg, ByteArrayInputStream stream){

        try {
            FacilityAvailabilityQuery query = SerialisationTool.deserialiseToObject(stream, new FacilityAvailabilityQuery());
            msg.message = msg.message.copyToNewMessage(bookingService.getFacilityAvailibity(query), XYZZMessageType.REPLY, false);

        } catch (Exception e) {
            msg.message = msg.message.copyToNewMessage(new ErrorMessageResponse(
                    e.getMessage()
            ), XYZZMessageType.ERROR, false);
            e.printStackTrace();
        }
        return msg;

    }

    public BaseUdpMsg checkFacilityNameList(BaseUdpMsg msg, ByteArrayInputStream stream){

        try {
            msg.message = msg.message.copyToNewMessage(bookingService.getFacilityNameList(), XYZZMessageType.REPLY, true);
        } catch (Exception e) {
            msg.message = msg.message.copyToNewMessage(new ErrorMessageResponse(
                    e.getMessage()
            ), XYZZMessageType.ERROR, false);
            e.printStackTrace();
        }

        return msg;

    }

    public BaseUdpMsg subscribeToFacilityAvailability(BaseUdpMsg msg, ByteArrayInputStream stream){

        try {
            FacilitySubscriptionQuery query = SerialisationTool.deserialiseToObject(stream, new FacilitySubscriptionQuery());
            FacilityAvailSubscriptionResponse response = subscriptionService.register(new FacilitySubscriptionRequest(msg.message, query, msg.returnAddress,msg.returnPort));
            msg.message = msg.message.copyToNewMessage(response, XYZZMessageType.REPLY, true);
        } catch (Exception e) {
            msg.message = msg.message.copyToNewMessage(new ErrorMessageResponse(
                    e.getMessage()
            ), XYZZMessageType.ERROR, false);
            e.printStackTrace();
        }
        return msg;

    }
}
