package com.company.cz4013.controller;

import com.company.cz4013.base.dto.BaseXYZZMessage;
import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZMessageType;
import com.company.cz4013.base.event.BasePublisher;
import com.company.cz4013.dto.ErrorMessageResponse;
import com.company.cz4013.dto.FacilityAvailabilityQuery;
import com.company.cz4013.exception.DeserialisationError;
import com.company.cz4013.util.SerialisationTool;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.HashMap;

public class MethodsController extends BasePublisher {

    BookingService bookingService;

    public MethodsController (){
        bookingService = new BookingService();
    }

    public static HashMap<String, String> methodHashMap = new HashMap<>(){{
        put("FACILITY_AVAIL_CHECKING", "checkFacilityAvailability");
        put("FACILITY_NAMELIST_CHECKING", "checkFacilityNameList");
    }};


    public BaseXYZZMessage<BaseXYZZObject> checkFacilityAvailability(BaseXYZZMessage<FacilityAvailabilityQuery> msg, ByteArrayInputStream stream){

        BaseXYZZMessage returnMsg = new BaseXYZZMessage<>();
        returnMsg.setUuId(msg.getUuId());
        returnMsg.setMethodName(msg.getMethodName());
        try {
            FacilityAvailabilityQuery query = SerialisationTool.deserialiseToObject(stream, new FacilityAvailabilityQuery());
            msg.setData(query);

            returnMsg.setType(XYZZMessageType.REPLY);
            bookingService.getFacilityAvailibity(msg.getData());
        } catch (Exception e) {
            returnMsg.setType(XYZZMessageType.ERROR);
            returnMsg.setData(new ErrorMessageResponse(
                   e.getMessage()
            ));
            e.printStackTrace();
        }
        return returnMsg;

    }

    public BaseXYZZMessage<BaseXYZZObject> checkFacilityNameList(BaseXYZZMessage<FacilityAvailabilityQuery> msg, ByteArrayInputStream stream){

        BaseXYZZMessage returnMsg = new BaseXYZZMessage<>();
        returnMsg.setUuId(msg.getUuId());
//        returnMsg.setMethodName(msg.getMethodName());
        try {
            returnMsg.setType(XYZZMessageType.REPLY);
            returnMsg.setData(bookingService.getFacilityNameList());
        } catch (Exception e) {
            returnMsg.setType(XYZZMessageType.ERROR);
            returnMsg.setData(new ErrorMessageResponse(
                    e.getMessage()
            ));
            e.printStackTrace();
        }
        return returnMsg;

    }
}
