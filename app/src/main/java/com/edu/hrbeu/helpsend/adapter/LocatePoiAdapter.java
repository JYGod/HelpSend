package com.edu.hrbeu.helpsend.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.bean.LocatePoi;

import java.util.ArrayList;

public class LocatePoiAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<LocatePoi> data;
    private ViewHolder holder;

    public LocatePoiAdapter(Context mContext, ArrayList<LocatePoi> data) {
        this.mContext = mContext;
        this.data = data;
    }

    public void bindData(ArrayList<LocatePoi> list) {
        this.data = list;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;

        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_nearby, null);
            holder=new ViewHolder();
            holder.tvTitle=(TextView) v.findViewById(R.id.tv_title);
            holder.tvAddress=(TextView) v.findViewById(R.id.tv_address);
            v.setTag(holder);
        }else {
            holder=(ViewHolder)v.getTag();
        }

        if (data!=null){
            LocatePoi newPoi=data.get(i);
            holder.tvTitle.setText(newPoi.getTitle());
            holder.tvAddress.setText(newPoi.getAddress());
        }
        return v;
    }


    class ViewHolder {
        TextView tvTitle, tvAddress;
    }
}
