package com.example.core.attr.handler;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.skin_annotation.Attribute;

/**
 * Created By hudawei
 * on 2020/12/10 0010
 * 用于处理TextView的setTextColor属性
 */
@Attribute(attrName = "textColor")
public class TextColorHandler extends BaseSkinAttrHandler {
    @Override
    public boolean isSupport(@NonNull View view, @NonNull String attributeName, int resId) {
        return attributeName.equals("textColor") && view instanceof TextView &&
                TextUtils.equals(getResourceType(view.getContext(), resId), "color");
    }

    @Override
    public void handle(@NonNull View view, @NonNull String attributeName, int resId) {
        super.handle(view, attributeName, resId);
        TextView textView = (TextView) view;
        textView.setTextColor(getColor(view.getContext(), resId));
    }
}
