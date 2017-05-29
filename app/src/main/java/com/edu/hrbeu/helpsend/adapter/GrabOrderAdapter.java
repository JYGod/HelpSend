package com.edu.hrbeu.helpsend.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.bean.GrabOrder;
import com.edu.hrbeu.helpsend.bean.Order;

import java.util.ArrayList;

public class GrabOrderAdapter extends RecyclerView.Adapter<GrabOrderAdapter.mViewHolder>{


    private Context mContext;
    private ArrayList<GrabOrder>data;

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
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder{

        TextView tvName,tvStart,tvEnd,tvStarTime,tvEndTime;

        public mViewHolder(View itemView) {
            super(itemView);
            tvName=(TextView)itemView.findViewById(R.id.tv_name);
            tvStart=(TextView)itemView.findViewById(R.id.tv_start);
            tvEnd=(TextView)itemView.findViewById(R.id.tv_end);
            tvStarTime=(TextView)itemView.findViewById(R.id.tv_start_time);
            tvEndTime=(TextView)itemView.findViewById(R.id.tv_end_time);
        }
    }
}
