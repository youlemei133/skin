package com.example.core.attr.handler;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;


import com.example.core.attr.SkinAttrItem;
import com.example.core.attr.SkinView;
import com.example.core.attr.SkinViews;

import java.util.List;

/**
 * Created By hudawei
 * on 2020/12/9 0009
 * 皮肤属性处理工具类
 */
public class SkinAttrHandlerUtils {

    /**
     * 处理指定View的指定属性
     *
     * @param view          需处理的View
     * @param attributeName 需处理的属性名
     * @param resId         需处理的属性资源
     * @param add           是否需要添加到SkinView容器中
     */
    public static void apply(@NonNull View view, @NonNull String attributeName, int resId, boolean add) {
        List<SkinAttrHandler> handlers = HandlerMap.get(attributeName);
        if (handlers == null || handlers.size() == 0)
            return;
        for (SkinAttrHandler handler : handlers) {
            if (handler.isSupport(view, attributeName, resId)) {
                if (add)
                    tryAddSkinView(view, attributeName, resId);
                handler.handle(view, attributeName, resId);
                break;
            }
        }
    }

    /**
     * 如果View在SkinView的容器中不存在，则创建SkinView并添加到容器中。
     * 更新其属性的属性资源ID
     */
    public static void tryAddSkinView(@NonNull View view, @NonNull String attributeName, int resId) {
        //从容器中获取View对应的SkinView
        SkinView skinView = SkinViews.getInstance().get(view);
        if (skinView == null) {
            //如果没有，就创建一个SkinView，然后添加到容器中
            skinView = SkinViews.getInstance().add(view);
        }
        //遍历SkinView的所有属性，寻找对应属性的SkinAttrItem对象
        List<SkinAttrItem> skinAttrItems = skinView.getSkinAttrItems();
        SkinAttrItem skinAttrItem = null;
        for (SkinAttrItem item : skinAttrItems) {
            if (TextUtils.equals(item.attrName, attributeName)) {
                skinAttrItem = item;
                break;
            }
        }
        //如果不存在就创建一个SkinAttrItem，并添加到SKinView的属性集合中
        if (skinAttrItem == null) {
            skinAttrItem = SkinViews.getInstance().acquireSkinAttrItem(attributeName, resId);
            skinAttrItems.add(skinAttrItem);
        } else {
            //存在则更新资源Id
            skinAttrItem.resId = resId;
        }
    }
}
