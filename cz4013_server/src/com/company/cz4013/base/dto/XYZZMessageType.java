package com.company.cz4013.base.dto;

import com.company.cz4013.exception.DeserialisationError;

public enum XYZZMessageType {

    CALL,
    REPLY,
    NOTIFY,
    ERROR;


    public static XYZZMessageType fromInteger(int x) throws DeserialisationError {
        switch(x) {
            case 0:
                return CALL;
            case 1:
                return REPLY;
            case 2:
                return NOTIFY;
            case 3:
                return ERROR;
        }
        throw new DeserialisationError(String.format("Unrecognised Error type: %d", x));
    }
}
