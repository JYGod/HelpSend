package com.edu.hrbeu.helpsend.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.activity.LoginActivity;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtil {

    /**
     * 吐司-短
     *
     * @param mContext
     * @param msg
     */
    public static void showToast(Context mContext, String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 限制显示文字长度
     *
     * @param text
     * @param content
     */
    public static void setLimitText(TextView text, String content) {
        if (content == null) {
            text.setText("");
        } else {
            if (content.length() < 20) {
                text.setText(content);
            } else {
                text.setText(content.substring(0, 19) + "...");
            }
        }
    }

    public static void startActivityWithFinish(Activity mActivity, Class toClass) {
        Intent intent = new Intent(mActivity, toClass);
        mActivity.startActivity(intent);
        mActivity.finish();

    }

    /**
     * 界面跳转
     *
     * @param mContext
     * @param toClass
     */
    public static void startActivity(Context mContext, Class toClass) {
        Intent intent = new Intent(mContext, toClass);
        mContext.startActivity(intent);

    }


    public static String calculateTime(long del) {
        if (del > 0) {
            long day = del / 1000 / 3600 / 24;
            long hour = del / 1000 / 3600 - day * 24;
            long minute = del / 1000 / 60 - hour * 60 - day * 24 * 60;
            long second = del / 1000 - hour * 3600 - minute * 60 - day * 24 * 3600;
            String res = "";
            if (day == 0 && minute == 0 && hour == 0) {
                res = second + "秒";
            } else if (day == 0 && hour == 0) {
                res = minute + "分钟" + second + "秒";
            } else if (day == 0) {
                res = hour + "小时" + minute + "分钟" + second + "秒";
            } else {
                res = day + "天" + hour + "小时" + minute + "分钟" + second + "秒";
            }
            return res;

        } else {
            return "配送超时！";
        }
    }


    public static DialogPlus createDialog(Context mContext, int res, int gravity, boolean cancelable) {
        DialogPlus dialog = DialogPlus.newDialog(mContext)
                .setContentHolder(new ViewHolder(res))
                .setCancelable(cancelable)
                .setExpanded(true, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setContentHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(gravity)
                .create();
        return dialog;
    }

    public static void packUpSoftKeyboard(Activity mActivity) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && mActivity.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void displayImage(Context mContext, String imageUrl, ImageView imageView) {
        Glide.with(mContext)
                .load(imageUrl)
                .crossFade(500)
                .error(R.drawable.pic_null)
                .into(imageView);
    }
}
