package com.ashera.aop;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BaseAscpect {
	public Object getFieldValueUsingReflection(Object targetObject, String fieldName) {
        Field field;
        try {
            field = targetObject.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            field = null;
        }
        Class superClass = targetObject.getClass().getSuperclass();
        while (field == null && superClass != null) {
            try {
                field = superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                superClass = superClass.getSuperclass();
            }
        }
        if (field == null) {
            return null;
        }
        field.setAccessible(true);
        try {
            return field.get(targetObject);
        } catch (IllegalAccessException e) {
            return null;
        }
    }
	
	public boolean setFieldUsingReflection(Object targetObject, String fieldName, Object fieldValue) {
        Field field;
        try {
            field = targetObject.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            field = null;
        }
        Class superClass = targetObject.getClass().getSuperclass();
        while (field == null && superClass != null) {
            try {
                field = superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                superClass = superClass.getSuperclass();
            }
        }
        if (field == null) {
            return false;
        }
        field.setAccessible(true);
        try {
            field.set(targetObject, fieldValue);
            return true;
        } catch (IllegalAccessException e) {
            return false;
        }
    }

	public Object invokePrivateMethodUsingReflection(Object obj, String methodName,
            Object... params) {
        int paramCount = params.length;
        Method method = null;
        Object requiredObj = null;
        Class<?>[] classArray = new Class<?>[paramCount];
        for (int i = 0; i < paramCount; i++) {
            classArray[i] = params[i].getClass();
        }
        Class superClass = obj.getClass();
        try {
        	while(method == null && superClass != null) {
                try {
                	method = superClass.getDeclaredMethod(methodName, classArray);
                } catch (NoSuchMethodException var8) {
                    superClass = superClass.getSuperclass();
                }
            }
        	
            method.setAccessible(true);
            requiredObj = method.invoke(obj, params);
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }

        return requiredObj;
    }
}
