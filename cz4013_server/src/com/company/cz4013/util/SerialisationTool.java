package com.company.cz4013.util;

import com.company.cz4013.base.dto.BaseXYZZMessage;
import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZMessageType;
import com.company.cz4013.exception.DeserialisationError;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SerialisationTool {

    public static BaseXYZZMessage deserialiseToMsg(ByteArrayInputStream stream) throws DeserialisationError {

        BaseXYZZMessage<?> msg = new BaseXYZZMessage<>();

        //Interpret Msg Type
        msg.setType(XYZZMessageType.fromInteger(stream.read()));

        //Interpret UUID
        byte[] uuid = new byte[36];
        int uuidResult = stream.read(uuid, 0, 36);
        if (uuidResult == -1){
            throw new DeserialisationError("UUID Error");
        } else {
            msg.setUuId(new String(uuid, StandardCharsets.US_ASCII));
        }

        //Interpret MethodName
        byte[] methodName = new byte[stream.read()];
        int methodNameResult = stream.readNBytes(methodName, 0, methodName.length);
        if (methodNameResult == -1){
            throw new DeserialisationError("ClassName Error");
        }
        String methodNameString = new String(methodName, StandardCharsets.US_ASCII);
        msg.setMethodName(methodNameString);
        return msg;
    }


    public static <T extends BaseXYZZObject> T deserialiseToObject(ByteArrayInputStream stream, T decodeObject) throws DeserialisationError {

        List<Field> fields = BaseXYZZObject.getOrderedField(decodeObject.getClass().getFields());

        //Interpret Every
        for (Field field : fields) {
            field.setAccessible(true);
            int type = stream.read();
            try {
                Object runtimeObject = decodeNext(type, stream);
                field.set(decodeObject, runtimeObject);
            } catch (Exception e) {
                throw new DeserialisationError("Message Decoding error: " + e.getMessage());
            }
        }
        
        return decodeObject;
    }

    public static byte[] serialiseToByte(ByteArrayInputStream stream) throws DeserialisationError {

        //TODO: Deserialisation
        return null;
    }




    private static Object decodeNext(int type, ByteArrayInputStream stream) throws Exception {

        switch (type){
            case 0:
                byte[] intByte =stream.readNBytes(4);
                stream.readNBytes(intByte,0,4);
                return ByteBuffer.wrap(intByte).order(ByteOrder.LITTLE_ENDIAN).getInt();
            case 1:
                byte[] floatByte = stream.readNBytes(4);
                return ByteBuffer.wrap(floatByte).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            case 2:
                byte[] stringlength = stream.readNBytes(4);
                int length = ByteBuffer.wrap(stringlength).order(ByteOrder.LITTLE_ENDIAN).getInt();
                return new String(stream.readNBytes(length), StandardCharsets.US_ASCII);
            case 3:
                return stream.read() == 1;
            case 4:
                int arrayType = stream.read();

                if(arrayType > 3)
                    throw new DeserialisationError("Wrong MessageType");

                byte[] arrayLengthBytes = stream.readNBytes(4);
                int arrayLength = ByteBuffer.wrap(arrayLengthBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
                Object[] objects = new Object[arrayLength];
                for (int i = 0; i < objects.length; i++) {
                    objects[i] = decodeNext(arrayType, stream);
                }
                return objects;

        }
        throw new DeserialisationError("Wrong MessageType: " + type);


    }




}
