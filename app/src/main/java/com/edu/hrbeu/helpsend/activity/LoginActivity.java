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
import android.widget.Button;
import android.widget.EditText;

import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.adapter.LoginPageAdapter;
import com.edu.hrbeu.helpsend.cache.ACache;
import com.edu.hrbeu.helpsend.databinding.ActivityLoginBinding;
import com.edu.hrbeu.helpsend.listener.Util;
import com.edu.hrbeu.helpsend.pojo.UserPojo;
import com.edu.hrbeu.helpsend.pojo.VrfCodeResponsePojo;
import com.edu.hrbeu.helpsend.seivice.UserService;
import com.edu.hrbeu.helpsend.utils.CommonUtil;
import com.edu.hrbeu.helpsend.utils.ImgLoadUtil;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.white.countdownbutton.CountDownButton;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.edu.hrbeu.helpsend.seivice.UserService.retrofit;


public class LoginActivity extends Activity implements View.OnClickListener {
    private Context mContext;
    private ActivityLoginBinding mBinding;
    private CountDownButton btnMessage;
    private EditText etPhone1,etPhone2,etCode,etPwd;
    private Button btnLogin1,btnLogin2;
    private Activity mActivity;
    private ACache mCache;
    private String mPhone;
    private String mVrfCode;
    private UserPojo userPojo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCache= ACache.get(this);
       // mCache.clear();
        mContext=this;
        mActivity=this;
        String isLogin = mCache.getAsString("isLogin");
        if (isLogin==null||isLogin.equals("false")){
            mBinding= DataBindingUtil.setContentView(this, R.layout.activity_login);
            initView();
            clickListener();
        }else {
            CommonUtil.startActivityWithFinish(mActivity,SplashActivity.class);
        }

    }

    private void clickListener() {
        mBinding.ivWeixin.setOnClickListener(this);
        mBinding.ivQq.setOnClickListener(this);
        btnMessage.setOnClickListener(this);
        btnLogin1.setOnClickListener(this);
        btnLogin2.setOnClickListener(this);
    }

    private void initView() {
        ImgLoadUtil.displayLoginCircle(mBinding.ivWeixin,R.drawable.weixin_img);
        ImgLoadUtil.displayLoginCircle(mBinding.ivQq,R.drawable.qq_img);
        ArrayList<View> mViewList=new ArrayList<>();
        View view1= View.inflate(mContext,R.layout.login_phone,null);
        View view2= View.inflate(mContext,R.layout.login_pwd,null);
        mViewList.add(view1);
        mViewList.add(view2);
        LoginPageAdapter adapter=new LoginPageAdapter(mContext,mViewList);
        mBinding.vp.setAdapter(adapter);
        mBinding.navTab.setViewPager(mBinding.vp);
        btnMessage=(CountDownButton)view1.findViewById(R.id.btn_message);
        etPhone1=(EditText)view1.findViewById(R.id.et_phone);
        etPhone2=(EditText)view2.findViewById(R.id.et_phone);
        etCode=(EditText)view1.findViewById(R.id.et_code);
        etPwd=(EditText)view2.findViewById(R.id.et_pwd);
        btnLogin1=(Button)view1.findViewById(R.id.btn_code_login);
        btnLogin2=(Button)view2.findViewById(R.id.btn_pwd_login);
        etPhone1.setText(mCache.getAsString("userPhone")==null?"":mCache.getAsString("userPhone"));
        etPhone2.setText(mCache.getAsString("userPhone")==null?"":mCache.getAsString("userPhone"));
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
//                mTencent = Tencent.createInstance(GlobalData.QQ_APP_ID, this.getApplicationContext());
//                if (!mTencent.isSessionValid()) {
//                    mTencent.login(this, "all", listener);
//                }
                break;
            case R.id.btn_message:
                Log.e("send message :","========click=======");
                String phoneNum = etPhone1.getText().toString().trim();
                if (phoneNum.equals("")){
                    CommonUtil.showToast(mContext,"请输入手机号!");
                    btnMessage.removeCountDown();
                }else {
                    UserService service=retrofit.create(UserService.class);
                    Call<VrfCodeResponsePojo> call= service.getOwnerOrders(phoneNum);
                    call.enqueue(new Callback<VrfCodeResponsePojo>() {
                        @Override
                        public void onResponse(Call<VrfCodeResponsePojo> call, Response<VrfCodeResponsePojo> response) {
                            VrfCodeResponsePojo responsePojo=response.body();
                            if (responsePojo!=null){
                                mPhone=responsePojo.getMobile();
                                mVrfCode=responsePojo.getVrfCode();
                            }
                        }

                        @Override
                        public void onFailure(Call<VrfCodeResponsePojo> call, Throwable t) {
                            CommonUtil.showToast(mContext,"获取验证码失败！");
                        }
                    });
                }

                break;
            case R.id.btn_code_login:
                if (etPhone1.getText().toString().trim().equals("")||etCode.getText().toString().trim().equals("")){
                    CommonUtil.showToast(mContext,"请输入登录信息!");
                }else {
                    if (mPhone!=null&&mVrfCode!=null){
                        if (etPhone1.getText().toString().trim().equals(mPhone)&&
                                etCode.getText().toString().trim().equals(mVrfCode)){
                            UserService service=retrofit.create(UserService.class);
                            Call<UserPojo> call= service.quickLogin(mPhone);
                            call.enqueue(new Callback<UserPojo>() {
                                @Override
                                public void onResponse(Call<UserPojo> call, Response<UserPojo> response) {
                                    userPojo=response.body();
                                    settingCaches(userPojo);
                                    CommonUtil.startActivityWithFinish(mActivity,BottomTabActivity.class);
                                }

                                @Override
                                public void onFailure(Call<UserPojo> call, Throwable t) {
                                    CommonUtil.showToast(mContext,"登录失败！");
                                }
                            });
                        }else {
                            CommonUtil.showToast(mContext,"输入验证码错误！");
                        }
                    }
                }
                break;
            case R.id.btn_pwd_login:
                CommonUtil.startActivityWithFinish(mActivity,BottomTabActivity.class);
                break;
        }

    }

    private void settingCaches(UserPojo userPojo) {
        mCache.put("userPhone",mPhone,2*ACache.TIME_DAY);
        mCache.put("isLogin","true");
        mCache.put("mId",userPojo.getUserId());
        mCache.put("mNickName",userPojo.getNickName());
        mCache.put("mAvatar",userPojo.getAvatarPath());
        mCache.put("mGender",userPojo.getGender());
        mCache.put("mRole",userPojo.getRole());
        mCache.put("mExp",String.valueOf(userPojo.getExperience()));
    }
}

