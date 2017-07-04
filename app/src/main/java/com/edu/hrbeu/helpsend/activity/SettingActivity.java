package com.edu.hrbeu.helpsend.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.edu.hrbeu.helpsend.MyApplication;
import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.cache.ACache;
import com.edu.hrbeu.helpsend.databinding.ActivitySettingBinding;
import com.edu.hrbeu.helpsend.global.CustomDialog;
import com.edu.hrbeu.helpsend.pojo.ResponsePojo;
import com.edu.hrbeu.helpsend.seivice.OrderService;
import com.edu.hrbeu.helpsend.utils.CommonUtil;
import com.edu.hrbeu.helpsend.utils.TopMenuHeader;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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


        });

        top.topMenuLeft.setOnClickListener((View v) -> {
            finish();
        });

        mBinding.btnQuit.setOnClickListener((View v) -> {

            CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
            builder.setMessage("注销登录?");
            builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mCache.clear();
                    CommonUtil.startActivityWithFinish(mActivity, LoginActivity.class);
                }
            });
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        });
    }
}
