package com.edu.hrbeu.helpsend.receiver;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.edu.hrbeu.helpsend.po.XGNotification;
import com.edu.hrbeu.helpsend.seivice.NotificationService;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OrderReceiver extends XGPushBaseReceiver{
    private Intent intent = new Intent("com.edu.hrbeu.helpsend.ORDER_LESTENER");
    public static final String LogTag = "TPushReceiver";
    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {

        Log.e("xgPushRegisterResult",xgPushRegisterResult.toString());
    }

    @Override
    public void onUnregisterResult(Context context, int i) {

    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {

    }

    // 消息透传
    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        String text = "收到消息:" + xgPushTextMessage.toString();
        // 获取自定义key-value
        String customContent = xgPushTextMessage.getCustomContent();
        if (customContent != null && customContent.length() != 0) {
            try {
                JSONObject obj = new JSONObject(customContent);
                // key为前台配置的key
                if (!obj.isNull("key")) {
                    String value = obj.getString("key");
                    Log.d(LogTag, "get custom value:" + value);
                }
                // ...
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // APP自主处理消息的过程...
        Log.e(LogTag, text);
    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {

    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
        if (context == null || xgPushShowedResult == null) {
            return;
        }
        XGNotification notific = new XGNotification();
        notific.setMsg_id(xgPushShowedResult.getMsgId());
        notific.setTitle(xgPushShowedResult.getTitle());
        notific.setContent(xgPushShowedResult.getContent());
        // notificationActionType==1为Activity，2为url，3为intent
        notific.setNotificationActionType(xgPushShowedResult
                .getNotificationActionType());
        // Activity,url,intent都可以通过getActivity()获得
        notific.setActivity(xgPushShowedResult.getActivity());
        notific.setUpdate_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Calendar.getInstance().getTime()));
        NotificationService.getInstance(context).save(notific);
        context.sendBroadcast(intent);
        Log.e("您有1条新消息",xgPushShowedResult.toString());
        Toast.makeText(context,"您有1条新消息, " + "通知被展示 ， " + xgPushShowedResult.toString(),Toast.LENGTH_SHORT).show();
    }
}
