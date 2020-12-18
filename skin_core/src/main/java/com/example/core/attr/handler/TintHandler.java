package com.example.core.attr.handler;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.skin_annotation.Attribute;


/**
 * Created By hudawei
 * on 2020/12/17 0017
 * 设置ImageView的tint属性
 */
@Attribute(attrName = "tint")
public class TintHandler extends BaseSkinAttrHandler {
    @Override
    public boolean isSupport(@NonNull View view, @NonNull String attributeName, int resId) {
        return attributeName.equals("tint") && view instanceof ImageView &&
                TextUtils.equals(getResourceType(view.getContext(), resId), "color");
    }

    @Override
    public void handle(@NonNull View view, @NonNull String attributeName, int resId) {
        super.handle(view, attributeName, resId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ColorStateList colorStateList = getColorStateList(view.getContext(), resId);
            ImageView imageView = (ImageView) view;
            Drawable drawable = imageView.getDrawable();
            if (drawable != null) {
                drawable = drawable.mutate();
                drawable.setTintList(colorStateList);
                if (drawable.isStateful()) {
                    drawable.setState(imageView.getDrawableState());
                }
            }
        }
    }
}
