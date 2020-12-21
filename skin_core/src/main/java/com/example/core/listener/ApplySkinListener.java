package com.example.core.listener;

/**
 * Created By hudawei
 * on 2020/12/19 0019
 * 切换皮肤回调，用于皮肤切换时做特定处理
 */
public interface ApplySkinListener {
    /**
     * 切换皮肤时，回调该方法
     *
     * @param skinName 当前使用的皮肤名
     */
    void onApplySkin(String skinName);
}
