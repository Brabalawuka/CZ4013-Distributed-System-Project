package com.company.cz4013.base.dto;

import java.util.UUID;

public class BaseXYZZMessage<T extends BaseXYZZObject> {

    protected UUID uuId;
    protected XYZZMessageType type;
    protected String methodName;
    protected T data;

    public boolean shouldCache() {
        return shouldCache;
    }

    public void setShouldCache(boolean shouldCache) {
        this.shouldCache = shouldCache;
    }

    private boolean shouldCache;


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

    public <K extends BaseXYZZObject> BaseXYZZMessage<K> copyToNewMessage(K object, XYZZMessageType type, boolean shouldCache){
        BaseXYZZMessage<K> baseXYZZMessage = new BaseXYZZMessage<K>();
        baseXYZZMessage.setUuId(uuId);
        baseXYZZMessage.setMethodName(methodName);
        baseXYZZMessage.setData(object);
        baseXYZZMessage.setType(type);
        baseXYZZMessage.setShouldCache(shouldCache);
        return baseXYZZMessage;
    }
}
