package com.company.cz4013.controller;

import com.company.cz4013.base.client.BaseUdpMsg;
import com.company.cz4013.base.dto.XYZZMessageType;
import com.company.cz4013.base.event.BasePublisher;
import com.company.cz4013.dto.query.*;
import com.company.cz4013.dto.response.ErrorMessageResponse;
import com.company.cz4013.dto.response.FacilityAvailSubscriptionResponse;
import com.company.cz4013.util.SerialisationTool;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

public class MethodsController extends BasePublisher {

    private BookingService bookingService;
    private FacilityService facilityService;
    private SubscriptionService subscriptionService;

    public MethodsController (){
        bookingService = new BookingService();
        facilityService = new FacilityService();
        subscriptionService = new SubscriptionService();
    }

    public static HashMap<String, String> methodHashMap = new HashMap<>(){{
        put("FACILITY_AVAIL_CHECKING", "checkFacilityAvailability");
        put("FACILITY_NAMELIST_CHECKING", "checkFacilityNameList");
        put("FACILITY_AVAIL_CHECKING_SUBSCRIPTION", "subscribeToFacilityAvailability");
        put("FACILITY_BOOKING_CHECKING", "checkBookingInfo");
        put("FACILITY_BOOKING", "createNewBooking");
        put("FACILITY_BOOKING_AMENDMENT", "editCurrentBooking");
    }};


    public BaseUdpMsg checkFacilityAvailability(BaseUdpMsg msg, ByteArrayInputStream stream){

        try {
            FacilityAvailabilityQuery query = SerialisationTool.deserialiseToObject(stream, new FacilityAvailabilityQuery());
            msg.message = msg.message.copyToNewMessage(facilityService.getFacilityAvailibity(query), XYZZMessageType.REPLY, false);

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
            msg.message = msg.message.copyToNewMessage(facilityService.getFacilityNameList(), XYZZMessageType.REPLY, false);
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
            FacilityAvailSubscriptionResponse response = subscriptionService.register(query, msg.returnAddress,msg.returnPort);
            msg.message = msg.message.copyToNewMessage(response, XYZZMessageType.REPLY, true);
        } catch (Exception e) {
            msg.message = msg.message.copyToNewMessage(new ErrorMessageResponse(
                    e.getMessage()
            ), XYZZMessageType.ERROR, false);
            e.printStackTrace();
        }
        return msg;

    }

    public BaseUdpMsg checkBookingInfo(BaseUdpMsg msg, ByteArrayInputStream stream){

        try {
            BookingInfoQuery query = SerialisationTool.deserialiseToObject(stream, new BookingInfoQuery());
            msg.message = msg.message.copyToNewMessage(bookingService.getBookingInfo(query), XYZZMessageType.REPLY, false);
        } catch (Exception e) {
            msg.message = msg.message.copyToNewMessage(new ErrorMessageResponse(
                    e.getMessage()
            ), XYZZMessageType.ERROR, false);
            e.printStackTrace();
        }

        return msg;

    }

    // TODO these methods are highly similar, can further abstract them
    public BaseUdpMsg createNewBooking(BaseUdpMsg msg, ByteArrayInputStream stream){

        try {
            BookingCreationQuery query = SerialisationTool.deserialiseToObject(stream, new BookingCreationQuery());
            msg.message = msg.message.copyToNewMessage(bookingService.creatBooking(query), XYZZMessageType.REPLY, true);
        } catch (Exception e) {
            msg.message = msg.message.copyToNewMessage(new ErrorMessageResponse(
                    e.getMessage()
            ), XYZZMessageType.ERROR, false);
            e.printStackTrace();
        }

        return msg;

    }

    public BaseUdpMsg editCurrentBooking(BaseUdpMsg msg, ByteArrayInputStream stream){

        try {
            BookingEditingQuery query = SerialisationTool.deserialiseToObject(stream, new BookingEditingQuery());
            msg.message = msg.message.copyToNewMessage(bookingService.editBooking(query), XYZZMessageType.REPLY, true);
        } catch (Exception e) {
            msg.message = msg.message.copyToNewMessage(new ErrorMessageResponse(
                    e.getMessage()
            ), XYZZMessageType.ERROR, false);
            e.printStackTrace();
        }

        return msg;

    }

}
