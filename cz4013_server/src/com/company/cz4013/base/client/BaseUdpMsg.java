package com.company.cz4013.base.client;

import com.company.cz4013.base.dto.BaseXYZZMessage;

import java.net.InetAddress;

public class BaseUdpMsg {

    public InetAddress returnAddress;
    public int returnPort;
    public byte[] data;
    public BaseXYZZMessage<?> message;

    public BaseUdpMsg(InetAddress returnAddress, int returnPort, byte[] data) {
        this.returnAddress = returnAddress;
        this.returnPort = returnPort;
        this.data = data;
    }
}