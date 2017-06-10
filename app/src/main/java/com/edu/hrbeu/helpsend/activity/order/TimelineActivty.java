package com.edu.hrbeu.helpsend.activity.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.adapter.TimeLineAdapter;
import com.edu.hrbeu.helpsend.databinding.ActivityTimelineBinding;
import com.edu.hrbeu.helpsend.seivice.OrderService;
import com.edu.hrbeu.helpsend.utils.CommonUtil;
import com.edu.hrbeu.helpsend.utils.TopMenuHeader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.edu.hrbeu.helpsend.seivice.OrderService.retrofit;


public class TimelineActivty extends Activity{
    private ActivityTimelineBinding mBinding;
    private Context mContext;
    private TimeLineAdapter adapter;
    private String orderId;
    private TopMenuHeader top;
    private String avatar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        Intent intent=getIntent();
        orderId=intent.getStringExtra("orderId");
        avatar=intent.getStringExtra("avatar");
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        initView();
        clickListener();
    }

    private void clickListener() {
        top.topMenuLeft.setOnClickListener((View v)->{
            finish();
        });

        mBinding.btnEstimate.setOnClickListener((View v)->{

        });
    }

    private void initView() {
        mBinding.timelineRecycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.timelineRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        mBinding.timelineRecycler.setHasFixedSize(true);
        top=new TopMenuHeader(getWindow().getDecorView());
        top.topMenuTitle.setText("订单流程详情");
        OrderService service=retrofit.create(OrderService.class);
        Call<ArrayList<String>> call = service.getOrderProgress(orderId);
        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                ArrayList<String> mList=response.body();
                adapter=new TimeLineAdapter(mList,mContext);
                mBinding.timelineRecycler.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                CommonUtil.showToast(mContext,"查询失败！");
            }
        });

    }
}
