package com.edu.hrbeu.helpsend.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.hrbeu.helpsend.activity.LoginActivity;

public class CommonUtil {

    /**
     * 吐司-短
     * @param mContext
     * @param msg
     */
    public static void showToast(Context mContext,String msg){
        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
    }

    /**
     * 限制显示文字长度
     * @param text
     * @param content
     */
    public static void setLimitText(TextView text,String content){
        if (content==null){
            text.setText("");
        }else {
            if (content.length()<20){
                text.setText(content);
            }else {
                text.setText(content.substring(0,19)+"...");
            }
        }
    }

    public static void startActivityWithFinish(Activity mActivity, Class toClass){
        Intent intent=new Intent(mActivity,toClass);
        mActivity.startActivity(intent);
        mActivity.finish();

    }

    /**
     * 界面跳转
     * @param mContext
     * @param toClass
     */
    public static void startActivity(Context mContext, Class toClass){
        Intent intent=new Intent(mContext,toClass);
        mContext.startActivity(intent);

    }


}
