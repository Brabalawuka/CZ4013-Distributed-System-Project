package com.company.cz4013;

import com.company.cz4013.base.client.BaseUdpClient;
import com.company.cz4013.base.client.BaseUdpMsg;
import com.company.cz4013.base.dto.BaseXYZZMessage;
import com.company.cz4013.base.event.BasePublisher;
import com.company.cz4013.controller.MethodsController;
import com.company.cz4013.exception.DeserialisationError;
import com.company.cz4013.util.LRUCache;
import com.company.cz4013.util.SerialisationTool;

import java.io.ByteArrayInputStream;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

public class MainUDPServer extends BaseUdpClient {

    private MethodsController controller;
    private final LRUCache<String, BaseUdpMsg> messageHistory = new LRUCache<>(100);



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
        } catch (DeserialisationError deserialisationError) {
            //TODO: Return error;
        }
        messageHistory.set(msg.message.getUuId(), msg);

        //TODO: HANDLE MESSAGE HISTORY

        try {
            Method method = MethodsController.class.getDeclaredMethod(MethodsController.methodHashMap.get(msg.message.getMethodName()));
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

        //TODO: LISTEN TO KEYBOARD INTERRUPT
    }

    public void sendMessage( BaseXYZZMessage<?> message){

    }


    @Override
    protected void sendMessage(BaseUdpMsg message) {

        //TODO: SEND MSG


    }



}
