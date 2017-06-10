package com.edu.hrbeu.helpsend.activity.order;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.databinding.ActivityRemarkBinding;
import com.edu.hrbeu.helpsend.global.GlobalData;
import com.edu.hrbeu.helpsend.utils.CommonUtil;
import com.edu.hrbeu.helpsend.utils.TopMenuHeader;

public class RemarkActivity extends Activity implements View.OnClickListener {
    private ActivityRemarkBinding mBinding;
    private Context mContext;
    private TopMenuHeader top;
    private String etRemark="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_remark);
        initView();
        clickListener();
    }

    private void initView() {
        top=new TopMenuHeader(getWindow().getDecorView());
        top.topMenuTitle.setText("备注");
        mBinding.etRemark.setText(GlobalData.MY_ORDER.getRemark()==null?"":GlobalData.MY_ORDER.getRemark());
    }

    private void clickListener() {
        mBinding.btnRemarkSure.setOnClickListener(this);
        top.topMenuLeft.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_remark_sure:
                etRemark=mBinding.etRemark.getText().toString();
                if (etRemark.equals("")){
                   CommonUtil.showToast(mContext,"请填写备注信息~");
                }else {
                    GlobalData.MY_ORDER.setRemark(etRemark);
                    finish();
                }
                break;
            case R.id.top_menu_left:
                finish();
                break;
        }
    }
}
