package com.edu.hrbeu.helpsend.activity.order;


import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.adapter.MyOderAdapter;
import com.edu.hrbeu.helpsend.cache.ACache;
import com.edu.hrbeu.helpsend.databinding.ActivityMyorderBinding;
import com.edu.hrbeu.helpsend.global.GlobalData;
import com.edu.hrbeu.helpsend.pojo.MyOrderPojo;
import com.edu.hrbeu.helpsend.seivice.OrderService;
import com.edu.hrbeu.helpsend.utils.CommonUtil;
import com.edu.hrbeu.helpsend.utils.TopMenuHeader;
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener;

import java.util.ArrayList;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.edu.hrbeu.helpsend.seivice.OrderService.retrofit;

public class MyorderActivity extends Activity implements View.OnClickListener, OnFABMenuSelectedListener, SwipeRefreshLayout.OnRefreshListener {
    private final int SEND_ORDER=0;
    private final int GRAB_ORDER=1;
    private ActivityMyorderBinding mBinding;
    private TopMenuHeader top;
    private ACache mCache;
    private Context mContext;
    private String mStatus="all";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCache= ACache.get(this);
        mContext=this;
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_myorder);
        initView();
        clickListener();
    }

    private void clickListener() {
        top.topMenuLeft.setOnClickListener(this);
        mBinding.radioSelector.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                switch (position){
                    case SEND_ORDER:
                        GlobalData.ORDER_SELECT="put";
                        queryDatas("all");
                        break;
                    case GRAB_ORDER:
                        GlobalData.ORDER_SELECT="receive";
                        queryDatas("all");
                        break;
                    default:
                        break;
                }
            }
        });
        mBinding.swipe.setOnRefreshListener(this);
    }

    private void initView() {
        mBinding.radioSelector.setPosition(SEND_ORDER);
        top=new TopMenuHeader(getWindow().getDecorView());
        top.topMenuTitle.setText("我的订单");
        mBinding.fabMenu.bindAncherView(mBinding.fab);
        mBinding.fabMenu.setOnFABMenuSelectedListener(this);
        mBinding.recyclerOrder.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerOrder.setLayoutManager(new LinearLayoutManager(mContext));
        queryDatas("all");
    }

    private void queryDatas(String status) {
        mBinding.swipe.setRefreshing(true);
        if (mBinding.radioSelector.getPosition()==SEND_ORDER){
            OrderService service=retrofit.create(OrderService.class);
            Call<ArrayList<MyOrderPojo>> call = service.getMyPutOrders(mCache.getAsString("mId"),status);
            call.enqueue(new Callback<ArrayList<MyOrderPojo>>() {
                @Override
                public void onResponse(Call<ArrayList<MyOrderPojo>> call, Response<ArrayList<MyOrderPojo>> response) {
                   ArrayList<MyOrderPojo> list=response.body();
                    MyOderAdapter adapter=new MyOderAdapter(mContext,list);
                    mBinding.recyclerOrder.setAdapter(adapter);
                    mBinding.swipe.setRefreshing(false);

                }

                @Override
                public void onFailure(Call<ArrayList<MyOrderPojo>> call, Throwable t) {
                    CommonUtil.showToast(mContext,"获取数据失败！");
                    mBinding.swipe.setRefreshing(false);
                }
            });
        }else {
            OrderService service=retrofit.create(OrderService.class);
            Call<ArrayList<MyOrderPojo>> call = service.getMyReceiveOrders(mCache.getAsString("mId"),status);
            call.enqueue(new Callback<ArrayList<MyOrderPojo>>() {
                @Override
                public void onResponse(Call<ArrayList<MyOrderPojo>> call, Response<ArrayList<MyOrderPojo>> response) {
                    ArrayList<MyOrderPojo> list=response.body();
                    MyOderAdapter adapter=new MyOderAdapter(mContext,list);
                    mBinding.recyclerOrder.setAdapter(adapter);
                    mBinding.swipe.setRefreshing(false);

                }

                @Override
                public void onFailure(Call<ArrayList<MyOrderPojo>> call, Throwable t) {
                    CommonUtil.showToast(mContext,"获取数据失败！");
                    mBinding.swipe.setRefreshing(false);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.top_menu_left:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onMenuItemSelected(View view) {
        int id = (int) view.getTag();
        switch (id){
            case R.id.item_0:
                Log.e("click0","click0!!!");
                mStatus="0";
                queryDatas(mStatus);
                break;
            case R.id.item_1:
                mStatus="-1";
                queryDatas(mStatus);
                break;
            case R.id.item_2:
                mStatus="2";
                queryDatas(mStatus);
                break;
            case R.id.item_3:
                mStatus="all";
                queryDatas(mStatus);
                break;
        }
    }

    @Override
    public void onRefresh() {
        queryDatas(mStatus);
    }
}
