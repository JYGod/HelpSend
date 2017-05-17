package com.edu.hrbeu.helpsend.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.databinding.ActivityLoginBinding;
import com.edu.hrbeu.helpsend.global.GlobalData;
import com.edu.hrbeu.helpsend.listener.Util;
import com.edu.hrbeu.helpsend.utils.ImgLoadUtil;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;


public class LoginActivity extends Activity implements View.OnClickListener {
    private Tencent mTencent;
    private Context mContext;
    private ActivityLoginBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_login);
        mContext=this;
        initView();
        clickListener();


    }

    private void clickListener() {
        mBinding.ivWeixin.setOnClickListener(this);
        mBinding.ivQq.setOnClickListener(this);
    }

    private void initView() {
        ImgLoadUtil.displayLoginCircle(mBinding.ivWeixin,R.drawable.weixin_img);
        ImgLoadUtil.displayLoginCircle(mBinding.ivQq,R.drawable.qq_img);
    }


    IUiListener listener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
            Log.e("SDKQQAgentPref", "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());

        }
    };
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                Util.showResultDialog(mContext, "返回为空", "登录失败");
                Log.e("登录失败","登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                Util.showResultDialog(mContext, "返回为空", "登录失败");
                Log.e("登录失败","登录失败");
                return;
            }
            Util.showResultDialog(mContext, response.toString(), "登录成功");
            Log.e("登录成功","==========登录成功===========");
            doComplete((JSONObject)response);
        }

        @Override
        public void onError(UiError uiError) {

            Util.dismissDialog();
        }

        protected void doComplete(JSONObject values) {
            Log.e("values", String.valueOf(values));
            Intent intent=new Intent(mContext,BottomTabActivity.class);
            startActivity(intent);
            finish();
        }


        @Override
        public void onCancel() {
            Util.dismissDialog();
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_weixin:
                break;
            case R.id.iv_qq:
                mTencent = Tencent.createInstance(GlobalData.QQ_APP_ID, this.getApplicationContext());
                if (!mTencent.isSessionValid()) {
                    mTencent.login(this, "all", listener);
                }
                break;
        }

    }
}

