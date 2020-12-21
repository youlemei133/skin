package com.example.core.utils;

import androidx.annotation.NonNull;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created By hudawei
 * on 2020/11/27 0027
 */
public class RefUtils {
    public static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (Exception e) {
            try {
                field = clazz.getField(fieldName);
            } catch (NoSuchFieldException noSuchFieldException) {
                Class<?> superClass = clazz.getSuperclass();
                if (superClass != null) {
                    field = getField(superClass, fieldName);
                }
            }
        }
        if (field != null && !field.isAccessible()) {
            field.setAccessible(true);
        }
        return field;
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... args) {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, args);
        } catch (NoSuchMethodException e) {
            try {
                method = clazz.getMethod(methodName, args);
            } catch (NoSuchMethodException noSuchMethodException) {
                Class<?> superClass = clazz.getSuperclass();
                if (superClass != null) {
                    method = getMethod(superClass, methodName, args);
                }
            }
        }
        if (method != null && !method.isAccessible()) {
            method.setAccessible(true);
        }
        return method;
    }

    public static Object getFieldValue(@NonNull Field field, @NonNull Object object) {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getStaticFieldValue(@NonNull Field field) {
        try {
            return field.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setFieldValue(@NonNull Object object, @NonNull Field field, Object value) {
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Object invokeMethod(@NonNull Method method, Object object, Object... args) {
        try {
            return method.invoke(object, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getArrayType(Field arrayField) {
        return arrayField.getType().getComponentType();
    }

    public static Class<?> getArrayType(Object array) {
        return array.getClass().getComponentType();
    }

    public static Object createArray(Class<?> componentType, int length) {
        return Array.newInstance(componentType, length);
    }

}
