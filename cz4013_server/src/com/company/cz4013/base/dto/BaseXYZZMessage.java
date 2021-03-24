package com.company.cz4013.base.dto;

import java.util.UUID;

public class BaseXYZZMessage<T extends BaseXYZZObject> {

    private UUID uuId;
    private XYZZMessageType type;
    private String methodName;
    private T data;


    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }



    public UUID getUuId() {
        return uuId;
    }

    public void setUuId(String uuId) {
        this.uuId = UUID.fromString(uuId);
    }
    public void setUuId(UUID uuId) {
        this.uuId = uuId;
    }

    public XYZZMessageType getType() {
        return type;
    }

    public void setType(XYZZMessageType type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
