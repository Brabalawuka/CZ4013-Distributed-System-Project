package com.company.cz4013;

import com.company.cz4013.base.client.BaseUdpClient;
import com.company.cz4013.base.client.BaseUdpMsg;
import com.company.cz4013.base.dto.BaseXYZZMessage;
import com.company.cz4013.base.dto.XYZZMessageType;
import com.company.cz4013.controller.MethodsController;
import com.company.cz4013.exception.ClientDisconnectionException;
import com.company.cz4013.util.*;
import com.company.cz4013.dto.response.ErrorMessageResponse;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.UUID;

public class MainUDPServer extends BaseUdpClient {

    public static boolean userInterrupt = false;


    private static final int TIMEOUT_IN_MILLI = 2000;
    private MethodsController controller;
    private final LRUCache<UUID, BaseUdpMsg> messageHistory = new LRUCache<>(50);



    public MainUDPServer(DatagramSocket socket) {

        super(socket);
//        try {
//            socket.setSoTimeout(TIMEOUT_IN_MILLI);
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }
        controller = new MethodsController();
    }


    @Override
    protected BaseUdpMsg receiveRequest() throws SocketTimeoutException {
        BaseUdpMsg msg = super.receiveRequest();
        //carryout checksum verify content
        byte[] checkSum = Arrays.copyOfRange(msg.data, 0, 4);
        long checkSumUnsigned =  ByteBuffer.wrap(checkSum).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL;
        byte[] data =  Arrays.copyOfRange(msg.data, 4, msg.data.length);
        if (!verifyCheckSum(checkSumUnsigned, data)){
            System.out.println("CheckSum Failed: Received CheckSum:" + checkSumUnsigned + "Decoded CheckSum: " + verifyCheckSum(checkSumUnsigned, data));
            BaseXYZZMessage<ErrorMessageResponse> errorMessage = new BaseXYZZMessage<ErrorMessageResponse>();
            errorMessage.setUuId(UUID.randomUUID());
            errorMessage.setType(XYZZMessageType.ERROR);
            errorMessage.setMethodName("NO Method Interpreted");
            errorMessage.setData(new ErrorMessageResponse("Transmission CheckSum Failed:" + checkSumUnsigned));
            msg.message = errorMessage;
            sendMessage(msg);
            return msg;
        }

        XYZZByteReader reader = new XYZZByteReader(data);
        try {
            msg.message = SerialisationTool.deserialiseToMsg(reader);
        } catch (Exception deserialisationError) {
            deserialisationError.printStackTrace();
            BaseXYZZMessage<ErrorMessageResponse> errorMessage = new BaseXYZZMessage<ErrorMessageResponse>();
            errorMessage.setUuId(UUID.randomUUID());
            errorMessage.setType(XYZZMessageType.ERROR);
            errorMessage.setMethodName("NO Method Interpreted");
            errorMessage.setData(new ErrorMessageResponse(deserialisationError.getMessage()));
            msg.message = errorMessage;
            sendMessage(msg);
        }
        //Check for cached value in case of repetitive message
        BaseUdpMsg storedMessage = messageHistory.get(msg.message.getUuId());
        if(storedMessage != null){
            System.out.println("Duplicated Message Received: " + msg.message.getUuId());
            sendMessage(storedMessage);
            return storedMessage;
        }

        try {
            Method method = MethodsController.class.getDeclaredMethod(MethodsController.methodHashMap.get(msg.message.getMethodName()),BaseUdpMsg.class, XYZZByteReader.class);
            msg = (BaseUdpMsg) method.invoke(controller, msg, reader);
            //Save returned msg for At Most Once Messages
            if (msg.message.shouldCache()){
                messageHistory.set(msg.message.getUuId(), msg);
            }

            sendMessage(msg);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            BaseXYZZMessage<ErrorMessageResponse> errorMessage = new BaseXYZZMessage<>();
            errorMessage.setUuId(msg.message.getUuId());
            errorMessage.setType(XYZZMessageType.ERROR);
            errorMessage.setMethodName(msg.message.getMethodName());
            errorMessage.setData(new ErrorMessageResponse(e.getMessage()));
            msg.message = errorMessage;
            sendMessage(msg);
        }
        return msg;
    }


    public void listen(){
        while (true){
            try {
                receiveRequest();
            } catch (SocketTimeoutException ignored) { }
        }
    }

    public BaseUdpMsg listen(int rounds) throws ClientDisconnectionException {
        for (int i = 0; i < rounds; i++) {
            try {
                return receiveRequest();
            } catch (SocketTimeoutException ignored) {
                i++;
            }
        }
        throw new ClientDisconnectionException("Not Able to Receive Request listening port for "+ rounds * TIMEOUT_IN_MILLI / 1000 + "seconds");

    }



    public synchronized void sendMessage(BaseUdpMsg message) {
        try {
            XYZZByteWriter writer = SerialisationTool.serialiseToMsg(message.message);
            byte[] payload = writer.toByteArray();
            long checkSum = createCheckSum(payload);
            byte[] data = new byte[4 + payload.length];
            byte[] checkSumBytes = new byte[8];
            ByteBuffer.wrap(checkSumBytes).order(ByteOrder.LITTLE_ENDIAN).putLong(checkSum);
            System.arraycopy(checkSumBytes, 0, data, 0, 4);
            System.arraycopy(payload,0, data,4, payload.length);
            message.data = data;
            //System.out.println(bytesToHex(data));
            super.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean verifyCheckSum(long checkSum, byte[] content){
//        System.out.println("CheckSum receivced: " + checkSum);
//        System.out.println("CheckSum verified: : " + AdlerCheckSum.checkSum(content));

        return checkSum == AdlerCheckSum.checkSum(content);
    }

    private long createCheckSum(byte[] content){
//        System.out.println("CheckSum created: " + AdlerCheckSum.checkSum(content));
        return AdlerCheckSum.checkSum(content);

    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }





}
