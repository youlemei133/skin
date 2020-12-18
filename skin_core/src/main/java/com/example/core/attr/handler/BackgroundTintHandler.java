package com.example.core.attr.handler;

import android.content.res.ColorStateList;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.skin_annotation.Attribute;

/**
 * Created By hudawei
 * on 2020/12/17 0017
 */
@Attribute(attrName = "backgroundTint")
public class BackgroundTintHandler extends BaseSkinAttrHandler {
    @Override
    public boolean isSupport(@NonNull View view, @NonNull String attributeName, int resId) {
        return attributeName.equals("backgroundTint");
    }

    @Override
    public void handle(@NonNull View view, @NonNull String attributeName, int resId) {
        ColorStateList colorStateList = getColorStateList(view.getContext(), resId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setBackgroundTintList(colorStateList);
        }
    }
}
