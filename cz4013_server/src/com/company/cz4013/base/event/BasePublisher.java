package com.company.cz4013.base.event;

import com.company.cz4013.base.dto.BaseXYZZMessage;
import com.company.cz4013.base.dto.BaseXYZZObject;

import java.util.*;

public abstract class BasePublisher {

    private final Map<String, BaseListener> listenerMap = new HashMap<String, BaseListener>();


    public void attachListener(BaseListener listener){
        listenerMap.put(listener.getClass().getName(), listener);
    }

    public <T extends BaseXYZZObject> void notifyAllListeners(BaseXYZZMessage<T> message){
        for (BaseListener listener : listenerMap.values()) {
            listener.onMessage(message);
        }
    }


    //TODO: Deal with situation that listener doesnt exist
    public <T extends BaseXYZZObject, K extends BaseListener> Optional<BaseXYZZObject> notifySingleListeners(Class<K> listener, BaseXYZZMessage<T> message){
        return Optional.ofNullable(listenerMap.get(listener.getName())).flatMap(l -> l.onMessage(message));
    }
}
