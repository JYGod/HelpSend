package com.edu.hrbeu.helpsend.utils;


import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

public class CommonUtil {

    public static void showToast(Context mContext,String msg){
        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
    }

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
}
