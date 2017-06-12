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
import android.widget.RatingBar;

import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.adapter.TimeLineAdapter;
import com.edu.hrbeu.helpsend.bean.Order;
import com.edu.hrbeu.helpsend.databinding.ActivityTimelineBinding;
import com.edu.hrbeu.helpsend.pojo.ResponsePojo;
import com.edu.hrbeu.helpsend.seivice.OrderService;
import com.edu.hrbeu.helpsend.utils.CommonUtil;
import com.edu.hrbeu.helpsend.utils.ImgLoadUtil;
import com.edu.hrbeu.helpsend.utils.TopMenuHeader;

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
    private String commit;
    private Activity mActivity;
    private String status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        mActivity=this;
        Intent intent=getIntent();
        commit=intent.getStringExtra("commit");
        orderId=intent.getStringExtra("orderId");
        avatar=intent.getStringExtra("avatar");
        status=intent.getStringExtra("status");
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        initView();
        clickListener();
    }

    private void clickListener() {
        top.topMenuLeft.setOnClickListener((View v)->{
            finish();
        });

        mBinding.btnEstimate.setOnClickListener((View v)->{
            mBinding.rating.dialogRate.setVisibility(View.VISIBLE);
        });
        mBinding.rating.negativeButton.setOnClickListener((View v)->{
            mBinding.rating.dialogRate.setVisibility(View.GONE);
        });
        mBinding.rating.positiveBotton.setOnClickListener((View v)->{

          Log.e("star", String.valueOf(mBinding.rating.ratingBar.getRating()));
            //提交
            OrderService service = retrofit.create(OrderService.class);
            Call<ResponsePojo> call = service.ratingOrder(orderId,String.valueOf(mBinding.rating.ratingBar.getRating()));
            call.enqueue(new Callback<ResponsePojo>() {
                @Override
                public void onResponse(Call<ResponsePojo> call, Response<ResponsePojo> response) {
                    ResponsePojo pojo=response.body();
                    CommonUtil.showToast(mContext,pojo.getMessage());
                    mBinding.rating.dialogRate.setVisibility(View.GONE);
                    CommonUtil.startActivityWithFinish(mActivity,MyorderActivity.class);
                }

                @Override
                public void onFailure(Call<ResponsePojo> call, Throwable t) {
                    CommonUtil.showToast(mContext,"评分失败！");
                    mBinding.rating.dialogRate.setVisibility(View.GONE);
                }
            });
        });
    }

    private void initView() {
        if (commit.equals("-1")&&status.equals("2")){
            mBinding.btnEstimate.setVisibility(View.VISIBLE);
        }else {
            mBinding.btnEstimate.setVisibility(View.GONE);
        }
        mBinding.timelineRecycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.timelineRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        mBinding.timelineRecycler.setHasFixedSize(true);
        ImgLoadUtil.displayCircle(mBinding.rating.ivAvatar,avatar);
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
