package com.example.core.attr.handler;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.skin_annotation.Attribute;

/**
 * Created By hudawei
 * on 2020/12/17 0017
 */
@Attribute(attrName = "src")
public class SrcHandler extends BaseSkinAttrHandler {
    @Override
    public boolean isSupport(@NonNull View view, @NonNull String attributeName, int resId) {
        return attributeName.equals("src") && view instanceof ImageView;
    }

    @Override
    public void handle(@NonNull View view, @NonNull String attributeName, int resId) {
        ImageView imageView = (ImageView) view;
        imageView.setImageDrawable(getDrawable(view.getContext(), resId));
    }
}
