package com.edu.hrbeu.helpsend.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.activity.navigate.NavigateActivity;
import com.edu.hrbeu.helpsend.activity.order.TimelineActivty;
import com.edu.hrbeu.helpsend.bean.GrabOrderDetail;
import com.edu.hrbeu.helpsend.global.GlobalData;
import com.edu.hrbeu.helpsend.pojo.GrabDetailResponse;
import com.edu.hrbeu.helpsend.pojo.MyOrderPojo;
import com.edu.hrbeu.helpsend.seivice.OrderService;
import com.edu.hrbeu.helpsend.utils.CommonUtil;
import com.edu.hrbeu.helpsend.utils.LabelView;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.edu.hrbeu.helpsend.seivice.OrderService.retrofit;


public class MyOderAdapter extends RecyclerView.Adapter<MyOderAdapter.mViewHolder>{
    private Context mContext;
    private ArrayList<MyOrderPojo> data;

    public MyOderAdapter(Context mContext, ArrayList<MyOrderPojo> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mViewHolder holder= new mViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_my_order,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, int position) {
        MyOrderPojo order=data.get(position);
        holder.tvId.setText(order.getOrderId());
        holder.tvName.setText(order.getGoodsName());
        holder.tvTime.setText(order.getOrderTime());
        if (!order.getOrderStatus().equals("0")){
            holder.tvCancel.setVisibility(View.GONE);
        }
        holder.tvCancel.setOnClickListener((View v)->{
            //撤单

        });

        switch (Integer.parseInt(order.getOrderStatus())){
            case 1:
                holder.label.setBgColor(R.color.green);
                holder.label.setText("进行中");
                break;
            case -1:
                holder.label.setBgColor(R.color.bottomTextColoer);
                holder.label.setText("已撤单");
                break;
            case 2:
                holder.label.setBgColor(R.color.themeColor);
                holder.label.setText("已完成");
                break;
            default:
                break;
        }

        holder.itemView.setOnClickListener((View v)->{
            OrderService service=retrofit.create(OrderService.class);
            Call<GrabDetailResponse> call=service.getGrabOrderDetail(order.getOrderId());
            call.enqueue(new Callback<GrabDetailResponse>() {
                @Override
                public void onResponse(Call<GrabDetailResponse> call, Response<GrabDetailResponse> response) {
                    GrabDetailResponse grabDetailResponse=response.body();
                    GrabOrderDetail detail=grabDetailResponse.getMessage();
                    showOrderDetail(detail);
                }

                @Override
                public void onFailure(Call<GrabDetailResponse> call, Throwable t) {
                    CommonUtil.showToast(mContext,"获取信息失败！");
                }
            });
        });

    }

    private void showOrderDetail(GrabOrderDetail detail) {
        final DialogPlus dialog = DialogPlus.newDialog(mContext)
                .setContentHolder(new ViewHolder(R.layout.my_order_detail))
                .setCancelable(false)
                .setExpanded(true,ViewGroup.LayoutParams.WRAP_CONTENT)
                .setContentHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .create();
        View holder = dialog.getHolderView();
        ImageView ivCancel=(ImageView)holder.findViewById(R.id.detail_cancel);
        TextView tvStart = (TextView) holder.findViewById(R.id.detail_start);
        TextView tvEnd = (TextView) holder.findViewById(R.id.detail_end);
        TextView tvCategory = (TextView) holder.findViewById(R.id.tv_category);
        TextView tvWeight = (TextView) holder.findViewById(R.id.tv_gravity);
        TextView tvSend = (TextView) holder.findViewById(R.id.detail_send);
        TextView tvReceice = (TextView) holder.findViewById(R.id.detail_receive);
        ImageView ivImg = (ImageView) holder.findViewById(R.id.detail_img);
        Button btnGrab=(Button)holder.findViewById(R.id.btn_grab);
        if (GlobalData.ORDER_SELECT.equals("receive")){
            btnGrab.setText("查看配送路线");
        }
        tvStart.setText(detail.getStartLocationPojo().getDescription());
        tvEnd.setText(detail.getEndLocationPojo().getDescription());
        tvCategory.setText(detail.getGoodsCategory());
        tvWeight.setText(detail.getGoodsWeight());
        tvSend.setText(detail.getSendTime());
        tvReceice.setText(detail.getReceiveTime());
        Glide.with(mContext)
                .load(detail.getImagePath())
                .crossFade(500)
                .error(R.drawable.pic_null)
                .into(ivImg);
        dialog.show();
        ivCancel.setOnClickListener((View v)->{
            dialog.dismiss();
        });
        btnGrab.setOnClickListener((View v)->{
            if (GlobalData.ORDER_SELECT.equals("receive")){

                Intent intent=new Intent(mContext, NavigateActivity.class);
                intent.putExtra("startLat",detail.getStartLocationPojo().getLatitude());
                intent.putExtra("startLng",detail.getStartLocationPojo().getLongitude());
                intent.putExtra("endLat",detail.getEndLocationPojo().getLatitude());
                intent.putExtra("endLng",detail.getEndLocationPojo().getLongitude());
                mContext.startActivity(intent);
            }else {
                Intent intent=new Intent(mContext, TimelineActivty.class);
                intent.putExtra("orderId",detail.getOrderId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class mViewHolder extends RecyclerView.ViewHolder{

        TextView tvName,tvTime,tvId,tvCancel;
        LabelView label;

        public mViewHolder(View itemView) {
            super(itemView);
            label= (LabelView) itemView.findViewById(R.id.label);
            tvId=(TextView)itemView.findViewById(R.id.tv_id);
            tvName=(TextView)itemView.findViewById(R.id.tv_name);
            tvTime=(TextView)itemView.findViewById(R.id.tv_time);
            tvCancel=(TextView)itemView.findViewById(R.id.tv_cancel_order);
        }
    }
}
