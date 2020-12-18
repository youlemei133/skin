package com.example.core.resource;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By hudawei
 * on 2020/12/9 0009
 * 皮肤包资源管理类
 */
public class SkinResourcesManager {
    /**
     * 单例
     */
    private static final SkinResourcesManager sInstance = new SkinResourcesManager();
    /**
     * 皮肤资源容器
     * 皮肤名->对应资源Resources
     */
    private final Map<String, Resources> mResourcesMap;

    public static SkinResourcesManager getInstance() {
        return sInstance;
    }

    private SkinResourcesManager() {
        mResourcesMap = new HashMap<>();
    }

    /**
     * 获取皮肤名对应的资源Resources
     *
     * @param context  上下文
     * @param skinName 皮肤名
     * @param skinPath 皮肤存放路径
     * @return 皮肤对应的Resources，若皮肤文件不存在，则返回App的资源Resources
     */
    public Resources getResources(Context context, String skinName, String skinPath) {
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            String activityClass = activity.getClass().getCanonicalName();
            if (!SkinActivities.isApplySkin(activityClass))
                return context.getResources();
        }
        if (TextUtils.isEmpty(skinPath))
            return context.getResources();
        Resources resources = mResourcesMap.get(skinName);
        if (resources == null) {
            resources = createResources(context, skinPath);
            mResourcesMap.put(skinName, resources);
        }
        return resources;
    }

    /**
     * 更新皮肤资源
     *
     * @param context  上下文
     * @param skinName 需更新的皮肤名
     * @param skinPath 皮肤存放路径
     * @return 更新后的资源Resources，若皮肤文件不存在，则返回App的资源Resources
     */
    public Resources updateResources(Context context, String skinName, String skinPath) {
        if (TextUtils.isEmpty(skinPath))
            return context.getResources();
        Resources resources = createResources(context, skinPath);
        mResourcesMap.put(skinName, resources);
        return resources;
    }

    /**
     * 创建指定皮肤路径对应的资源Resources
     *
     * @param context  上下文
     * @param skinPath 皮肤路径
     * @return 皮肤文件对应的资源Resources，若皮肤文件不存在，则返回App的资源Resources
     */
    private Resources createResources(Context context, String skinPath) {
        Resources resources = context.getResources();
        if (!TextUtils.isEmpty(skinPath)) {
            try {
                AssetManager assets = createAssetManager(skinPath);
                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                Configuration config = context.getResources().getConfiguration();
                return new Resources(assets, metrics, config);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resources;
    }

    /**
     * 反射创建AssetManager，加载指定资源文件
     *
     * @param resPath 资源路径
     * @return AssetManager
     */
    private AssetManager createAssetManager(String resPath) {
        try {
            //使用默认构造函数创建新的AssetManager进行替换
            Constructor<AssetManager> declaredConstructor = AssetManager.class.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            AssetManager assetManager = declaredConstructor.newInstance();
            //调用addAssetPath()方法设置资源路径
            Method addAssetPathM = getMethod(AssetManager.class, "addAssetPath", String.class);
            addAssetPathM.invoke(assetManager, resPath);
            return assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Method getMethod(Class<?> clazz, String methodName, Class<?>... args) {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, args);
        } catch (NoSuchMethodException e) {
            try {
                method = clazz.getMethod(methodName, args);
            } catch (NoSuchMethodException noSuchMethodException) {
                Class<?> superClass = clazz.getSuperclass();
                if (superClass != null) {
                    method = getMethod(superClass, methodName, args);
                }
            }
        }
        if (method != null && !method.isAccessible()) {
            method.setAccessible(true);
        }
        return method;
    }
}
