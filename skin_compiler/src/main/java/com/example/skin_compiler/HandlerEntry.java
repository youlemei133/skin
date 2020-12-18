package com.example.skin_compiler;

/**
 * Created By hudawei
 * on 2020/12/10 0010
 * 用于存储Attribute注解信息的实体类
 */
public class HandlerEntry {
    public String attrName;
    public String methodName;
    public String handlerClass;

    public HandlerEntry(String attrName, String methodName, String handlerClass) {
        this.attrName = attrName;
        this.methodName = methodName;
        this.handlerClass = handlerClass;
    }
}
