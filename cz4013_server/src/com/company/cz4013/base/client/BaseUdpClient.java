package com.company.cz4013.base.client;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Random;

/**
 * Base UDP Client
 */
public abstract class BaseUdpClient {

    /**
     * Socket to be used to transmitting and listening data
     */
    protected DatagramSocket socket;


    public BaseUdpClient(DatagramSocket socket){
        this.socket = socket;
    }


    /**
     * receive a request via socket and parse it to a Base UDP message
     * @return parsed UDP message
     * @throws SocketTimeoutException no message was received and the timeout limit is hit
     */
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

    /**
     * send out a UDP message
     * @param message UDP message object to be sent out
     */
    protected void sendMessage(BaseUdpMsg message){

        System.out.print("Sending Package: UUID = " + message.message.getUuId()
                        + ", Method: " + message.message.getMethodName()
                        + ", Type: " + message.message.getType()
                        + ", IP: " + message.returnAddress
                        + ", PORT: " + message.returnPort
                        + " ------ "
        );
        Random random = new Random();
        if (message.returnPort == 8081) {
            if (random.nextInt(10) > 0){
                System.out.println("Targeted Message Lost Simulated");
                return;
            }
        } else {
            //Simulate Transmission Packet Loss
            if (random.nextInt(10) > 8) {
                System.out.println("Message Lost Simulated");
                return;
            }
        }
        //Simulate Transmission Byte Error
        if (random.nextInt(20) > 18){
            System.out.print("Transmission Error Simulated ----- ");
            message.data[7] = ((Integer)(Byte.toUnsignedInt(message.data[7]) + 1)).byteValue();
        }
        System.out.println("Success");
        DatagramPacket replyPacket = new DatagramPacket(message.data, message.data.length, message.returnAddress, message.returnPort);
        try {
            socket.send(replyPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
