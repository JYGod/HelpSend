package com.edu.hrbeu.helpsend.activity.grab;


import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.adapter.GrabOrderAdapter;
import com.edu.hrbeu.helpsend.bean.GrabOrder;
import com.edu.hrbeu.helpsend.bean.GrabResponse;
import com.edu.hrbeu.helpsend.bean.OrderResponse;
import com.edu.hrbeu.helpsend.databinding.ActivityGrabBinding;
import com.edu.hrbeu.helpsend.global.GlobalData;
import com.edu.hrbeu.helpsend.seivice.OrderService;
import com.google.gson.Gson;
import com.scu.miomin.shswiperefresh.core.SHSwipeRefreshLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.edu.hrbeu.helpsend.seivice.OrderService.retrofit;

public class GrabActivity extends Activity implements SHSwipeRefreshLayout.SHSOnRefreshListener {
    private ActivityGrabBinding mBinding;
    private Context mContext;
    private GrabOrderAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_grab);
        initView();
        clickListener();
    }

    private void clickListener() {
        mBinding.swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initView() {
       // View view=View.inflate(getApplicationContext(),R.layout.lay_logo,null);
       // mBinding.swipeRefreshLayout.setHeaderView(view);
        mBinding.grabRecyclerview.setItemAnimator(new DefaultItemAnimator());
        mBinding.grabRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        OrderService service=retrofit.create(OrderService.class);
        Call<GrabResponse> call=service.getGrabOrders();
        call.enqueue(new Callback<GrabResponse>() {
            @Override
            public void onResponse(Call<GrabResponse> call, Response<GrabResponse> response) {
                GrabResponse grabResponse=response.body();
                ArrayList<GrabOrder>grabOrders=grabResponse.getMessage();
                adapter=new GrabOrderAdapter(mContext,grabOrders);
                mBinding.grabRecyclerview.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<GrabResponse> call, Throwable t) {
                Log.e("获取数据失败------->","获取数据失败!");
            }
        });
    }

    @Override
    public void onRefresh() {
        OrderService service=retrofit.create(OrderService.class);
        Call<GrabResponse> call=service.getGrabOrders();
        call.enqueue(new Callback<GrabResponse>() {
            @Override
            public void onResponse(Call<GrabResponse> call, Response<GrabResponse> response) {
                GrabResponse grabResponse=response.body();
                ArrayList<GrabOrder>grabOrders=grabResponse.getMessage();
                adapter=new GrabOrderAdapter(mContext,grabOrders);
                mBinding.grabRecyclerview.setAdapter(adapter);
                mBinding.swipeRefreshLayout.finishRefresh();
            }

            @Override
            public void onFailure(Call<GrabResponse> call, Throwable t) {
                Log.e("获取数据失败------->","获取数据失败!");
                mBinding.swipeRefreshLayout.finishRefresh();
            }
        });
    }

    @Override
    public void onLoading() {
        mBinding.swipeRefreshLayout.finishLoadmore();
    }

    @Override
    public void onRefreshPulStateChange(float v, int i) {

    }

    @Override
    public void onLoadmorePullStateChange(float v, int i) {

    }
}
