package com.example.skin;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created By hudawei
 * on 2020/12/15 0015
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Skin.installViewFactory(this);
        super.onCreate(savedInstanceState);
    }
}
