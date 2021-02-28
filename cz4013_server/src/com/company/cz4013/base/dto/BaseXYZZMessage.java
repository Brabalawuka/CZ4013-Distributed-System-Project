package com.company.cz4013.base.dto;

public class BaseXYZZMessage<T extends BaseXYZZObject> {

    String seqId;
    XYZZMessageType type;
    /*
    Invocation At Least Once : 0
    Invocation At Most Once : 1
     */
    int qos;
    T data;
}
