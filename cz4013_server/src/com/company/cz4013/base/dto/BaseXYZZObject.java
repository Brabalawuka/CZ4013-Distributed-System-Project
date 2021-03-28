package com.company.cz4013.base.dto;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * Base XYZZ object which allows the serializer to get fields in sequence for marshalling and unmarshalling purpose
 */
public class BaseXYZZObject {

    public static List<Field> getOrderedField(Field[] fields){

        List<Field> fieldList = new ArrayList<>();

        for(Field f: fields){
            if(f.getAnnotation(XYZZFieldAnnotation.class)!=null){
                fieldList.add(f);
            }
        }

        fieldList.sort(Comparator.comparingInt(
                m -> m.getAnnotation(XYZZFieldAnnotation.class).order()
        ));
        return fieldList;
    }


    public static List<Method> getOrderedMethod(Method[] methods){

        List<Method> methodList = new ArrayList<>();

        for(Method m: methods){
            if(m.getAnnotation(XYZZMethodAnnotation.class)!=null){
                methodList.add(m);
            }
        }

        methodList.sort(Comparator.comparingInt(
                m -> m.getAnnotation(XYZZMethodAnnotation.class).order()
        ));
        return methodList;
    }


}
