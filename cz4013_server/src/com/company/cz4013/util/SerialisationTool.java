package com.company.cz4013.util;

import com.company.cz4013.base.dto.BaseXYZZMessage;
import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZMessageType;
import com.company.cz4013.exception.DeserialisationError;
import com.company.cz4013.exception.SerialisationError;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A self-defined tool to marshall and unmarshall XYZZMessage Protocol
 */
public class SerialisationTool {


    /**
     * Map of data type to integer representation
     */
    private static HashMap<Class, Integer> typeToIntMap = new HashMap<Class, Integer>(){{
        put(Integer.class, 0);
        put(Float.class, 1);
        put(String.class, 2);
        put(Boolean.class, 3);
        put(List.class, 4);
    }};

    /**
     * Map of integer representation to data type
     */
    private static HashMap<Integer, Class> intToTypeMap = new HashMap<Integer, Class>(){{
        put(0, Integer.class);
        put(1, Float.class);
        put(2,  String.class);
        put(3, Boolean.class);
        put(4, List.class);
    }};


    /**
     * Deserialise bytes got from the XYZZByteReader object to a BaseXYZZMessage
     * @param reader A XYZZByteReader which supplies the bytes to be deserialised
     * @return A BaseXYZZMessage which contains the unmarshalled data
     * @throws DeserialisationError
     */
    public static BaseXYZZMessage deserialiseToMsg(XYZZByteReader reader) throws DeserialisationError {

        BaseXYZZMessage<?> msg = new BaseXYZZMessage<>();


        //Interpret Msg Type
        msg.setType(XYZZMessageType.fromInteger(reader.read()));

        //Interpret UUID
        byte[] uuid = new byte[36];
        int uuidResult = reader.read(uuid, 0, 36);
        if (uuidResult == -1){
            throw new DeserialisationError("UUID Error");
        } else {
            msg.setUuId(new String(uuid, StandardCharsets.US_ASCII));
        }

        //Interpret MethodName
        byte[] methodName = new byte[reader.read()];
        int methodNameResult = reader.readNBytes(methodName, 0, methodName.length);
        if (methodNameResult == -1){
            throw new DeserialisationError("ClassName Error");
        }
        String methodNameString = new String(methodName, StandardCharsets.US_ASCII);
        msg.setMethodName(methodNameString);
        return msg;
    }

    /**
     * Serialize a BaseXYZZMessage to bytes and store in a XYZZByteWriter
     * @param message  A BaseXYZZMessage to be serialised
     * @return A XYZZByteWriter which contains data in bytes
     * @throws Exception
     */
    public static XYZZByteWriter serialiseToMsg(BaseXYZZMessage message) throws Exception {

        XYZZByteWriter writer = new XYZZByteWriter();

        //Write Msg Type
        writer.write(XYZZMessageType.toInteger(message.getType()));

        //Write UUID TODO: USE BASE64 to compress UUID in to 24byte
        writer.write(message.getUuId().toString().getBytes(), 0, 36);

        //WriteData
        if(message.getData() == null){
            return writer;
            //throw new SerialisationError("No XYZZObject detected: " + message.getUuId() + message.getMethodName());
        }

        serialiseObjectToWriter(writer, message.getData());
        return writer;
    }


    /**
     * Deserialize bytes got from the XYZZByteReader object to a BaseXYZZObject
     * @param reader A XYZZByteReader which supplies the bytes to be deserialised
     * @param decodeObject An empty BaseXYZZObject whose attributed is to be set during Deserialization
     * @param <T> A solid type of BaseXYZZObject (e.g. xxQuery)
     * @return The BaseXYZZObject with data filled
     * @throws Exception Thrown when unexpected/unmatched types encountered
     */
    public static <T extends BaseXYZZObject> T deserialiseToObject(XYZZByteReader reader, T decodeObject) throws Exception {

        List<Field> fields = BaseXYZZObject.getOrderedField(decodeObject.getClass().getDeclaredFields());

        //Interpret Every
        for (Field field : fields) {
            field.setAccessible(true);
            int type = reader.read();
            try {
                Object runtimeObject = decodeNext(type, reader);
                field.set(decodeObject, runtimeObject);
            } catch (Exception e) {
                e.printStackTrace();
                throw new DeserialisationError("Message Decoding error: " + e.getMessage());
            }
        }
        
        return decodeObject;
    }

    /**
     * Serialize a BaseXYZZObject into bytes and store into the given XYZZByteWriter
     * @param writer A XYZZByteWriter instance which will be used to store bytes
     * @param encodeObject A BaseXYZZObject object whose fields will be deserialized
     * @param <T> A solid type of BaseXYZZObject (e.g. xxResponse)
     * @throws SerialisationError Thrown when unexpected/unmatched types encountered
     */
    public static <T extends BaseXYZZObject> void serialiseObjectToWriter(XYZZByteWriter writer, T encodeObject) throws SerialisationError {
        // If use getFields, length 0. But by using getDeclaredFields, some originally private attribute might be sent as well
        List<Field> fields = BaseXYZZObject.getOrderedField(encodeObject.getClass().getDeclaredFields());

        //Interpret Every
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                int fieldTypeInt = typeToIntMap.getOrDefault(field.getType(), -1);
                writer.write(fieldTypeInt);
                encodeNext(writer, fieldTypeInt, field, encodeObject);
            } catch (Exception e) {
                e.printStackTrace();
                throw new SerialisationError("Message Encoding error: " + e.getMessage());
            }
        }
    }

    /**
     * Deserialize an field of data from bytes
     * @param type type of the data
     * @param reader A XYZZByteReader which supplies the bytes to be decoded
     * @return Deserialize data of type int, float, String, bool or List
     * @throws Exception Thrown when unexpected types encountered
     */
    private static Object decodeNext(int type, XYZZByteReader reader) throws Exception {

        switch (type){
            case 0:
                byte[] intByte =reader.readNBytes(4);
                return ByteBuffer.wrap(intByte).order(ByteOrder.LITTLE_ENDIAN).getInt();
            case 1:
                byte[] floatByte = reader.readNBytes(4);
                return ByteBuffer.wrap(floatByte).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            case 2:
                byte[] stringlength = reader.readNBytes(4);
                int length = ByteBuffer.wrap(stringlength).order(ByteOrder.LITTLE_ENDIAN).getInt();
                return new String(reader.readNBytes(length), StandardCharsets.US_ASCII);
            case 3:
                return reader.read() == 1;
            case 4:
                int arrayType = reader.read();
                if(arrayType > 3)
                    throw new DeserialisationError("Wrong MessageType: Array Component type error: " + arrayType);

                byte[] arrayLengthBytes = reader.readNBytes(4);
                int arrayLength = ByteBuffer.wrap(arrayLengthBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
                List<Object> objectList = new ArrayList<>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    objectList.add(i, decodeNext(arrayType, reader));
                }
                return objectList;

        }
        throw new DeserialisationError("Wrong MessageType: " + type);
    }

    /**
     * Serialize an field of data to bytes
     * @param writer A XYZZByteWriter which the serialized bytes are to be stored
     * @param fieldTypeInt type of the data in integer representation
     * @param field A field to get the actual unserialized data
     * @param object A BaseXYZZObject whose attribute is to be got
     * @return The XYZZByteWriter will filled bytes
     * @throws Exception Thrown when unexpected types encountered
     */
    private static XYZZByteWriter encodeNext(XYZZByteWriter writer, Integer fieldTypeInt,  Field field, BaseXYZZObject object) throws Exception {


        switch (fieldTypeInt){
            case -1:
                throw new SerialisationError("Wrong MessageType: " + fieldTypeInt);
            case 0:
                int intVal = field.getInt(object);
                writer.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(intVal).array());

                return writer;
            case 1:
                float floatVal = field.getFloat(object);
                writer.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(floatVal).array());
                return writer;
            case 2:
                String strVal = (String) field.get(object);
                byte[] strBytes = strVal.getBytes();
                writer.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(strBytes.length).array());
                writer.write(strBytes);
                return writer;
            case 3:
                Boolean boolVal = (Boolean) field.get(object);
//                boolean boolVal = field.getBoolean(object);
                if (boolVal) {
                    writer.write(1);
                } else {
                    writer.write(0);
                }
                return writer;
            case 4:
                //Get list type
                ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                Class c = (Class) stringListType.getActualTypeArguments()[0];
                //Get list length
                int length = ((Collection)field.get(object)).size();
                //get list type -> int
                int listType = typeToIntMap.getOrDefault(c, -1);
                //write inner type
                writer.write(listType);
                //write list length
                writer.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(length).array());
                for (Object o : ((Collection) field.get(object))) {
                    switch (listType){
                        //Wrong List Type
                        case -1:
                            throw new SerialisationError("Wrong MessageType: Array Component type error: " + listType);
                        case 0:
                            writer.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt((Integer) o).array());
                            break;
                        case 1:
                            writer.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat((Float) o).array());
                            break;
                        case 2:
                            byte[] objectBytes =((String)o).getBytes();
                            writer.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(objectBytes.length).array());
                            writer.write(objectBytes);
                            break;
                        case 3:
                            if ((Boolean)o) {
                                writer.write(1);
                            } else {
                                writer.write(0);
                            }
                            break;
                    }
                }
                return writer;

        }
        throw new SerialisationError("Wrong MessageType: " + field.getName() + field.getClass());
    }

}
