package com.company.cz4013;

import com.company.cz4013.base.client.BaseUdpClient;
import com.company.cz4013.base.client.BaseUdpMsg;
import com.company.cz4013.base.dto.BaseXYZZMessage;
import com.company.cz4013.base.dto.XYZZMessageType;
import com.company.cz4013.controller.MethodsController;
import com.company.cz4013.dto.ErrorMessageResponse;
import com.company.cz4013.exception.DeserialisationError;
import com.company.cz4013.util.LRUCache;
import com.company.cz4013.util.SerialisationTool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
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
        ByteArrayInputStream stream = new ByteArrayInputStream(msg.data);
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
            sendMessage(msg, 0);
        }
        //Check for cached value in case of repetitive message
        BaseUdpMsg storedMessage = messageHistory.get(msg.message.getUuId());
        if(storedMessage != null){
            sendMessage(storedMessage);
            return storedMessage;
        }
        messageHistory.set(msg.message.getUuId(), msg);

        //TODO: HANDLE MESSAGE HISTORY

        try {
            Method method = MethodsController.class.getDeclaredMethod(MethodsController.methodHashMap.get(msg.message.getMethodName()),BaseXYZZMessage.class, ByteArrayInputStream.class);
            method.invoke(controller, msg.message, stream);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            //TODO : NO METHOD ERROR + METHOD INVOCATION ERROR
        }
        return msg;
    }


    public void listen(){
        while (true){
            receiveRequest();
        }
    }



    protected void sendMessage(BaseUdpMsg message, int retryTime) {

        try {
            ByteArrayOutputStream stream = SerialisationTool.serialiseToMsg(message.message);
            message.data = stream.toByteArray();
            super.sendMessage(message);
        } catch (Exception e) {
            if (retryTime >= 1){
                e.printStackTrace();
                return;
            }
            BaseXYZZMessage<ErrorMessageResponse> errorMessage = new BaseXYZZMessage<ErrorMessageResponse>();
            errorMessage.setUuId(message.message.getUuId());
            errorMessage.setType(XYZZMessageType.ERROR);
            errorMessage.setMethodName(message.message.getMethodName());
            errorMessage.setData(new ErrorMessageResponse(e.getMessage()));
            message.message = errorMessage;
            sendMessage(message, ++retryTime);
        }
    }



}
