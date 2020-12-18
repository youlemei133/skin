package com.example.skin;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.example.skin_annotation.SupportSkin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By hudawei
 * on 2020/12/8 0008
 */
@SupportSkin()
public class TestActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Skin.setApplySkin(this, true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        ViewPager viewPager = findViewById(R.id.viewPager);
        List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            fragments.add(TestFragment.newInstance(i));
        }
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
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
