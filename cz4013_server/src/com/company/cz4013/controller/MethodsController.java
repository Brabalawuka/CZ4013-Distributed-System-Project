package com.company.cz4013.controller;

import com.company.cz4013.base.client.BaseUdpMsg;
import com.company.cz4013.base.dto.XYZZMessageType;
import com.company.cz4013.base.event.BasePublisher;
import com.company.cz4013.dto.query.*;
import com.company.cz4013.dto.response.ErrorMessageResponse;
import com.company.cz4013.dto.response.FacilityAvailSubscriptionResponse;
import com.company.cz4013.util.SerialisationTool;
import com.company.cz4013.util.XYZZByteReader;
import com.company.cz4013.util.XYZZByteWriter;

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
        put("FACILITY_BOOKING_EXTEND", "extendingCurrentBooking");
        put("FACILITY_AVAIL_CHECKING_MULTIPLE", "checkFacilityAvailabilityMultiPle");
    }};


    public BaseUdpMsg checkFacilityAvailability(BaseUdpMsg msg, XYZZByteReader reader){

        try {
            FacilityAvailabilityQuery query = SerialisationTool.deserialiseToObject(reader, new FacilityAvailabilityQuery());
            msg.message = msg.message.copyToNewMessage(facilityService.getFacilityAvailibity(query), XYZZMessageType.REPLY, false);

        } catch (Exception e) {
            msg.message = msg.message.copyToNewMessage(new ErrorMessageResponse(
                    e.getMessage()
            ), XYZZMessageType.ERROR, false);
            e.printStackTrace();
        }
        return msg;

    }
    public BaseUdpMsg checkFacilityAvailabilityMultiPle(BaseUdpMsg msg, XYZZByteReader reader){

        try {
            FacilityAvailabilityMultipleQuery query = SerialisationTool.deserialiseToObject(reader, new FacilityAvailabilityMultipleQuery());
            msg.message = msg.message.copyToNewMessage(facilityService.getMultipleFacilityAvailibity(query), XYZZMessageType.REPLY, false);

        } catch (Exception e) {
            msg.message = msg.message.copyToNewMessage(new ErrorMessageResponse(
                    e.getMessage()
            ), XYZZMessageType.ERROR, false);
            e.printStackTrace();
        }
        return msg;

    }


    public BaseUdpMsg checkFacilityNameList(BaseUdpMsg msg, XYZZByteReader reader){

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

    public BaseUdpMsg subscribeToFacilityAvailability(BaseUdpMsg msg, XYZZByteReader reader){

        try {
            FacilitySubscriptionQuery query = SerialisationTool.deserialiseToObject(reader, new FacilitySubscriptionQuery());
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

    public BaseUdpMsg checkBookingInfo(BaseUdpMsg msg, XYZZByteReader reader){

        try {
            BookingInfoQuery query = SerialisationTool.deserialiseToObject(reader, new BookingInfoQuery());
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
    public BaseUdpMsg createNewBooking(BaseUdpMsg msg, XYZZByteReader reader){

        try {
            BookingCreationQuery query = SerialisationTool.deserialiseToObject(reader, new BookingCreationQuery());
            msg.message = msg.message.copyToNewMessage(bookingService.creatBooking(query), XYZZMessageType.REPLY, true);
        } catch (Exception e) {
            msg.message = msg.message.copyToNewMessage(new ErrorMessageResponse(
                    e.getMessage()
            ), XYZZMessageType.ERROR, false);
            e.printStackTrace();
        }

        return msg;

    }

    public BaseUdpMsg editCurrentBooking(BaseUdpMsg msg, XYZZByteReader reader){

        try {
            BookingEditingQuery query = SerialisationTool.deserialiseToObject(reader, new BookingEditingQuery());
            msg.message = msg.message.copyToNewMessage(bookingService.editBooking(query), XYZZMessageType.REPLY, true);
        } catch (Exception e) {
            msg.message = msg.message.copyToNewMessage(new ErrorMessageResponse(
                    e.getMessage()
            ), XYZZMessageType.ERROR, false);
            e.printStackTrace();
        }

        return msg;

    }

    public BaseUdpMsg extendingCurrentBooking(BaseUdpMsg msg, XYZZByteReader reader){

        try {
            BookingExtendingQuery query = SerialisationTool.deserialiseToObject(reader, new BookingExtendingQuery());
            msg.message = msg.message.copyToNewMessage(bookingService.extendBooking(query), XYZZMessageType.REPLY, true);
        } catch (Exception e) {
            msg.message = msg.message.copyToNewMessage(new ErrorMessageResponse(
                    e.getMessage()
            ), XYZZMessageType.ERROR, false);
            e.printStackTrace();
        }
        return msg;

    }

}
