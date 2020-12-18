package com.example.core.attr;

import java.util.Objects;

/**
 * Created By hudawei
 * on 2020/12/9 0009
 * 用于存放属性名和资源Id实体类
 */
public class SkinAttrItem {
    /**
     * 属性名
     */
    public String attrName;
    /**
     * 设置的资源Id
     */
    public int resId;

    public void init(String attrName, int resId) {
        this.attrName = attrName;
        this.resId = resId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinAttrItem)) return false;
        SkinAttrItem that = (SkinAttrItem) o;
        return resId == that.resId &&
                Objects.equals(attrName, that.attrName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attrName, resId);
    }
}
