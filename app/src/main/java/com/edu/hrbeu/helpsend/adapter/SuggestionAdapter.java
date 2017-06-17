package com.edu.hrbeu.helpsend.adapter;


import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.bean.Order;
import com.edu.hrbeu.helpsend.global.GlobalData;
import com.edu.hrbeu.helpsend.utils.CommonUtil;
import com.tencent.lbssearch.object.result.SuggestionResultObject;

import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.mViewHolder> {

    private Activity mActivity;
    private int loc;
    private Context mContext;
    private List<SuggestionResultObject.SuggestionData> dataList;

    public SuggestionAdapter(Context mContext, List<SuggestionResultObject.SuggestionData> data, int loc, Activity mActivity) {
        this.mContext = mContext;
        this.dataList = data;
        this.loc = loc;
        this.mActivity = mActivity;
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
        holder.itemView.setOnClickListener((View v) -> {
            Order.Location locationTemp = new Order.Location();
            locationTemp.setLatitude(String.valueOf(data.location.lat));
            locationTemp.setLongitude(String.valueOf(data.location.lng));
            locationTemp.setDescription(data.title);
            if (loc == 0) { // start
                GlobalData.MY_ORDER.setStartLocation(locationTemp);
                GlobalData.POSITION_POINTS.setStart(locationTemp);
            } else {
                GlobalData.MY_ORDER.setEndLocation(locationTemp);
                GlobalData.POSITION_POINTS.setEnd(locationTemp);
            }
            CommonUtil.packUpSoftKeyboard(mActivity);
            mActivity.finish();
        });
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
