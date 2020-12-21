package com.example.core.attr.handler;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created By hudawei
 * on 2020/12/11 0011
 * 所有处理属性的类都存放在一个Map中
 * 处理一个属性可以有多个处理类来实现
 */
public class HandlerMap {
    /**
     * 用于存放处理属性类
     */
    private static final Map<String, List<SkinAttrHandler>> sHandlerMap = new HashMap<>();

    /**
     * 注册处理属性类
     *
     * @param attributeName 处理的属性名
     * @param handler       处理属性类的对象
     */
    public static void register(String attributeName, SkinAttrHandler handler) {
        List<SkinAttrHandler> skinAttrHandlers = get(attributeName);
        if (skinAttrHandlers == null) {
            skinAttrHandlers = new ArrayList<>();
            sHandlerMap.put(attributeName, skinAttrHandlers);
        }
        skinAttrHandlers.add(handler);
    }

    /**
     * 移除指定属性
     *
     * @param attributeName 属性名
     */
    public static void unRegister(String attributeName) {
        sHandlerMap.remove(attributeName);
    }

    /**
     * 移除指定处理属性类
     *
     * @param handlerClass 需移除的处理属性类型
     */
    public static void unRegister(Class<? extends SkinAttrHandler> handlerClass) {
        Collection<List<SkinAttrHandler>> values = sHandlerMap.values();
        for (List<SkinAttrHandler> handlers : values) {
            for (SkinAttrHandler h : handlers) {
                if (h.getClass() == handlerClass) {
                    handlers.remove(h);
                    break;
                }
            }
        }
    }

    /**
     * 获取指定属性的所有处理类对象
     *
     * @param attributeName 属性名
     * @return 处理该属性的类对象集合
     */
    public static List<SkinAttrHandler> get(String attributeName) {
        return sHandlerMap.get(attributeName);
    }

    /**
     * 获取处理属性Map
     */
    public static Map<String, List<SkinAttrHandler>> getHandlerMap() {
        return sHandlerMap;
    }

}
