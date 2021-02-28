package com.company.cz4013.base.client;

import com.company.cz4013.exception.UdpClientException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public abstract class BaseUdpClient {

    private DatagramSocket socket;


    public BaseUdpClient(DatagramSocket socket){
        this.socket = socket;
    }


    public BaseUdpMsg receiveRequest(){
        byte[] dataBuffer = new byte[2048];
        DatagramPacket request = new DatagramPacket(dataBuffer, dataBuffer.length);
        try {
            socket.receive(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new BaseUdpMsg(request.getAddress(), request.getPort(), request.getData());
    }

    public void sendMessage(BaseUdpMsg message){

        DatagramPacket replyPacket = new DatagramPacket(message.data, message.data.length, message.returnAddress, message.returnPort);

        try {
            socket.send(replyPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }






}
