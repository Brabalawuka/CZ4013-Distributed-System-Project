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

    public static HashMap<String, String> methodHashMap = new HashMap<>(){{
        put("FACILITY_AVAIL_CHECKING", "checkFacilityAvailability");

    }};


    public BaseXYZZMessage<BaseXYZZObject> checkFacilityAvailability(BaseXYZZMessage<FacilityAvailabilityQuery> msg, ByteArrayInputStream stream){

        BaseXYZZMessage returnMsg = new BaseXYZZMessage<>();
        returnMsg.setUuId(msg.getUuId());
        returnMsg.setMethodName(msg.getMethodName());
        try {
            FacilityAvailabilityQuery query = SerialisationTool.deserialiseToObject(stream, new FacilityAvailabilityQuery());
            msg.setData(query);

            returnMsg.setType(XYZZMessageType.REPLY);
            notifySingleListeners(BookingService.class, msg).ifPresentOrElse(
                    returnMsg::setData, () -> {}
            );
        } catch (DeserialisationError deserialisationError) {
            returnMsg.setType(XYZZMessageType.ERROR);
            returnMsg.setData(new ErrorMessageResponse(
                   deserialisationError.getMessage()
            ));
            deserialisationError.printStackTrace();
        }
        return returnMsg;

    }
}
