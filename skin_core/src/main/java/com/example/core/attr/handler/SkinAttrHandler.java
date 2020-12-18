package com.example.core.attr.handler;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * Created By hudawei
 * on 2020/12/9 0009
 * 处理View的属性接口
 */
public interface SkinAttrHandler {
    /**
     * 是否能处理指定View的属性
     *
     * @param view          需处理的View
     * @param attributeName 需处理的属性名
     * @param resId         需处理的资源ID
     * @return true代表能处理
     */
    boolean isSupport(@NonNull View view, @NonNull String attributeName, int resId);

    /**
     * 处理该属性
     *
     * @param view          需处理的View
     * @param attributeName 需处理的资源ID
     * @param resId         需处理的属性值
     */
    void handle(@NonNull View view, @NonNull String attributeName, int resId);
}
