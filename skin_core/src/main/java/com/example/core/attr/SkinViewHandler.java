package com.example.core.attr;

import android.util.Log;
import android.view.View;

import androidx.core.util.Pools;

import com.example.core.attr.handler.SkinAttrHandlerUtils;

import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created By hudawei
 * on 2020/12/8 0008
 * 处理SkinView
 */
public class SkinViewHandler {
    /**
     * 存放所有的SkinView
     */
    private final List<SkinView> skinViews;
    /**
     * 单例模式
     */
    private static SkinViewHandler sInstance;
    /**
     * WeakReferenceView回收队列
     */
    private final ReferenceQueue<View> queue;
    private volatile boolean isShutdown;
    private ExecutorService monitorClearedResourcesExecutor;
    /**
     * SkinView对象缓存池
     */
    private final Pools.SynchronizedPool<SkinView> mSkinViewPools;
    /**
     * SkinAttrItem对象缓存池
     */
    private final Pools.SynchronizedPool<SkinAttrItem> mSkinAttrItemPools;

    private SkinViewHandler() {
        skinViews = new ArrayList<>();
        queue = new ReferenceQueue<>();
        mSkinViewPools = new Pools.SynchronizedPool<>(100);
        mSkinAttrItemPools = new Pools.SynchronizedPool<>(100);
        cleanReferenceQueue();
    }

    /**
     * 监听View的回收，将回收的View对应的SkinView移除skinViews容器，然后回收该SkinView
     */
    private void cleanReferenceQueue() {
        monitorClearedResourcesExecutor = Executors.newSingleThreadExecutor();
        monitorClearedResourcesExecutor.execute(() -> {
            while (!isShutdown) {
                try {
                    //这是一个阻塞方法，如果View在该队列中，说明该View已垃圾回收了
                    SkinView.WeakReferenceView remove = (SkinView.WeakReferenceView) queue.remove();
                    SkinView skinView = remove.getSkinView();
                    Log.e("ViewAttrHelper", skinView + "被回收" + skinViews.size());
                    //回收SkinView到缓存对象池
                    recycleSkinView(skinView);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 回收SkinView到对象池
     */
    private void recycleSkinView(SkinView skinView) {
        //从队列中移除
        skinViews.remove(skinView);
        //回收到对象缓存池
        mSkinViewPools.release(skinView);
        //获取里面的所有属性，并回收，然后清除
        List<SkinAttrItem> skinAttrItems = skinView.getSkinAttrItems();
        for (SkinAttrItem item : skinAttrItems) {
            mSkinAttrItemPools.release(item);
        }
        skinAttrItems.clear();
    }

    /**
     * 获取单例对象
     */
    public static SkinViewHandler getInstance() {
        if (sInstance == null) {
            sInstance = new SkinViewHandler();
        }
        return sInstance;
    }

    /**
     * 从缓存池中获取，如果没有就创建一个新的SkinAttrItem对象
     *
     * @param attributeName 属性名
     * @param resId         对应的资源ID
     */
    public SkinAttrItem acquireSkinAttrItem(String attributeName, int resId) {
        SkinAttrItem skinAttrItem = mSkinAttrItemPools.acquire();
        if (skinAttrItem == null)
            skinAttrItem = new SkinAttrItem();
        skinAttrItem.init(attributeName, resId);
        return skinAttrItem;
    }

    /**
     * 从容器中移除指定View
     *
     * @param view 需移除的View
     */
    public void remove(View view) {
        //先从容器中获取
        SkinView skinView = get(view);
        //如果获取到了就回收该SkinView
        if (skinView != null)
            recycleSkinView(skinView);
    }

    /**
     * 从容器中获取指定SkinView
     *
     * @param view SkinView中存储的View
     * @return SkinView对象，如果没有则返回Null
     */
    public SkinView get(View view) {
        for (int i = skinViews.size() - 1; i >= 0; i--) {
            SkinView skinView = skinViews.get(i);
            if (skinView.getView() == view)
                return skinView;
        }
        return null;
    }

    /**
     * 往容器中添加一个View
     *
     * @param view 需添加的View
     * @return View对应的SkinView
     */
    public SkinView add(View view) {
        SkinView skinView = mSkinViewPools.acquire();
        if (skinView == null)
            skinView = new SkinView();
        skinView.init(view, queue);
        skinViews.add(skinView);
        return skinView;
    }

    /**
     * 遍历容器中所有SkinView，然后处理SkinView中的所有属性
     */
    public void apply() {
        for (SkinView skinView : skinViews) {
            //获取View
            View view = skinView.getView();
            //获取View设置的属性
            List<SkinAttrItem> skinAttrItems = skinView.getSkinAttrItems();
            if (view != null) {
                for (SkinAttrItem item : skinAttrItems) {
                    //找到处理指定属性的处理类进行处理
                    SkinAttrHandlerUtils.apply(view, item.attrName, item.resId, false);
                }
            }
        }
    }

    /**
     * 停止监听View回收
     */
    public void shutdown() {
        isShutdown = true;
        monitorClearedResourcesExecutor.shutdownNow();
    }

    /**
     * 获取所有SkinView
     */
    public List<SkinView> getSkinViews() {
        return skinViews;
    }
}
