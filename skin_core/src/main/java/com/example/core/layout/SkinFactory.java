package com.example.core.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created By hudawei
 * on 2020/12/9 0009
 * 布局加载器的Factory2实现
 */
public class SkinFactory implements LayoutInflater.Factory2 {
    private SkinLayoutInflater mSkinLayoutInflater;

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return createView(parent, name, context, attrs);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return onCreateView(null, name, context, attrs);
    }

    private View createView(View parent, final String name, @NonNull Context context,
                            @NonNull AttributeSet attrs) {
        if (mSkinLayoutInflater == null)
            mSkinLayoutInflater = new SkinLayoutInflater();

        return mSkinLayoutInflater.createView(parent, name, context, attrs, false,
                false, /* Only read android:theme pre-L (L+ handles this anyway) */
                true/* Read read app:theme as a fallback at all times for legacy reasons */
        );
    }
}
