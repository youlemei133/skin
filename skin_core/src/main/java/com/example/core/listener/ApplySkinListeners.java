package com.example.core.listener;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created By hudawei
 * on 2020/12/19 0019
 * 管理所有皮肤切换监听
 */
public class ApplySkinListeners {
    private final ReferenceQueue<ApplySkinListener> queue;
    private final List<WeakReferenceListener> listeners;
    private static ApplySkinListeners mInstance;

    public static ApplySkinListeners getInstance() {
        if (mInstance == null)
            mInstance = new ApplySkinListeners();
        return mInstance;
    }

    private ApplySkinListeners() {
        listeners = new ArrayList<>();
        queue = new ReferenceQueue<>();
        Executor cleanExecutor = Executors.newSingleThreadExecutor();
        cleanExecutor.execute(() -> {
            while (true) {
                try {
                    WeakReferenceListener reference = (WeakReferenceListener) queue.remove();
                    if (reference != null) {
                        listeners.remove(reference);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 添加皮肤切换监听
     *
     * @param listener 监听
     */
    public void add(ApplySkinListener listener) {
        WeakReferenceListener ref = new WeakReferenceListener(listener, queue);
        listeners.add(ref);
    }

    /**
     * 皮肤切换时调用该方法
     *
     * @param skinName 当前使用的皮肤名
     */
    public void apply(String skinName) {
        for (WeakReferenceListener ref :
                listeners) {
            ApplySkinListener applySkinListener = ref.get();
            if (applySkinListener != null) {
                applySkinListener.onApplySkin(skinName);
            }
        }
    }

    private static class WeakReferenceListener extends WeakReference<ApplySkinListener> {

        public WeakReferenceListener(ApplySkinListener referent,
                                     ReferenceQueue<? super ApplySkinListener> q) {
            super(referent, q);
        }
    }
}
