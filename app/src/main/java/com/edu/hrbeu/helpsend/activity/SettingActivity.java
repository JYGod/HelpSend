package com.edu.hrbeu.helpsend.activity;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.cache.ACache;
import com.edu.hrbeu.helpsend.databinding.ActivitySettingBinding;
import com.edu.hrbeu.helpsend.utils.CommonUtil;
import com.edu.hrbeu.helpsend.utils.TopMenuHeader;

import java.io.File;


public class SettingActivity extends Activity {
    private ActivitySettingBinding mBinding;
    private ACache mCache;
    private Activity mActivity;
    private Context mContext;
    private TopMenuHeader top;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCache = ACache.get(this);
        mContext = this;
        mActivity = this;
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        initView();
        clickListener();
    }

    private void initView() {
        top = new TopMenuHeader(getWindow().getDecorView());
        top.topMenuTitle.setText("设置");
        String cacheSize = mCache.CacheSize();
        mBinding.tvCacheSize.setText(cacheSize);

    }

    private void clickListener() {
        mBinding.selectClearCache.setOnClickListener((View v) -> {
            mCache.clear();
            CommonUtil.startActivityWithFinish(mActivity, LoginActivity.class);
        });

        top.topMenuLeft.setOnClickListener((View v) -> {
            finish();
        });
    }
}
