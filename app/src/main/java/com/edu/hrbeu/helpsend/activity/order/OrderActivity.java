package com.edu.hrbeu.helpsend.activity.order;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edu.hrbeu.helpsend.MyApplication;
import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.bean.Order;
import com.edu.hrbeu.helpsend.bean.UpdateInfo;
import com.edu.hrbeu.helpsend.cache.ACache;
import com.edu.hrbeu.helpsend.databinding.ActivityOrderBinding;
import com.edu.hrbeu.helpsend.global.GlobalData;
import com.edu.hrbeu.helpsend.http.PhoneUtil;
import com.edu.hrbeu.helpsend.pojo.PositionPojo;
import com.edu.hrbeu.helpsend.pojo.ResponsePojo;
import com.edu.hrbeu.helpsend.receiver.LocalBroadcastManager;
import com.edu.hrbeu.helpsend.receiver.MyReceiver;
import com.edu.hrbeu.helpsend.seivice.OrderService;
import com.edu.hrbeu.helpsend.utils.CommonUtil;
import com.edu.hrbeu.helpsend.utils.PerfectClickListener;
import com.edu.hrbeu.helpsend.utils.TimeUtil;
import com.google.gson.Gson;
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

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import cn.pedant.SweetAlert.SweetAlertDialog;
import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderActivity extends AppCompatActivity implements View.OnClickListener, SHSwipeRefreshLayout.SHSOnRefreshListener, OnDateSetListener {

    private static final int SHOW_PAYMENT_DIALOG = 5;
    public final int REQUEST_CONTACTS_SEND = 10;
    public final int REQUEST_CONTACTS_RECEIVE = 11;
    public final int REQUEST_SELECT_IMG = 20;
    private ActivityOrderBinding mBinding;
    private Context mContext;
    private Activity mActivity;
    private TimePickerDialog mTimeDialog;
    private MyReceiver receiver;
    private String timeStyle = "";
    private int currentIndex = 0;//选择的支付方式
    private final int[] payment_pic = new int[]{R.drawable.zhifu_small, R.drawable.weixin_small, R.drawable.pocket_small};
    private ACache mCache;
    private Gson gson = new Gson();
    private MyApplication myApplication;
    private OrderService orderService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCache = ACache.get(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_order);
        mContext = this;
        mActivity = this;
        myApplication = MyApplication.create(mContext);
        orderService = myApplication.getOrderService();
        initView();
        clickListener();
        registerMessageReceiver();
    }


    /**
     * 界面重载
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        mBinding.include.tvMyLocation.setText(GlobalData.MY_ORDER.getStartLocation() == null ?
                "" : GlobalData.MY_ORDER.getStartLocation().getDescription());
        mBinding.include.tvEndLocation.setText(GlobalData.MY_ORDER.getEndLocation() == null ?
                "" : GlobalData.MY_ORDER.getEndLocation().getDescription());
        mBinding.include.tvSenderPhone.setText(GlobalData.MY_ORDER.getSenderTel() == null ? "" : GlobalData.MY_ORDER.getSenderTel());
        mBinding.include.tvReceiverPhone.setText(GlobalData.MY_ORDER.getReceiverTel() == null ? "" : GlobalData.MY_ORDER.getReceiverTel());
        mBinding.include.tvGoods.setText(GlobalData.MY_ORDER.getGoodsName() == null ? "" : GlobalData.MY_ORDER.getGoodsName());
        mBinding.include.tvTime.setText(GlobalData.MY_ORDER.getSendTime() == null ? "" : GlobalData.MY_ORDER.getSendTime());
        mBinding.include.tvReceiveTime.setText(GlobalData.MY_ORDER.getReceiveTime() == null ? "" : GlobalData.MY_ORDER.getReceiveTime());
        Glide.with(mContext).load(GlobalData.ACCESSORY)
                .error(R.drawable.pic_null)
                .crossFade(1000)
                .into(mBinding.include.ivAccessory);
        CommonUtil.setLimitText(mBinding.include.tvRemark, GlobalData.MY_ORDER.getRemark());
        calculatePrice();
        initPayingAnim();


    }

    /**
     * 初始化支付动画
     */
    private void initPayingAnim() {
        mBinding.paying.payingLoading.addBitmap(payment_pic[0]);
        mBinding.paying.payingLoading.addBitmap(payment_pic[1]);
        mBinding.paying.payingLoading.addBitmap(payment_pic[2]);
        mBinding.paying.payingLoading.setShadowColor(R.color.bottomTextColoer);
    }

    /**
     * 动态加算价格
     */
    private void calculatePrice() {
        mBinding.tvPrice.setText("计算中...");
        String reqStr = gson.toJson(GlobalData.POSITION_POINTS);
        Call<ResponsePojo> call = orderService.getPrice(reqStr);
        call.enqueue(new Callback<ResponsePojo>() {
            @Override
            public void onResponse(Call<ResponsePojo> call, Response<ResponsePojo> response) {
                ResponsePojo responsePojo = response.body();
                mBinding.tvPrice.setText(responsePojo.getStatus());
                GlobalData.MY_ORDER.setOrderPrice(responsePojo.getStatus());
                mBinding.btnPriceDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogPlus dialog = CommonUtil.createDialog(mContext, R.layout.dialog_price_detail, Gravity.BOTTOM, true);
                        TextView detail = (TextView) dialog.getHolderView().findViewById(R.id.tv_price_detail);
                        detail.setText(responsePojo.getMessage());
                        if (!responsePojo.getMessage().trim().equals("")) {
                            dialog.show();
                        }

                    }
                });
            }

            @Override
            public void onFailure(Call<ResponsePojo> call, Throwable t) {
                CommonUtil.showToast(mContext, "价格获取失败！");
            }
        });
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void initView() {
        View view = View.inflate(getApplicationContext(), R.layout.lay_logo, null);
        View view2 = View.inflate(getApplicationContext(), R.layout.lay_logo, null);
        mBinding.swipeRefreshLayout.setHeaderView(view);
        mBinding.swipeRefreshLayout.setFooterView(view2);
        mBinding.include.tvMyLocation.setText(GlobalData.MY_ORDER.getStartLocation() == null ?
                "" : GlobalData.MY_ORDER.getStartLocation().getDescription());
        mBinding.include.tvEndLocation.setText(GlobalData.MY_ORDER.getEndLocation() == null ?
                "" : GlobalData.MY_ORDER.getEndLocation().getDescription());
        mBinding.include.tvSenderPhone.setText(GlobalData.MY_ORDER.getSenderTel() == null ? "" : GlobalData.MY_ORDER.getSenderTel());
        mBinding.include.tvReceiverPhone.setText(GlobalData.MY_ORDER.getReceiverTel() == null ? "" : GlobalData.MY_ORDER.getReceiverTel());
        mBinding.include.tvGoods.setText(GlobalData.MY_ORDER.getGoodsName() == null ? "" : GlobalData.MY_ORDER.getGoodsName());
        mBinding.include.tvTime.setText(GlobalData.MY_ORDER.getSendTime() == null ? "" : GlobalData.MY_ORDER.getSendTime());
        mBinding.include.tvReceiveTime.setText(GlobalData.MY_ORDER.getReceiveTime() == null ? "" : GlobalData.MY_ORDER.getReceiveTime());
        CommonUtil.setLimitText(mBinding.include.tvRemark, GlobalData.MY_ORDER.getRemark());
        Glide.with(mContext).load(GlobalData.ACCESSORY)
                .error(R.drawable.pic_null)
                .crossFade(1000)
                .into(mBinding.include.ivAccessory);

    }

    private void clickListener() {
        // mBinding.tvPersonality.setOnClickListener(this);
        mBinding.include.itemStart.setOnClickListener(this);
        mBinding.include.itemEnd.setOnClickListener(this);
        mBinding.swipeRefreshLayout.setOnRefreshListener(this);
        mBinding.btnBuy.setOnClickListener(this);
        mBinding.include.btnSelectSender.setOnClickListener(this);
        mBinding.include.btnSelectReceiver.setOnClickListener(this);
        mBinding.include.selectImg.setOnClickListener(this);
        mBinding.include.selecltGoods.setOnClickListener(this);
        mBinding.include.selectTime.setOnClickListener(this);
        mBinding.include.selectReceiveTime.setOnClickListener(this);
        mBinding.include.selectRemark.setOnClickListener(this);
        mBinding.include.tvSenderPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                GlobalData.MY_ORDER.setSenderTel(mBinding.include.tvSenderPhone.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mBinding.include.tvReceiverPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                GlobalData.MY_ORDER.setReceiverTel(mBinding.include.tvReceiverPhone.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.item_start:
                intent.setClass(this, LocateActivity.class);
                GlobalData.LOCATE_DIRECTION = "start";
                startActivity(intent);
                break;
            case R.id.item_end:
                intent.setClass(this, LocateActivity.class);
                GlobalData.LOCATE_DIRECTION = "end";
                startActivity(intent);
                break;
            case R.id.btn_buy:
                mBinding.btnBuy.startAnimation();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(SHOW_PAYMENT_DIALOG);
                    }
                }, 1000);
                break;
            case R.id.btn_select_sender:
                intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CONTACTS_SEND);
                break;
            case R.id.btn_select_receiver:
                intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CONTACTS_RECEIVE);
                break;
            case R.id.select_img:
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_SELECT_IMG);
                break;
            case R.id.seleclt_goods:
                initDialog();
                break;
            case R.id.select_time:
                timeStyle = "send";
                initTime();
                break;
            case R.id.select_receive_time:
                timeStyle = "receive";
                initTime();
                break;
            case R.id.select_remark:
                intent.setClass(mContext, RemarkActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 时间选择对话框
     */
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

    /**
     * 物品选择对话框
     */
    private void initDialog() {
        DialogPlus dialog = CommonUtil.createDialog(mContext, R.layout.dialog_select_goods, Gravity.BOTTOM, false);
        dialog.show();
        ImageView cancel = (ImageView) dialog.getHolderView().findViewById(R.id.goods_dialog_cancel);
        cancel.setOnClickListener((View v) -> {
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
        SelectorTextView btnDialogCheck = (SelectorTextView) dialog.getHolderView().findViewById(R.id.goods_dialog_check);
        btnDialogCheck.setOnClickListener((View v) -> {
            String description = swipeKinds.getSelectedItem().title + " - " + swipeWeight.getSelectedItem().title;
            GlobalData.MY_ORDER.setGoodsName(description);
            mBinding.include.tvGoods.setText(description);
            dialog.dismiss();
        });

    }

    private int BUY_SUCCESS = 1;
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == BUY_SUCCESS) {

            } else if (msg.what == SHOW_PAYMENT_DIALOG) {
                if (GlobalData.MY_ORDER.getStartLocation() == null || GlobalData.MY_ORDER.getEndLocation() == null
                        || GlobalData.MY_ORDER.getSenderTel() == null || GlobalData.MY_ORDER.getReceiverTel() == null
                        || GlobalData.MY_ORDER.getReceiveTime() == null || GlobalData.MY_ORDER.getSendTime() == null) {
                    CommonUtil.showToast(mContext, "请完善订单信息！");
                    mBinding.btnBuy.revertAnimation();
                } else {
                    if (GlobalData.MY_ORDER.getGoodsName() == null) {
                        GlobalData.MY_ORDER.setGoodsName("其他 - 其他");
                    }
                    mBinding.btnBuy.revertAnimation();
                    initPaymentDialog();
                }
            }
        }
    };

    /**
     * 支付对话框
     */
    private void initPaymentDialog() {
        final DialogPlus dialog = CommonUtil.createDialog(mContext, R.layout.dialog_payment, Gravity.BOTTOM, false);
        TextView tvPrice = (TextView) dialog.findViewById(R.id.tv_price);
        tvPrice.setText(GlobalData.MY_ORDER.getOrderPrice());
        dialog.show();
        ImageView ivCancel = (ImageView) dialog.getHolderView().findViewById(R.id.iv_payment_cancel);
        ivCancel.setOnClickListener((View v) -> {
            dialog.dismiss();
        });
        RadioRealButtonGroup group = (RadioRealButtonGroup) dialog.getHolderView().findViewById(R.id.radio_group);
        group.setOnPositionChangedListener(new RadioRealButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(RadioRealButton button, int currentPosition, int lastPosition) {
                currentIndex = currentPosition;
            }
        });
        group.setPosition(currentIndex);
        CircularProgressButton btnPay = (CircularProgressButton) dialog.getHolderView().findViewById(R.id.btn_pay);
        btnPay.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                dialog.dismiss();
                GlobalData.MY_ORDER.setOrderOwnerId(mCache.getAsString("mId"));
                String orderinfo = gson.toJson(GlobalData.MY_ORDER, Order.class);
                mBinding.paying.payingLoading.start();
                mBinding.paying.payingLoading.setVisibility(View.VISIBLE);
                MultipartBody.Part photo = null;
                if (GlobalData.ACCESSORY == null) {

                    Call<UpdateInfo> call = orderService.submitOrderWithoutAc(orderinfo);
                    call.enqueue(new Callback<UpdateInfo>() {
                        @Override
                        public void onResponse(Call<UpdateInfo> call, Response<UpdateInfo> response) {
                            GlobalData.MY_ORDER = new Order();
                            GlobalData.ACCESSORY = null;
                            mBinding.paying.payingLoading.stop();
                            mBinding.paying.payingLoading.setVisibility(View.GONE);
                            UpdateInfo updateInfo = response.body();
                            CommonUtil.showToast(mContext, updateInfo.getMessage());
                            GlobalData.MY_ORDER = new Order();
                            GlobalData.POSITION_POINTS = new PositionPojo(new Order.Location(), new Order.Location());
                            CommonUtil.startActivity(mContext, MyorderActivity.class);
                        }

                        @Override
                        public void onFailure(Call<UpdateInfo> call, Throwable t) {
                            mBinding.paying.payingLoading.stop();
                            mBinding.paying.payingLoading.setVisibility(View.GONE);
                            CommonUtil.showToast(mContext, "支付失败!");
                            dialog.dismiss();
                        }
                    });
                } else {
                    RequestBody photoRequestBody = RequestBody.create(MediaType.parse("image/png"), GlobalData.ACCESSORY);
                    photo = MultipartBody.Part.createFormData("photos", "accessory.png", photoRequestBody);
                    Call<UpdateInfo> call = orderService.submitOrder(photo, RequestBody.create(null, orderinfo));
                    call.enqueue(new Callback<UpdateInfo>() {
                        @Override
                        public void onResponse(Call<UpdateInfo> call, Response<UpdateInfo> response) {
                            GlobalData.MY_ORDER = new Order();
                            GlobalData.ACCESSORY = null;
                            mBinding.paying.payingLoading.stop();
                            mBinding.paying.payingLoading.setVisibility(View.GONE);
                            UpdateInfo updateInfo = response.body();
                            CommonUtil.showToast(mContext, updateInfo.getMessage());
                            GlobalData.MY_ORDER = new Order();
                            GlobalData.POSITION_POINTS = new PositionPojo(new Order.Location(), new Order.Location());
                            CommonUtil.startActivity(mContext, MyorderActivity.class);
                        }

                        @Override
                        public void onFailure(Call<UpdateInfo> call, Throwable t) {
                            mBinding.paying.payingLoading.stop();
                            mBinding.paying.payingLoading.setVisibility(View.GONE);
                            CommonUtil.showToast(mContext, "支付失败!");
                            dialog.dismiss();
                        }
                    });
                }

            }
        });
    }

    /**
     * 取消订单  对话框
     *
     * @param v
     */
    private void cancelOrderDialog(View v) {
        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("取消订单？")
                .setContentText("取消订单后帮带员将搜索不到您的订单信息")
                .setConfirmText("确认")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.setTitleText("订单已取消")
                                .setContentText("下次想好了再发布您的需求~")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        mBinding.waitingDialog.content.stopRippleAnimation();
                                        mBinding.waitingDialog.waitDialog.setVisibility(View.GONE);
                                        sweetAlertDialog.dismiss();
                                    }
                                })
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                    }
                                })
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                })
                .setCancelText("取消")
                .setCancelClickListener(null)
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONTACTS_SEND://发送方电话
                if (resultCode == RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cursor = managedQuery(contactData, null, null, null, null);
                    String userPhone = PhoneUtil.getPhoneNumber(cursor, mContext);
                    GlobalData.MY_ORDER.setSenderTel(userPhone);
                    mBinding.include.tvSenderPhone.setText(userPhone);
                }
                break;
            case REQUEST_CONTACTS_RECEIVE://接收方电话
                if (resultCode == RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cursor = managedQuery(contactData, null, null, null, null);
                    String userPhone = PhoneUtil.getPhoneNumber(cursor, mContext);
                    GlobalData.MY_ORDER.setReceiverTel(userPhone);
                    mBinding.include.tvReceiverPhone.setText(userPhone);
                }
                break;
            case REQUEST_SELECT_IMG://物品附件
                if (resultCode == RESULT_OK && data != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    File file = new File(picturePath);
                    Glide.with(mContext).load(file)
                            .error(R.drawable.pic_null)
                            .crossFade(1000)
                            .into(mBinding.include.ivAccessory);
                    GlobalData.ACCESSORY = file;
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
        String time = TimeUtil.getDateToString(millseconds);
        if (timeStyle.equals("send")) {
            mBinding.include.tvTime.setText(time);
            GlobalData.MY_ORDER.setSendTime(time);
        } else {
            mBinding.include.tvReceiveTime.setText(time);
            GlobalData.MY_ORDER.setReceiveTime(time);
        }
    }


    private void registerMessageReceiver() {
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(GlobalData.ORDER_LISTENER);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }
}
