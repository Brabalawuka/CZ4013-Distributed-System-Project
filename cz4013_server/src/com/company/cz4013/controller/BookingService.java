package com.company.cz4013.controller;

import com.company.cz4013.base.dto.BaseXYZZMessage;
import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.event.BaseListener;
import com.company.cz4013.base.event.BasePublisher;

public class BookingService extends BaseListener {


    public BookingService(BasePublisher publisher) {
        super(publisher);
    }

    @Override
    public <T extends BaseXYZZObject> void onMessage(BaseXYZZMessage<T> message) {

    }
}
