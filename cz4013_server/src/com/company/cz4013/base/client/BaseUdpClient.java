package com.company.cz4013.base.client;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Random;

public abstract class BaseUdpClient {

    protected DatagramSocket socket;


    public BaseUdpClient(DatagramSocket socket){
        this.socket = socket;
    }


    protected BaseUdpMsg receiveRequest() throws SocketTimeoutException{
        byte[] dataBuffer = new byte[4096];
        DatagramPacket request = new DatagramPacket(dataBuffer, dataBuffer.length);
        try {
            socket.receive(request);
        } catch (SocketTimeoutException exception){
            throw exception;
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] payload = Arrays.copyOfRange(request.getData(), 0, request.getLength());
        return new BaseUdpMsg(request.getAddress(), request.getPort(), payload);
    }

    protected void sendMessage(BaseUdpMsg message){

        System.out.println("Sending Package: UUID = " + message.message.getUuId());
        Random random = new Random();
        //Simulate Transmission Packet Loss
        if (random.nextInt(10) > 8){
            System.out.println("Message Lost Simulated");
            return;
        }
        //Simulate Transmission Byte Error
        if (random.nextInt(20) > 18){
            System.out.println("Transmission Error Simulated");
            message.data[7] = ((Integer)(Byte.toUnsignedInt(message.data[7]) + 1)).byteValue();
        }
        DatagramPacket replyPacket = new DatagramPacket(message.data, message.data.length, message.returnAddress, message.returnPort);
        try {
            socket.send(replyPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
