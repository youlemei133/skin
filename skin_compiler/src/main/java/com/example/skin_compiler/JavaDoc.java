package com.example.skin_compiler;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;


/**
 * Created By hudawei
 * on 2020/12/17 0017
 * 注释帮助类
 */
class JavaDoc {
    private StringBuilder doc;
    private Object[] args;
    private static JavaDoc mInstance;

    private JavaDoc() {
        this.doc = new StringBuilder();
    }

    public static JavaDoc getInstance() {
        if (mInstance == null)
            mInstance = new JavaDoc();
        return mInstance;
    }

    /**
     * 写入一行注释
     *
     * @param content 注释内容
     */
    public JavaDoc write(String content) {
        doc.append(content).append("\n");
        return this;
    }

    /**
     * 写入空行
     */
    public JavaDoc writeEmpty() {
        doc.append("\n");
        return this;
    }

    /**
     * 添加参数
     */
    public JavaDoc args(Object... args) {
        this.args = args;
        return this;
    }

    /**
     * 为类添加注释
     */
    public void commit(TypeSpec.Builder builder) {
        if (args != null)
            builder.addJavadoc(doc.toString(), args);
        else
            builder.addJavadoc(doc.toString());
        doc = new StringBuilder();
        args = null;
    }

    /**
     * 为字段添加注释
     */
    public void commit(FieldSpec.Builder builder) {
        if (args != null)
            builder.addJavadoc(doc.toString(), args);
        else
            builder.addJavadoc(doc.toString());
        doc = new StringBuilder();
        args = null;
    }

    /**
     * 为方法添加注释
     */
    public void commit(MethodSpec.Builder builder) {
        if (args != null)
            builder.addJavadoc(doc.toString(), args);
        else
            builder.addJavadoc(doc.toString());
        doc = new StringBuilder();
        args = null;
    }
}
