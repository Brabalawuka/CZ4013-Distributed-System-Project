package com.company.cz4013.base.event;

import com.company.cz4013.base.dto.BaseXYZZMessage;
import com.company.cz4013.base.dto.BaseXYZZObject;

import java.util.*;

public abstract class BasePublisher {

    private final Map<String, BaseListener> listeners = new HashMap<String, BaseListener>();


    public void attachListener(BaseListener listener){
        listeners.put(listener.getClass().getName(), listener);
    }

    public <T extends BaseXYZZObject> void notifyAllListeners(BaseXYZZMessage<T> message){
        for (BaseListener listener : listeners.values()) {
            listener.onMessage(message);
        }
    }


    //TODO: Deal with situation that listener doesnt exist
    public <T extends BaseXYZZObject, K extends BaseListener> void notifySingleListeners(Class<K> listener, BaseXYZZMessage<T> message){
        Optional.ofNullable(listeners.get(listener.getName())).ifPresentOrElse(
                baseListener -> baseListener.onMessage(message),
                () -> {}
        );
    }
}
