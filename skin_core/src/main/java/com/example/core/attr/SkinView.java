package com.example.core.attr;

import android.view.View;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By hudawei
 * on 2020/12/9 0009
 * 存放View以及该View设置的属性集合
 */
public class SkinView {
    /**
     * View的弱引用，用于监听View被回收，以便进行处理
     */
    private WeakReferenceView refView;
    /**
     * 属性集合
     */
    private final List<SkinAttrItem> skinAttrItems = new ArrayList<>();

    /**
     * 初始化
     *
     * @param view  需要进行属性处理的View
     * @param queue 用于回收View的队列
     */
    public void init(View view, ReferenceQueue<View> queue) {
        refView = new WeakReferenceView(this, view, queue);
    }

    /**
     * 获取View
     */
    public View getView() {
        if (refView != null && refView.get() != null)
            return refView.get();
        return null;
    }

    /**
     * 获取属性集合
     */
    public List<SkinAttrItem> getSkinAttrItems() {
        return skinAttrItems;
    }

    /**
     * View的弱引用
     */
    public static class WeakReferenceView extends WeakReference<View> {
        private final SkinView skinView;

        public WeakReferenceView(SkinView skinView, View view, ReferenceQueue<View> queue) {
            super(view, queue);
            this.skinView = skinView;
        }

        public SkinView getSkinView() {
            return skinView;
        }
    }
}
