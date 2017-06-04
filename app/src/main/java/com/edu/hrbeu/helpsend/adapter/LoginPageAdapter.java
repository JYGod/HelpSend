package com.edu.hrbeu.helpsend.adapter;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class LoginPageAdapter extends PagerAdapter{
    private Context mContext;
    private ArrayList<View> mViewList;

    public LoginPageAdapter(Context mContext, ArrayList<View> mViewList) {
        this.mContext = mContext;
        this.mViewList = mViewList;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final View container, final int position, final Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }
}
