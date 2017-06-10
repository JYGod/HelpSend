package com.edu.hrbeu.helpsend.utils;


import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.edu.hrbeu.helpsend.R;

public class ImgLoadUtil {
    private static ImgLoadUtil instance;

    private ImgLoadUtil() {
    }

    public static ImgLoadUtil getInstance() {
        if (instance == null) {
            instance = new ImgLoadUtil();
        }
        return instance;
    }



//--------------------------------------


    public static String drawableToUri(int resId,Context mContext){
        Resources r =mContext.getResources();
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + r.getResourcePackageName(resId) + "/"
                + r.getResourceTypeName(resId) + "/"
                + r.getResourceEntryName(resId));
        String url=uri.getPath();
        return url;
    }



    /**
     * 加载圆角图,暂时用到显示头像
     */
    public static void displayCircle(ImageView imageView, String imageUrl) {
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .crossFade(500)
                .error(R.drawable.pic_null)
                .transform(new GlideCircleTransform(imageView.getContext()))
                .into(imageView);
    }

    public static void displayLoginCircle(ImageView imageView, int resId) {
        Glide.with(imageView.getContext())
                .load(resId)
                .fitCenter()
                .crossFade(500)
                .error(R.drawable.pic_null)
                .transform(new GlideCircleTransform(imageView.getContext()))
                .into(imageView);
    }




}
