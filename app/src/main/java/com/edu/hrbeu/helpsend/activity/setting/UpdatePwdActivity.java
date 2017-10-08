package com.edu.hrbeu.helpsend.activity.setting;


import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.databinding.ActivityUpdatePwdBinding;
import com.edu.hrbeu.helpsend.utils.TopMenuHeader;

public class UpdatePwdActivity extends Activity{
    private ActivityUpdatePwdBinding mBinding;
    private TopMenuHeader top;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_update_pwd);

        initView();
        clickListener();

    }

    private void clickListener() {
        top.topMenuLeft.setOnClickListener((View v) -> {
            finish();
        });
    }

    private void initView() {
        top = new TopMenuHeader(getWindow().getDecorView());
        top.topMenuTitle.setText("修改密码");
    }
}
