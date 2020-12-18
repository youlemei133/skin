package com.example.skin_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created By hudawei
 * on 2020/12/17 0017
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface SupportSkin {
}
