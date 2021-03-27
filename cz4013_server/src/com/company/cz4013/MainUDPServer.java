package com.company.cz4013;

import com.company.cz4013.base.client.BaseUdpClient;
import com.company.cz4013.base.client.BaseUdpMsg;
import com.company.cz4013.base.dto.BaseXYZZMessage;
import com.company.cz4013.base.dto.XYZZMessageType;
import com.company.cz4013.controller.MethodsController;
import com.company.cz4013.util.AdlerCheckSum;
import com.company.cz4013.dto.response.ErrorMessageResponse;
import com.company.cz4013.util.LRUCache;
import com.company.cz4013.util.SerialisationTool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.UUID;

public class MainUDPServer extends BaseUdpClient {

    public static boolean userInterrupt = false;


    private MethodsController controller;
    private final LRUCache<UUID, BaseUdpMsg> messageHistory = new LRUCache<>(50);



    public MainUDPServer(DatagramSocket socket) {

        super(socket);
        controller = new MethodsController();
    }


    @Override
    protected BaseUdpMsg receiveRequest() {
        BaseUdpMsg msg = super.receiveRequest();
        //carryout checksum verify content
        byte[] checkSum = Arrays.copyOfRange(msg.data, 0, 3);
        byte[] data =  Arrays.copyOfRange(msg.data, 4, msg.data.length-1);
        int checkSumInt = ByteBuffer.wrap(checkSum).order(ByteOrder.LITTLE_ENDIAN).getInt();
        if (!verifyCheckSum(checkSumInt, data)){
            BaseXYZZMessage<ErrorMessageResponse> errorMessage = new BaseXYZZMessage<ErrorMessageResponse>();
            errorMessage.setUuId(UUID.randomUUID());
            errorMessage.setType(XYZZMessageType.ERROR);
            errorMessage.setMethodName("NO Method Interpreted");
            errorMessage.setData(new ErrorMessageResponse("Transmission CheckSum Failed:" + checkSumInt));
            msg.message = errorMessage;
            sendMessage(msg);
            return msg;
        }

        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        try {
            msg.message = SerialisationTool.deserialiseToMsg(stream);
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
            sendMessage(storedMessage);
            return storedMessage;
        }

        try {
            Method method = MethodsController.class.getDeclaredMethod(MethodsController.methodHashMap.get(msg.message.getMethodName()),BaseUdpMsg.class, ByteArrayInputStream.class);
            msg = (BaseUdpMsg) method.invoke(controller, msg, stream);
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
            receiveRequest();
        }
    }



    public void sendMessage(BaseUdpMsg message) {
        try {
            ByteArrayOutputStream stream = SerialisationTool.serialiseToMsg(message.message);
            byte[] payload = stream.toByteArray();
            int checkSum = createCheckSum(payload);
            byte[] data = new byte[4 + payload.length];
            System.arraycopy(ByteBuffer.allocate(4).putInt(checkSum).array(), 0, data, 0, 4);
            System.arraycopy(payload,0, data,4, payload.length);
            message.data = data;
            super.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean verifyCheckSum(int checkSum, byte[] content){
        return checkSum == AdlerCheckSum.checkSum(content);
    }

    private int createCheckSum(byte[] content){
       return AdlerCheckSum.checkSum(content);
    }





}