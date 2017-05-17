package com.edu.hrbeu.helpsend.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.edu.hrbeu.helpsend.R;
import com.youth.banner.loader.ImageLoader;

/**
 * 首页轮播图
 */

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object url, ImageView imageView) {
        Glide.with(context).load(url)
                .placeholder(R.drawable.pic_null)
                .error(R.drawable.pic_null)
                .crossFade(1000)
                .into(imageView);
    }
}
