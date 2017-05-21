package com.edu.hrbeu.helpsend.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.databinding.ActivityOrderBinding;
import com.edu.hrbeu.helpsend.global.GlobalData;
import com.edu.hrbeu.helpsend.http.PhoneUtil;
import com.edu.hrbeu.helpsend.receiver.LocalBroadcastManager;
import com.edu.hrbeu.helpsend.receiver.MyReceiver;
import com.edu.hrbeu.helpsend.utils.TimeUtil;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.scu.miomin.shswiperefresh.core.SHSwipeRefreshLayout;

import net.soulwolf.widget.speedyselector.widget.SelectorTextView;

import java.io.File;

import cn.jpush.android.api.JPushInterface;

import static com.roughike.swipeselector.R.styleable.SwipeSelector;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener, SHSwipeRefreshLayout.SHSOnRefreshListener, OnDateSetListener {

    private static final int SHOW_PAYMENT_DIALOG = 5;
    public final int REQUEST_CONTACTS_SEND=10;
    public final int REQUEST_CONTACTS_RECEIVE=11;
    public final int REQUEST_SELECT_IMG=20;
    private boolean isExpand=false;
    private ActivityOrderBinding mBinding;
    private Context mContext;
    private Activity mActivity;
    private TimePickerDialog mTimeDialog;
    private MyReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_order);
        mBinding.expandableLayout.collapse();
        mContext=this;
        mActivity=this;
        initView();
        clickListener();
        registerMessageReceiver();
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        mBinding.include.tvMyLocation.setText(GlobalData.MY_LOCATE==null?"":GlobalData.MY_LOCATE);
        mBinding.include.tvEndLocation.setText(GlobalData.END_LOCATE==null?"":GlobalData.END_LOCATE);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void initView() {
        View view=View.inflate(getApplicationContext(),R.layout.lay_logo,null);
        View view2=View.inflate(getApplicationContext(),R.layout.lay_logo,null);
        mBinding.swipeRefreshLayout.setHeaderView(view);
        mBinding.swipeRefreshLayout.setFooterView(view2);
        mBinding.include.tvMyLocation.setText(GlobalData.MY_LOCATE==null?"":GlobalData.MY_LOCATE);
        mBinding.include.tvEndLocation.setText(GlobalData.END_LOCATE==null?"":GlobalData.END_LOCATE);
        mBinding.include.tvSenderPhone.setText(GlobalData.SENDER_PHONE==null?"":GlobalData.SENDER_PHONE);
        mBinding.include.tvReceiverPhone.setText(GlobalData.RECEIVER_PHONE==null?"":GlobalData.RECEIVER_PHONE);
        mBinding.include.tvGoods.setText(GlobalData.GOODS_DESCRIPION==null?"":GlobalData.GOODS_DESCRIPION);
        mBinding.include.tvTime.setText(GlobalData.SEND_TIME==null?"":GlobalData.SEND_TIME);
        Glide.with(mContext).load(GlobalData.ACCESSORY)
                .error(R.drawable.pic_null)
                .crossFade(1000)
                .into(mBinding.include.ivAccessory);
    }

    private void clickListener() {
        mBinding.tvPersonality.setOnClickListener(this);
        mBinding.include.itemStart.setOnClickListener(this);
        mBinding.include.itemEnd.setOnClickListener(this);
        mBinding.swipeRefreshLayout.setOnRefreshListener(this);
        mBinding.btnBuy.setOnClickListener(this);
        mBinding.include.btnSelectSender.setOnClickListener(this);
        mBinding.include.btnSelectReceiver.setOnClickListener(this);
        mBinding.include.selectImg.setOnClickListener(this);
        mBinding.include.selecltGoods.setOnClickListener(this);
        mBinding.include.selectTime.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent();
        switch (view.getId()){
            case R.id.tv_personality:
                if (isExpand){
                    mBinding.expandableLayout.collapse();
                    mBinding.tvPersonality.setText("个性化定制");
                    isExpand=false;
                }else {
                    mBinding.expandableLayout.setVisibility(View.VISIBLE);
                    mBinding.tvPersonality.setText("取消个性化定制");
                    mBinding.expandableLayout.expand();
                    isExpand=true;
                }
                break;
            case R.id.item_start:
                intent.setClass(this,LocateActivity.class);
                GlobalData.LOCATE_DIRECTION="start";
                startActivity(intent);
                break;
            case R.id.item_end:
                intent.setClass(this,LocateActivity.class);
                GlobalData.LOCATE_DIRECTION="end";
                startActivity(intent);
                break;
            case R.id.btn_buy:
                mBinding.btnBuy.startAnimation();
                new Handler().postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       mHandler.sendEmptyMessage(SHOW_PAYMENT_DIALOG);
                   }
               },1000);
                break;
            case R.id.btn_select_sender:
                intent=new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent,REQUEST_CONTACTS_SEND);
                break;
            case R.id.btn_select_receiver:
                intent=new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent,REQUEST_CONTACTS_RECEIVE);
                break;
            case R.id.select_img:
                intent=new Intent(Intent.ACTION_PICK,  android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,REQUEST_SELECT_IMG);
                break;
            case R.id.seleclt_goods:
                initDialog();
                break;
            case R.id.select_time:
                initTime();
                break;
            default:
                break;
        }
    }

    private void initTime() {
        mTimeDialog = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("选择送货时间")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setMinMillseconds(System.currentTimeMillis())
                .setCurrentMillseconds(System.currentTimeMillis())
                .setType(Type.ALL)
                .setThemeColor(getResources().getColor(R.color.themeColor))
                .build();
        mTimeDialog.show(getSupportFragmentManager(), "all");

    }

    private void initDialog() {
        final DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.dialog_select_goods))
                .setCancelable(false)
                .setExpanded(true)
                .create();
        dialog.show();
        ImageView cancel= (ImageView) dialog.getHolderView().findViewById(R.id.goods_dialog_cancel);
        cancel.setOnClickListener((View v)->{
            dialog.dismiss();
        });
        SwipeSelector swipeKinds = (com.roughike.swipeselector.SwipeSelector) dialog.getHolderView().findViewById(R.id.swip_kinds);
        SwipeSelector swipeWeight = (com.roughike.swipeselector.SwipeSelector) dialog.getHolderView().findViewById(R.id.swip_weight);
        swipeKinds.setItems(
                new SwipeItem(0, "生活用品", "没有我们不敢送的东西 ^_^"),
                new SwipeItem(1, "食 品", "不怕我们下毒就选吧"),
                new SwipeItem(2, "文 件", "保密资料别来！我们会偷走的~"),
                new SwipeItem(3, "鲜 花", "放心让我们送~ 给你头上一片绿"),
                new SwipeItem(4, "蛋 糕", "这种东西在配送的路上消失在二次元可别怪我们~"),
                new SwipeItem(5, "其 他", "亲，您要送的到底是什么鬼？"));
        swipeWeight.setItems(
                new SwipeItem(0, "500g", "轻如牛毛，送这个东西真是一点都不费力气"),
                new SwipeItem(1, "1kg", "放心，不会被风吹跑的 @_@"),
                new SwipeItem(2, "2kg", "重量刚好，放心一定安全送达~"),
                new SwipeItem(3, "5kg", "并没有超重，还在我们的配送范围内"),
                new SwipeItem(4, "10kg", "有点重，但是我们能扛得动！"),
                new SwipeItem(5, "其 他", "我们可能会搬不动哟~"));
        SelectorTextView btnDialogCheck= (SelectorTextView) dialog.getHolderView().findViewById(R.id.goods_dialog_check);
        btnDialogCheck.setOnClickListener((View v)->{
            String description=swipeKinds.getSelectedItem().title+" - "+swipeWeight.getSelectedItem().title;
            GlobalData.GOODS_DESCRIPION=description;
            mBinding.include.tvGoods.setText(description);
            dialog.dismiss();
        });

    }

    private int BUY_SUCCESS=1;
    Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==BUY_SUCCESS){


            }else if (msg.what==SHOW_PAYMENT_DIALOG){
                mBinding.btnBuy.revertAnimation();
                initPaymentDialog();
            }
        }
    };

    private void initPaymentDialog() {
        final DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.dialog_payment))
                .setCancelable(false)
                .setExpanded(true)
                .create();
        dialog.show();
        ImageView ivCancel=(ImageView)dialog.getHolderView().findViewById(R.id.iv_payment_cancel);
        ivCancel.setOnClickListener((View v)->{
            dialog.dismiss();
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CONTACTS_SEND://发送方电话
                if (resultCode==RESULT_OK){
                    Uri contactData = data.getData();
                    Cursor cursor = managedQuery(contactData, null, null, null, null);
                    String userPhone= PhoneUtil.getPhoneNumber(cursor,mContext);
                    GlobalData.SENDER_PHONE=userPhone;
                    mBinding.include.tvSenderPhone.setText(userPhone);
                }
                break;
            case REQUEST_CONTACTS_RECEIVE://接收方电话
                if (resultCode==RESULT_OK){
                    Uri contactData = data.getData();
                    Cursor cursor = managedQuery(contactData, null, null, null, null);
                    String userPhone= PhoneUtil.getPhoneNumber(cursor,mContext);
                    GlobalData.RECEIVER_PHONE=userPhone;
                    mBinding.include.tvReceiverPhone.setText(userPhone);
                }
                break;
            case REQUEST_SELECT_IMG://物品附件
                if (resultCode==RESULT_OK&&data!=null){
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    File file=new File(picturePath);
                    Glide.with(mContext).load(file)
                            .error(R.drawable.pic_null)
                            .crossFade(1000)
                            .into(mBinding.include.ivAccessory);
                    GlobalData.ACCESSORY=file;
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        mBinding.swipeRefreshLayout.finishRefresh();
    }

    @Override
    public void onLoading() {
        mBinding.swipeRefreshLayout.finishLoadmore();
    }

    @Override
    public void onRefreshPulStateChange(float v, int i) {

    }

    @Override
    public void onLoadmorePullStateChange(float v, int i) {

    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        String time= TimeUtil.getDateToString(millseconds);
        GlobalData.SEND_TIME=time;
        mBinding.include.tvTime.setText(time);
    }


    private void registerMessageReceiver() {
        receiver=new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(GlobalData.ORDER_LISTENER);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }
}
