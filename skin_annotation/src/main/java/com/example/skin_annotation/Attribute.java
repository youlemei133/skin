package com.example.skin_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created By hudawei
 * on 2020/12/9 0009
 * 此注解用于标注实现SkinAttrHandler的自定义处理属性类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Attribute {
    String attrName();

    String methodName() default "";
}
