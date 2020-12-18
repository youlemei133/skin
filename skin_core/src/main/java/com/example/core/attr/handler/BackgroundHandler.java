package com.example.core.attr.handler;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.skin_annotation.Attribute;


/**
 * Created By hudawei
 * on 2020/12/8 0008
 * 用于处理View设置背景样色
 */
@Attribute(attrName = "background")
public class BackgroundHandler extends BaseSkinAttrHandler {

    @Override
    public boolean isSupport(@NonNull View view, @NonNull String attributeName, int resId) {
        return attributeName.equals("background") && TextUtils.equals(attributeName, "background") &&
                TextUtils.equals("color", getResourceType(view.getContext(), resId));
    }

    @Override
    public void handle(@NonNull View view, @NonNull String attributeName, int resId) {
        view.setBackground(getDrawable(view.getContext(), resId));
    }
}
