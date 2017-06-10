package com.edu.hrbeu.helpsend.adapter;


import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edu.hrbeu.helpsend.R;


import java.util.ArrayList;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.TimeLineViewHolder>{

    private ArrayList<String> mList;
    private Context mContext;

    public TimeLineAdapter(ArrayList<String> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        TimeLineViewHolder holder= new TimeLineViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_timeline,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {

       String strMessage = mList.get(position);
        holder.tvMessage.setText(strMessage);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class TimeLineViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView tvMessage;

        public TimeLineViewHolder(View itemView) {
            super(itemView);
            tvMessage=(AppCompatTextView)itemView.findViewById(R.id.item_text);
        }
    }
}
