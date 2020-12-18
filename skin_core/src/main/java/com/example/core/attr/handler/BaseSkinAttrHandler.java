package com.example.core.attr.handler;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.core.resource.SkinResourcesManager;

import java.io.File;


/**
 * Created By hudawei
 * on 2020/12/10 0010
 * 处理属性基类
 * 所有自定义处理属性类都需继承该类
 */
public class BaseSkinAttrHandler implements SkinAttrHandler {
    @Override
    public boolean isSupport(@NonNull View view, @NonNull String attributeName, int resId) {
        return false;
    }

    @Override
    public void handle(@NonNull View view, @NonNull String attributeName, int resId) {

    }

    /**
     * 获取资源名
     *
     * @param context 上下文
     * @param resId   资源Id
     * @return 资源名
     */
    protected String getResourceName(Context context, int resId) {
        Resources appResources = context.getApplicationContext().getResources();
        return appResources.getResourceEntryName(resId);
    }

    /**
     * 获取资源类型
     *
     * @param context 上下文
     * @param resId   资源Id
     * @return 资源类型
     */
    protected String getResourceType(Context context, int resId) {
        Resources appResources = context.getApplicationContext().getResources();
        return appResources.getResourceTypeName(resId);
    }

    /**
     * 获取资源对应的包名
     *
     * @param context 上下文
     * @param resId   资源Id
     * @return 资源所在包名
     */
    protected String getResourcePackage(Context context, int resId) {
        Resources appResources = context.getApplicationContext().getResources();
        return appResources.getResourcePackageName(resId);
    }

    /**
     * 获取当前皮肤资源
     *
     * @param context 上下文
     * @return 皮肤资源
     */
    protected Resources getCurSkinResources(Context context) {
        String skinName = getCurSkin(context);
        String skinPath = getSkinDir(context) + "/" + skinName;
        return SkinResourcesManager.getInstance().getResources(context, skinName, skinPath);
    }

    private File getSkinDir(Context context) {
        File cacheDir = context.getApplicationContext().getCacheDir();
        File skin = new File(cacheDir, "skin");
        if (!skin.exists())
            skin.mkdir();
        return skin;
    }

    private String getCurSkin(Context context) {
        SharedPreferences sp = context.getSharedPreferences("skin", Context.MODE_PRIVATE);
        return sp.getString("CUR_SKIN_NAME", "");
    }

    /**
     * 获取App资源
     *
     * @param context 上下文
     * @return App资源
     */
    protected Resources getAppResources(Context context) {
        return context.getApplicationContext().getResources();
    }

    /**
     * 获取指定资源中对应的资源Id
     *
     * @param context       上下文
     * @param skinResources 指定资源
     * @param resId         原始资源Id
     * @return 原始资源Id对应指定资源中的资源Id, 如果为0代表没有对应资源
     */
    protected int getSkinIdentifier(Context context, Resources skinResources, int resId) {
        return skinResources.getIdentifier(getResourceName(context, resId),
                getResourceType(context, resId),
                getResourcePackage(context, resId));
    }

    /**
     * 获取颜色
     *
     * @param context 上下文
     * @param resId   颜色Id
     * @return 如果皮肤包中有对应的颜色则返回，没有则返回App中对应的颜色
     */
    protected int getColor(Context context, int resId) {
        Resources skinResources = getCurSkinResources(context);
        int skinIdentifier = getSkinIdentifier(context, skinResources, resId);
        int color;
        if (skinIdentifier != 0) {
            color = skinResources.getColor(skinIdentifier);
        } else {
            color = getAppResources(context).getColor(resId);
        }
        return color;
    }

    /**
     * 获取Drawable
     *
     * @param context 上下文
     * @param resId   资源Id
     * @return 如果皮肤包中有对应的Drawable则返回，没有则返回App中对应的Drawable
     */
    protected Drawable getDrawable(Context context, int resId) {
        Resources skinResources = getCurSkinResources(context);
        int skinIdentifier = getSkinIdentifier(context, skinResources, resId);
        Drawable drawable;
        if (skinIdentifier != 0) {
            drawable = skinResources.getDrawable(skinIdentifier);
        } else {
            drawable = getAppResources(context).getDrawable(resId);
        }
        return drawable;
    }

    /**
     * 获取ColorStateList
     *
     * @param context 上下文
     * @param resId   资源Id
     * @return 如果皮肤包中有对应的ColorStateList则返回，没有则返回App中对应的ColorStateList
     */
    protected ColorStateList getColorStateList(Context context, int resId) {
        Resources skinResources = getCurSkinResources(context);
        int skinIdentifier = getSkinIdentifier(context, skinResources, resId);
        ColorStateList colorStateList;
        if (skinIdentifier != 0) {
            colorStateList = skinResources.getColorStateList(skinIdentifier);
        } else {
            colorStateList = getAppResources(context).getColorStateList(resId);
        }
        return colorStateList;
    }
}
