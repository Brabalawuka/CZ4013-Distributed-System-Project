package com.company.cz4013.base.client;

import com.company.cz4013.base.dto.BaseXYZZMessage;

import java.net.InetAddress;

/**
 * Base UDP Message
 */
public class BaseUdpMsg {

    /**
     * Address of the requester
     */
    public InetAddress returnAddress;
    /**
     * Port of the requester
     */
    public int returnPort;

    /**
     * Data to be sent back to the requester in bytes
     */
    public byte[] data;

    /**
     * Data to be sent back to the requester in object format
     */
    public BaseXYZZMessage message;

    public BaseUdpMsg(InetAddress returnAddress, int returnPort, byte[] data) {
        this.returnAddress = returnAddress;
        this.returnPort = returnPort;
        this.data = data;
    }
}
