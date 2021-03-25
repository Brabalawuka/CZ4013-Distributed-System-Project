package com.company.cz4013.util;

import com.company.cz4013.base.dto.BaseXYZZMessage;
import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZMessageType;
import com.company.cz4013.exception.DeserialisationError;
import com.company.cz4013.exception.SerialisationError;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SerialisationTool {


    private static HashMap<Class, Integer> typeToIntMap = new HashMap<Class, Integer>(){{
        put(Integer.class, 0);
        put(Float.class, 1);
        put(String.class, 2);
        put(Boolean.class, 3);
        put(List.class, 4);
    }};

    private static HashMap<Integer, Class> intToTypeMap = new HashMap<Integer, Class>(){{
        put(0, Integer.class);
        put(1, Float.class);
        put(2,  String.class);
        put(3, Boolean.class);
        put(4, List.class);
    }};


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

    public static ByteArrayOutputStream serialiseToMsg(BaseXYZZMessage message) throws Exception {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        //Write Msg Type
        stream.write(XYZZMessageType.toInteger(message.getType()));

        //Write UUID TODO: USE BASE64 to compress UUID in to 24byte
        stream.write(message.getUuId().toString().getBytes(), 0, 36);

        //WriteData
        if(message.getData() == null){
            throw new SerialisationError("No XYZZObject detected: " + message.getUuId() + message.getMethodName());
        }

        serialiseObjectTostream(stream, message.getData());
        return stream;
    }


    public static <T extends BaseXYZZObject> T deserialiseToObject(ByteArrayInputStream stream, T decodeObject) throws Exception {

        List<Field> fields = BaseXYZZObject.getOrderedField(decodeObject.getClass().getDeclaredFields());

        //Interpret Every
        for (Field field : fields) {
            field.setAccessible(true);
            int type = stream.read();
            try {
                Object runtimeObject = decodeNext(type, stream);
                field.set(decodeObject, runtimeObject);
            } catch (Exception e) {
                e.printStackTrace();
                throw new DeserialisationError("Message Decoding error: " + e.getMessage());
            }
        }
        
        return decodeObject;
    }



    public static <T extends BaseXYZZObject> void serialiseObjectTostream(ByteArrayOutputStream stream, T encodeObject) throws SerialisationError {
        // If use getFields, length 0. But by using getDeclaredFields, some originally private attribute might be sent as well
        List<Field> fields = BaseXYZZObject.getOrderedField(encodeObject.getClass().getDeclaredFields());

        //Interpret Every
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                int fieldTypeInt = typeToIntMap.getOrDefault(field.getType(), -1);
                stream.write(fieldTypeInt);
                encodeNext(stream, fieldTypeInt, field, encodeObject);
            } catch (Exception e) {
                e.printStackTrace();
                throw new SerialisationError("Message Encoding error: " + e.getMessage());
            }
        }
    }





    private static Object decodeNext(int type, ByteArrayInputStream stream) throws Exception {

        switch (type){
            case 0:
                byte[] intByte =stream.readNBytes(4);
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
                    throw new DeserialisationError("Wrong MessageType: Array Component type error: " + arrayType);

                byte[] arrayLengthBytes = stream.readNBytes(4);
                int arrayLength = ByteBuffer.wrap(arrayLengthBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
                List<Object> objectList = new ArrayList<>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    objectList.add(i, decodeNext(arrayType, stream));
                }
                return objectList;

        }
        throw new DeserialisationError("Wrong MessageType: " + type);
    }

    private static ByteArrayOutputStream encodeNext(ByteArrayOutputStream stream, Integer fieldTypeInt,  Field field, BaseXYZZObject object) throws Exception {


        switch (fieldTypeInt){
            case -1:
                throw new SerialisationError("Wrong MessageType: " + fieldTypeInt);
            case 0:
                int intVal = field.getInt(object);
                stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(intVal).array());
//                stream.write(intVal);
                return stream;
            case 1:
                float floatVal = field.getFloat(object);
                stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(floatVal).array());
                return stream;
            case 2:
                String strVal = (String) field.get(object);
                byte[] strBytes = strVal.getBytes();
                stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(strBytes.length).array());
                stream.write(strBytes);
                return stream;
            case 3:
                Boolean boolVal = (Boolean) field.get(object);
//                boolean boolVal = field.getBoolean(object);
                if (boolVal) {
                    stream.write(1);
                } else {
                    stream.write(0);
                }
                return stream;
            case 4:
                //Get list type
                ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                Class c = (Class) stringListType.getActualTypeArguments()[0];
                //Get list length
                int length = ((Collection)field.get(object)).size();
                //get list type -> int
                int listType = typeToIntMap.getOrDefault(c, -1);
                //write inner type
                stream.write(listType);
                //write list length
                stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(length).array());
                for (Object o : ((Collection) field.get(object))) {
                    switch (listType){
                        //Wrong List Type
                        case -1:
                            throw new SerialisationError("Wrong MessageType: Array Component type error: " + listType);
                        case 0:
                            stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt((Integer) o).array());
                            break;
                        case 1:
                            stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat((Float) o).array());
                            break;
                        case 2:
                            byte[] objectBytes =((String)o).getBytes();
                            stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(objectBytes.length).array());
                            stream.write(objectBytes);
                            break;
                        case 3:
                            if ((Boolean)o) {
                                stream.write(1);
                            } else {
                                stream.write(0);
                            }
                            break;
                    }
                }
                return stream;

        }
        throw new SerialisationError("Wrong MessageType: " + field.getName() + field.getClass());
    }

}
