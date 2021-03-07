package com.company.cz4013.base.event;


import com.company.cz4013.base.dto.BaseXYZZMessage;
import com.company.cz4013.base.dto.BaseXYZZObject;

public abstract class BaseListener {

    public BaseListener(BasePublisher publisher){
        publisher.attachListener(this);
    }

    public abstract <T extends BaseXYZZObject> void onMessage(BaseXYZZMessage<T> message);

}
