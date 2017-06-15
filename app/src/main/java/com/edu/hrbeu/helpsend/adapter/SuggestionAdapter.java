package com.edu.hrbeu.helpsend.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edu.hrbeu.helpsend.R;
import com.tencent.lbssearch.object.result.SuggestionResultObject;

import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.mViewHolder> {

    private Context mContext;
    private List<SuggestionResultObject.SuggestionData> dataList;

    public SuggestionAdapter(Context mContext, List<SuggestionResultObject.SuggestionData> data) {
        this.mContext = mContext;
        this.dataList = data;
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mViewHolder holder = new mViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_suggestion, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, int position) {
        SuggestionResultObject.SuggestionData data = dataList.get(position);
        holder.itemTitle.setText(data.title);
        holder.itemAddress.setText(data.address);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {

        TextView itemTitle, itemAddress;

        public mViewHolder(View itemView) {
            super(itemView);

            itemTitle = (TextView) itemView.findViewById(R.id.item_title);
            itemAddress = (TextView) itemView.findViewById(R.id.item_address);
        }
    }
}
