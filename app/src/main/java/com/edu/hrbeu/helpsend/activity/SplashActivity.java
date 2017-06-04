package com.edu.hrbeu.helpsend.activity;


import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.cache.ACache;
import com.edu.hrbeu.helpsend.databinding.ActivitySplashBinding;
import com.edu.hrbeu.helpsend.utils.CommonUtil;
import com.edu.hrbeu.helpsend.utils.ImgLoadUtil;

public class SplashActivity extends Activity{

    private static int SPLASH_TIME_OUT = 2000;  //2 Seconds
    private ActivitySplashBinding mBinding;
    private Activity mActivity;
    private ACache mCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCache= ACache.get(this);
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_splash);
        mActivity=this;
        ImgLoadUtil.displayCircle(mBinding.ivAvatar,mCache.getAsString("mAvatar"));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CommonUtil.startActivityWithFinish(mActivity,BottomTabActivity.class);
            }
        },SPLASH_TIME_OUT);
    }
}
