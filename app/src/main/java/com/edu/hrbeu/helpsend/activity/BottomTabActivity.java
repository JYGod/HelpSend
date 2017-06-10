package com.edu.hrbeu.helpsend.activity;


import android.Manifest;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.Toast;

import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.activity.grab.GrabActivity;
import com.edu.hrbeu.helpsend.activity.order.MyorderActivity;
import com.edu.hrbeu.helpsend.activity.order.OrderActivity;
import com.edu.hrbeu.helpsend.activity.step.Step1Activity;
import com.edu.hrbeu.helpsend.cache.ACache;
import com.edu.hrbeu.helpsend.databinding.ActivityBottomTabBinding;
import com.edu.hrbeu.helpsend.databinding.NavHeaderMainBinding;
import com.edu.hrbeu.helpsend.global.GlobalData;
import com.edu.hrbeu.helpsend.utils.CommonUtil;
import com.edu.hrbeu.helpsend.utils.ImgLoadUtil;
import com.edu.hrbeu.helpsend.utils.StatusBarUtil;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

public class BottomTabActivity extends TabActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = BottomTabActivity.class.getSimpleName();
    private String tabTag;
    private TabHost mTabHost;
    private Intent orderIntent;
    private Intent grabIntent;
    private Intent messageIntent;
    private ArrayList<String> tags = new ArrayList<>(Arrays.asList("ORDER_TAB", "GRAB_TAB", "MESSAGE_TAB"));
    private ActivityBottomTabBinding mBinding;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private NavHeaderMainBinding bind;
    private final int REQUEST_SELECT_IMG = 11;
    private String provider;
    private ACache mCache;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCache= ACache.get(this);
        mContext=this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
      //  setContentView(R.layout.activity_bottom_tab);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_bottom_tab);
        //  initXGPush();
        initStatusView();
        init();
        setupIntent();
        this.mTabHost.setCurrentTabByTag(tabTag);
        initDrawerLayout();

        initMyLocation();
        //  initTIM();
        clickListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        drawerLayout.closeDrawers();
    }

    private void initMyLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // 获取所有可用的位置提供器
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            //优先使用gps
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        }else {
            Toast.makeText(getApplicationContext(), "没有位置提供器可供使用", Toast.LENGTH_LONG).show();
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location==null){

        }else {
            mCache.put("mLat",String.valueOf(location.getLatitude()));
            mCache.put("mLng",String.valueOf(location.getLongitude()));
            GlobalData.mLocation.setLongitude(String.valueOf(location.getLongitude()));
            GlobalData.mLocation.setLatitude(String.valueOf(location.getLatitude()));
        }

    }

    private void initDrawerLayout() {
        navView.inflateHeaderView(R.layout.nav_header_main);
        View headerView=navView.getHeaderView(0);
        bind=DataBindingUtil.bind(headerView);
//        ImgLoadUtil.displayCircle(bind.ivHead,"http://img3.duitang.com/" +
//                "uploads/item/201409/22/20140922122621_fxvj8.thumb.224_0.jpeg");
        bind.tvUsername.setText(String.valueOf(mCache.getAsString("mNickName")));
        ImgLoadUtil.displayCircle(bind.ivHead, String.valueOf(mCache.getAsString("mAvatar")));
    }

    private void initStatusView() {
        ViewGroup.LayoutParams params=mBinding.include.viewStatus.getLayoutParams();
        params.height= StatusBarUtil.getStatusBarHeight(this);
        mBinding.include.viewStatus.setLayoutParams(params);
    }

    private void init() {
        ArrayList<RadioButton> radios = new ArrayList<RadioButton>(Arrays.asList(mBinding.radioOrder,
                mBinding.radioGrab, mBinding.radioMessage));
        Intent intent = getIntent();
        tabTag = intent.getStringExtra("tabTag");
        if (tabTag==null){
           radios.get(0).setChecked(true);
        }else {
            radios.get(tags.indexOf(tabTag)).setChecked(true);
        }
        orderIntent=new Intent(this,OrderActivity.class);
        grabIntent=new Intent(this,GrabActivity.class);
        messageIntent=new Intent(this,MessageActivity.class);

        drawerLayout=mBinding.drawerLayout;
        navView=mBinding.navView;
    }

    private void setupIntent() {
        mTabHost=getTabHost();
        TabHost localTabHost = this.mTabHost;
        localTabHost.addTab(buildTabSpec(tags.get(0),R.string.order,R.drawable.bottom_icon_order, this.orderIntent));
        localTabHost.addTab(buildTabSpec(tags.get(1),R.string.grab,R.drawable.bottom_icon_grab, this.grabIntent));
        localTabHost.addTab(buildTabSpec(tags.get(2),R.string.message,R.drawable.bottom_icon_message, this.messageIntent));
    }

    private void clickListener() {
        mBinding.radioOrder.setOnCheckedChangeListener(this);
        mBinding.radioGrab.setOnCheckedChangeListener(this);
        mBinding.radioMessage.setOnCheckedChangeListener(this);
        mBinding.include.ivTitleMenu.setOnClickListener(this);
        bind.ivHead.setOnClickListener(this);
        bind.navOrderManage.setOnClickListener(this);
        bind.navApply.setOnClickListener(this);
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b){
            switch (compoundButton.getId()){
                case R.id.radio_order:
                    mTabHost.setCurrentTabByTag("ORDER_TAB");
                    break;
                case R.id.radio_grab:
                    mTabHost.setCurrentTabByTag("GRAB_TAB");
                    break;
                case R.id.radio_message:
                    mTabHost.setCurrentTabByTag("MESSAGE_TAB");
                    break;
                default:
                    break;
            }
        }
    }

    private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
                                         final Intent content) {
        return mTabHost.newTabSpec(tag).setIndicator(getString(resLabel),
                getResources().getDrawable(resIcon)).setContent(content);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_SELECT_IMG:
                if (resultCode==RESULT_OK&&data!=null){
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    File file=new File(picturePath);
                    JMessageClient.updateUserAvatar(file, new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i==0){
                                ImgLoadUtil.displayCircle(bind.ivHead,picturePath);
                            }else {
                                Log.e("??????",s);
                            }
                        }
                    });

                }
                break;
        }
    }


    @Override
    public void onClick(View view) {
        Intent intent=new Intent();
        switch (view.getId()){
            case R.id.iv_title_menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.iv_head:
                intent=new Intent(Intent.ACTION_PICK,  android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,REQUEST_SELECT_IMG);
                break;
            case R.id.nav_order_manage:
                CommonUtil.startActivity(mContext, MyorderActivity.class);
                break;
            case R.id.nav_apply:
                CommonUtil.startActivity(mContext, Step1Activity.class);
                break;
            default:
                break;

        }
    }
}

