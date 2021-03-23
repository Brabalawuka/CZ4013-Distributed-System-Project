package com.company.cz4013.base.event;


import com.company.cz4013.base.dto.BaseXYZZMessage;
import com.company.cz4013.base.dto.BaseXYZZObject;

import javax.swing.text.html.Option;
import java.util.Optional;

public abstract class BaseListener {

    public BaseListener(BasePublisher publisher){
        publisher.attachListener(this);
    }

    public abstract <T extends BaseXYZZObject> Optional<BaseXYZZObject> onMessage(BaseXYZZMessage<T> message);

}
