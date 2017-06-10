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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.activity.grab.PositionActivity;
import com.edu.hrbeu.helpsend.activity.navigate.NavigateActivity;
import com.edu.hrbeu.helpsend.cache.ACache;
import com.edu.hrbeu.helpsend.pojo.GrabDetailResponse;
import com.edu.hrbeu.helpsend.bean.GrabOrder;
import com.edu.hrbeu.helpsend.bean.GrabOrderDetail;
import com.edu.hrbeu.helpsend.pojo.ResponsePojo;
import com.edu.hrbeu.helpsend.seivice.OrderService;
import com.edu.hrbeu.helpsend.utils.CommonUtil;
import com.edu.hrbeu.helpsend.utils.ImgLoadUtil;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.edu.hrbeu.helpsend.seivice.OrderService.retrofit;

public class GrabOrderAdapter extends RecyclerView.Adapter<GrabOrderAdapter.mViewHolder>{


    private Context mContext;
    private ArrayList<GrabOrder>data;
    private final ArrayList<String> sexSelector=new ArrayList<>(Arrays.asList("0","1"));
    private final int[] drawables=new int[]{R.drawable.woman,R.drawable.man};
    private ACache mCache;

    public GrabOrderAdapter(Context mContext, ArrayList<GrabOrder> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public GrabOrderAdapter.mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mViewHolder holder= new mViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_order_all,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(GrabOrderAdapter.mViewHolder holder, int position) {
        GrabOrder order=data.get(position);
        holder.tvName.setText(order.getGoodsCategory());
        holder.tvStart.setText("起点: "+order.getStartLocationPojo().getDescription());
        holder.tvEnd.setText("终点: "+order.getEndLocationPojo().getDescription());
        holder.tvStarTime.setText(order.getSendTime());
        holder.tvEndTime.setText(order.getReceiveTime());
        holder.tvDistance.setText(order.getDistance());
        holder.tvPrice.setText(order.getOrderPrice());
        holder.itemView.setOnClickListener((View v)->{
            OrderService service=retrofit.create(OrderService.class);
            Call<GrabDetailResponse>call=service.getGrabOrderDetail(order.getOrderId());
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
        mCache= ACache.get(mContext);
        final DialogPlus dialog = DialogPlus.newDialog(mContext)
                .setContentHolder(new ViewHolder(R.layout.grab_order_detail))
                .setCancelable(false)
                .setExpanded(true,ViewGroup.LayoutParams.WRAP_CONTENT)
                .setContentHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .create();
        View holder = dialog.getHolderView();
        ImageView ivCancel=(ImageView)holder.findViewById(R.id.detail_cancel);
        TextView tvNickName = (TextView) holder.findViewById(R.id.detail_name);
        ImageView ivAvatar=(ImageView)holder.findViewById(R.id.detail_avatar);
        ImageView ivSex = (ImageView) holder.findViewById(R.id.detail_sex);
        TextView tvStart = (TextView) holder.findViewById(R.id.detail_start);
        TextView tvEnd = (TextView) holder.findViewById(R.id.detail_end);
        TextView tvCategory = (TextView) holder.findViewById(R.id.tv_category);
        TextView tvWeight = (TextView) holder.findViewById(R.id.tv_gravity);
        TextView tvSend = (TextView) holder.findViewById(R.id.detail_send);
        TextView tvReceice = (TextView) holder.findViewById(R.id.detail_receive);
        ImageView ivImg = (ImageView) holder.findViewById(R.id.detail_img);
        LinearLayout selectStart=(LinearLayout)holder.findViewById(R.id.select_start);
        LinearLayout selectEnd=(LinearLayout)holder.findViewById(R.id.select_end);
        tvNickName.setText(detail.getOrderOwnerNickName());
        Button btnGrab=(Button)holder.findViewById(R.id.btn_grab);
        ivSex.setImageDrawable(mContext.getResources().getDrawable(
                drawables[sexSelector.indexOf(detail.getOrderOwnerGender())]));
        tvStart.setText(detail.getStartLocationPojo().getDescription());
        tvEnd.setText(detail.getEndLocationPojo().getDescription());
        tvCategory.setText(detail.getGoodsCategory());
        tvWeight.setText(detail.getGoodsWeight());
        tvSend.setText(detail.getSendTime());
        tvReceice.setText(detail.getReceiveTime());
        ImgLoadUtil.displayCircle(ivAvatar,detail.getOrderOwnerAvatarPath());
        Glide.with(mContext)
                .load(detail.getImagePath())
                .crossFade(500)
                .error(R.drawable.pic_null)
                .into(ivImg);
        dialog.show();

        selectStart.setOnClickListener((View v)->{
            mCache.put("targetLng",detail.getStartLocationPojo().getLongitude());
            mCache.put("targetLat",detail.getStartLocationPojo().getLatitude());
            CommonUtil.startActivity(mContext, PositionActivity.class);
        });
        selectEnd.setOnClickListener((View v)->{
            mCache.put("targetLng",detail.getEndLocationPojo().getLongitude());
            mCache.put("targetLat",detail.getEndLocationPojo().getLatitude());
            CommonUtil.startActivity(mContext, PositionActivity.class);
        });
        ivCancel.setOnClickListener((View v)->{
            dialog.dismiss();
        });
        btnGrab.setOnClickListener((View v)->{
            OrderService service=retrofit.create(OrderService.class);
            Call<ResponsePojo> call = service.grabOrder(detail.getOrderId(),mCache.getAsString("mId"));
            call.enqueue(new Callback<ResponsePojo>() {
                @Override
                public void onResponse(Call<ResponsePojo> call, Response<ResponsePojo> response) {
                    ResponsePojo grabResponse=response.body();
                    CommonUtil.showToast(mContext,grabResponse.getMessage());
                    dialog.dismiss();
                    //跳转到导航
                    Intent intent=new Intent(mContext, NavigateActivity.class);
                    intent.putExtra("startLat",detail.getStartLocationPojo().getLatitude());
                    intent.putExtra("startLng",detail.getStartLocationPojo().getLongitude());
                    intent.putExtra("endLat",detail.getEndLocationPojo().getLatitude());
                    intent.putExtra("endLng",detail.getEndLocationPojo().getLongitude());
                    String detTime=calculateDelTime(detail.getReceiveTime());
                    intent.putExtra("orderId",detail.getOrderId());
                    intent.putExtra("detTime",detTime);
                    intent.putExtra("startPhone",detail.getSenderTel());
                    intent.putExtra("endPhone",detail.getReceiverTel());
                    intent.putExtra("nick",detail.getOrderOwnerNickName());
                    intent.putExtra("avatar",detail.getOrderOwnerAvatarPath());
                    mContext.startActivity(intent);
                }

                @Override
                public void onFailure(Call<ResponsePojo> call, Throwable t) {
                    CommonUtil.showToast(mContext,"抢单失败！");
                    dialog.dismiss();
                }
            });
        });

    }

    private String calculateDelTime(String receiveTime)  {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=null;
        String res=null;
        try {
            date=formatter.parse(receiveTime);
            long currTime=System.currentTimeMillis();
            long receveTime=date.getTime();
            res= String.valueOf(receveTime-currTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder{

        TextView tvName,tvStart,tvEnd,tvStarTime,tvEndTime,tvDistance,tvPrice;

        public mViewHolder(View itemView) {
            super(itemView);
            tvName=(TextView)itemView.findViewById(R.id.tv_name);
            tvStart=(TextView)itemView.findViewById(R.id.tv_start);
            tvEnd=(TextView)itemView.findViewById(R.id.tv_end);
            tvStarTime=(TextView)itemView.findViewById(R.id.tv_start_time);
            tvEndTime=(TextView)itemView.findViewById(R.id.tv_end_time);
            tvDistance=(TextView)itemView.findViewById(R.id.tv_distance);
            tvPrice=(TextView)itemView.findViewById(R.id.tv_price);
        }
    }
}
