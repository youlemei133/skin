package com.example.skin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.skin_annotation.SupportSkin;


@SupportSkin()
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void test(View view) {
        startActivity(new Intent(this, TestActivity.class));
    }

    public void applyNightSkin(View view) {
        Skin.putCurSkin(this, "night.skin");
        //应用当前皮肤
        Skin.applySkin(this);
    }

    public void applyNormalSkin(View view) {
        Skin.putCurSkin(this, "");
        //应用当前皮肤
        Skin.applySkin(this);
    }
}