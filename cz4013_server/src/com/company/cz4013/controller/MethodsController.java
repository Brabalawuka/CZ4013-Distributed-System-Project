package com.company.cz4013.controller;

import com.company.cz4013.base.dto.BaseXYZZMessage;
import com.company.cz4013.base.event.BasePublisher;
import com.company.cz4013.dto.FacilityAvailabilityQuery;
import com.company.cz4013.exception.DeserialisationError;
import com.company.cz4013.util.SerialisationTool;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.HashMap;

public class MethodsController extends BasePublisher {

    public static HashMap<String, String> methodHashMap = new HashMap<>(){{
        put("FACILITY_AVAIL_CHECKING", "checkFacilityAvailibity");

    }};


    public void checkFacilityAvailability(BaseXYZZMessage<FacilityAvailabilityQuery> msg, ByteArrayInputStream stream){

        try {
            FacilityAvailabilityQuery query = SerialisationTool.deserialiseToObject(stream, new FacilityAvailabilityQuery());
            msg.setData(query);
            notifySingleListeners(BookingService.class, msg);
        } catch (DeserialisationError deserialisationError) {
            deserialisationError.printStackTrace();
        }

    }
}
