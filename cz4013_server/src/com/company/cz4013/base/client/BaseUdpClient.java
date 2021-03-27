package com.company.cz4013.base.client;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public abstract class BaseUdpClient {

    private DatagramSocket socket;


    public BaseUdpClient(DatagramSocket socket){
        this.socket = socket;
    }


    protected BaseUdpMsg receiveRequest(){
        byte[] dataBuffer = new byte[4096];
        DatagramPacket request = new DatagramPacket(dataBuffer, dataBuffer.length);
        try {
            socket.receive(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new BaseUdpMsg(request.getAddress(), request.getPort(), request.getData());
    }

    protected void sendMessage(BaseUdpMsg message){

        DatagramPacket replyPacket = new DatagramPacket(message.data, message.data.length, message.returnAddress, message.returnPort);
        Random random = new Random();

        try {
            //Simulate Transmission Packet Loss
            if (random.nextInt(10) > 8)
                return;
            socket.send(replyPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}
