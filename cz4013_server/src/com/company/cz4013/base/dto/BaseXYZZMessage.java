package com.company.cz4013.base.dto;

public class BaseXYZZMessage<T extends BaseXYZZObject> {

    private String uuId;
    private XYZZMessageType type;

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    private String methodName;

    private T data;

    public String getUuId() {
        return uuId;
    }

    public void setUuId(String uuId) {
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
