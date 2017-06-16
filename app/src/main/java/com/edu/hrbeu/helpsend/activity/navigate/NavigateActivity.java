package com.edu.hrbeu.helpsend.activity.navigate;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.hrbeu.helpsend.MyApplication;
import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.adapter.TimeLineAdapter;
import com.edu.hrbeu.helpsend.cache.ACache;
import com.edu.hrbeu.helpsend.databinding.ActivityNavigateBinding;
import com.edu.hrbeu.helpsend.global.CustomDialog;
import com.edu.hrbeu.helpsend.pojo.ResponsePojo;
import com.edu.hrbeu.helpsend.seivice.OrderService;
import com.edu.hrbeu.helpsend.utils.CommonUtil;
import com.edu.hrbeu.helpsend.utils.CountDownTimerUtil;
import com.edu.hrbeu.helpsend.utils.DrawableUtil;
import com.edu.hrbeu.helpsend.utils.ImgLoadUtil;
import com.edu.hrbeu.helpsend.utils.LocationOverlay;
import com.edu.hrbeu.helpsend.utils.LocationUtil;
import com.edu.hrbeu.helpsend.utils.TopMenuHeader;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.param.WalkingParam;
import com.tencent.lbssearch.object.result.RoutePlanningObject;
import com.tencent.lbssearch.object.result.WalkingResultObject;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.mapsdk.raster.model.BitmapDescriptor;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.GeoPoint;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.mapsdk.raster.model.Polyline;
import com.tencent.mapsdk.raster.model.PolylineOptions;
import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.TencentMap;
import com.tencent.tencentmap.mapsdk.map.UiSettings;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NavigateActivity extends MapActivity implements View.OnClickListener, TencentLocationListener, TencentMap.OnMarkerClickListener {
    private ActivityNavigateBinding mBinding;
    private Context mContext;
    private ACache mCache;
    private String startLat, startLng, endLat, endLng;
    private LatLng start, end;
    private TencentMap tencentMap;
    private LocationOverlay mLocationOverlay;
    private Marker marker;
    private Bitmap mMarker;
    private TencentLocationManager mLocationManager;
    private String mRequestParams;
    private String provider;
    private TencentLocation mLocation;
    private LatLng latLngLocation;
    private TopMenuHeader top;
    private ImageView ivWheel;
    private DialogPlus dialog;
    private String startPhone, endPhone, avatar, nick;
    private String deltTime;
    private CountDownTimerUtil timer;
    private String orderId;
    private MyApplication myApplication;
    private OrderService orderService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_navigate);
        mContext = this;
        mCache = ACache.get(this);
        Intent intent = getIntent();
        startLat = intent.getStringExtra("startLat");
        startLng = intent.getStringExtra("startLng");
        endLat = intent.getStringExtra("endLat");
        endLng = intent.getStringExtra("endLng");
        startPhone = intent.getStringExtra("startPhone");
        endPhone = intent.getStringExtra("endPhone");
        avatar = intent.getStringExtra("avatar");
        nick = intent.getStringExtra("nick");
        deltTime = intent.getStringExtra("detTime");
        orderId = intent.getStringExtra("orderId");
        counts();
        initView();
        uiSetting();
        clickListener();
        myApplication = MyApplication.create(mContext);
        orderService = myApplication.getOrderService();
        mLocationManager = TencentLocationManager.getInstance(this);
        // 设置坐标系为 gcj-02, 缺省坐标为 gcj-02, 所以通常不必进行如下调用
        mLocationManager.setCoordinateType(TencentLocationManager.COORDINATE_TYPE_GCJ02);
    }

    private void counts() {
        timer = new CountDownTimerUtil(Long.parseLong(deltTime), 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                String timeString = CommonUtil.calculateTime(millisUntilFinished);
                mBinding.top.tvTimeLeft.setText(timeString);
            }

            @Override
            public void onFinish() {
                mBinding.top.tvTimeLeft.setText("配送超时！！");
//                second.setText("载入");
//                timer.start();
            }
        };

        timer.start();
    }

    private void uiSetting() {
        UiSettings uiSettings = mBinding.mapview.getUiSettings();
        //设置logo到屏幕底部中心
        uiSettings.setLogoPosition(UiSettings.LOGO_POSITION_CENTER_BOTTOM);
        //设置比例尺到屏幕右下角
        uiSettings.setScaleViewPosition(UiSettings.SCALEVIEW_POSITION_RIGHT_BOTTOM);
        //启用缩放手势(更多的手势控制请参考开发手册)
        uiSettings.setZoomGesturesEnabled(true);
    }

    private void clickListener() {
        top.topMenuLeft.setOnClickListener(this);
        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                routeDialog.show();
            }
        });
        mBinding.top.btnDelivery.setOnClickListener(this);

    }

    private void initView() {
        start = new LatLng(Double.parseDouble(startLat), Double.parseDouble(startLng));
        end = new LatLng(Double.parseDouble(endLat), Double.parseDouble(endLng));
        top = new TopMenuHeader(getWindow().getDecorView());
        top.topMenuTitle.setText("配送导航");
        tencentMap = mBinding.mapview.getMap();
        tencentMap.setZoom(20);
        mMarker = BitmapFactory.decodeResource(getResources(), R.drawable.my_position);
        mLocationOverlay = new LocationOverlay(mMarker);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(DrawableUtil.zoomDrawable(getResources().getDrawable(R.drawable.nav_start), 120, 120));
        marker = tencentMap.addMarker(new MarkerOptions()
                .position(start)
                .title("起点")
                .icon(icon)
                .draggable(true));
        BitmapDescriptor icon2 = BitmapDescriptorFactory.fromBitmap(DrawableUtil.zoomDrawable(getResources().getDrawable(R.drawable.nav_end), 120, 120));
        marker = tencentMap.addMarker(new MarkerOptions()
                .position(end)
                .title("终点")
                .icon(icon2)
                .draggable(true));

        tencentMap.setOnMarkerClickListener(this);
        routeDialog = DialogPlus.newDialog(mContext)
                .setContentHolder(new ViewHolder(R.layout.recycler_routes))
                .setCancelable(true)
                .setExpanded(true, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setContentHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.BOTTOM)
                .create();
        View holder = routeDialog.getHolderView();
        ImageView imgAvatar = (ImageView) holder.findViewById(R.id.iv_avatar);
        TextView tvNick = (TextView) holder.findViewById(R.id.tv_name);
        ImgLoadUtil.displayCircle(imgAvatar, avatar);
        tvNick.setText(nick);

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocation();
    }


    private void startLocation() {
        Log.e("startLocation", "======startLocation======");
        TencentLocationRequest request = TencentLocationRequest.create();
        // 修改定位请求参数, 周期为 5000 ms
        request.setInterval(5000)
                .setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_NAME)
                .setAllowDirection(true);
        // 开始定位
        int state = mLocationManager.requestLocationUpdates(request, this);
        Log.e("state", String.valueOf(state));
        mRequestParams = request.toString() + ", 坐标系="
                + LocationUtil.toString(mLocationManager.getCoordinateType());

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        } else {
            Toast.makeText(getApplicationContext(), "没有位置提供器可供使用", Toast.LENGTH_LONG).show();
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            LatLng mLocation = new LatLng(Double.parseDouble(mCache.getAsString("mLat")), Double.parseDouble(mCache.getAsString("mLng")));
            //设置地图中心点
            tencentMap.setCenter(mLocation);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(DrawableUtil.zoomDrawable(getResources().getDrawable(R.drawable.my_position), 60, 60));
            marker = tencentMap.addMarker(new MarkerOptions()
                    .position(mLocation)
                    .title("搜索中...")
                    .icon(icon)
                    .draggable(true));

            //  marker.showInfoWindow();
        } else {
            final LatLng latLngLocation = new LatLng(location.getLatitude(), location.getLongitude());
            //设置地图中心点
            tencentMap.setCenter(latLngLocation);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(DrawableUtil.zoomDrawable(getResources().getDrawable(R.drawable.my_position), 60, 60));
            marker = tencentMap.addMarker(new MarkerOptions()
                    .position(latLngLocation)
                    .title("搜索中...")
                    .icon(icon)
                    .draggable(true));
            //marker.showInfoWindow();
        }
        mBinding.ivLoc.setOnClickListener(this);
    }

    private void stopLocation() {
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(TencentLocation location, int error, String reason) {
        if (error == TencentLocation.ERROR_OK) {
            mLocation = location;

            // 定位成功
            StringBuilder sb = new StringBuilder();
            sb.append("定位参数=").append(mRequestParams).append("\n");
            sb.append("(纬度=").append(location.getLatitude()).append(",经度=")
                    .append(location.getLongitude()).append(",精度=")
                    .append(location.getAccuracy()).append("), 来源=")
                    .append(location.getProvider()).append(", 地址=")
                    .append(location.getAddress());
            latLngLocation = new LatLng(location.getLatitude(), location.getLongitude());
            //Log.e("sb", sb.toString());
            // 更新 location 图层
            mLocationOverlay.setAccuracy(mLocation.getAccuracy());
            mLocationOverlay.setGeoCoords(of(mLocation));
            latLngLocation = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            marker.setPosition(latLngLocation);
            marker.setTitle(mLocation.getName());
            // marker.showInfoWindow();
            mBinding.mapview.invalidate();

        }
    }

    private static GeoPoint of(TencentLocation location) {
        GeoPoint ge = new GeoPoint((int) (location.getLatitude() * 1E6),
                (int) (location.getLongitude() * 1E6));
        return ge;
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_loc:
                tencentMap.setCenter(latLngLocation);
                break;
            case R.id.top_menu_left:
                finish();
                break;
            case R.id.btn_delivery:
                CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
                builder.setMessage("已完成配送?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Call<ResponsePojo> call = orderService.deliveryOrder(orderId);
                        call.enqueue(new Callback<ResponsePojo>() {
                            @Override
                            public void onResponse(Call<ResponsePojo> call, Response<ResponsePojo> response) {
                                ResponsePojo responsePojo = response.body();
                                String msg = responsePojo.getMessage();
                                CommonUtil.showToast(mContext, msg);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<ResponsePojo> call, Throwable t) {
                                CommonUtil.showToast(mContext, "提交失败，请重新点击！");
                                dialog.dismiss();
                            }
                        });


                    }
                });
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.e("click", marker.getId());

        dialog = DialogPlus.newDialog(mContext)
                .setContentHolder(new ViewHolder(R.layout.route_dialog))
                .setCancelable(false)
                .setExpanded(true, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setContentHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .create();
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
        ivWheel = (ImageView) dialog.getHolderView().findViewById(R.id.iv_wheel);
        ivWheel.setAnimation(rotate);
        ivWheel.startAnimation(rotate);
        dialog.show();
        TencentSearch tencentSearch = new TencentSearch(this);
        WalkingParam walkingParam = new WalkingParam();
        com.tencent.lbssearch.object.Location locationFrom = new com.tencent.lbssearch.object.Location(
                Float.parseFloat(String.valueOf(latLngLocation.getLatitude())),
                Float.parseFloat(String.valueOf(latLngLocation.getLongitude()))
        );
        com.tencent.lbssearch.object.Location locationTo = new com.tencent.lbssearch.object.Location(
                Float.parseFloat(String.valueOf(marker.getPosition().getLatitude())),
                Float.parseFloat(String.valueOf(marker.getPosition().getLongitude()))
        );
        walkingParam.from(locationFrom);
        walkingParam.to(locationTo);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tencentSearch.getDirection(walkingParam, directionResponseListener);
            }
        }, 1000);
        return true;
    }

    private Polyline polyline;
    private DialogPlus routeDialog;
    HttpResponseListener directionResponseListener = new HttpResponseListener() {
        public List<com.tencent.lbssearch.object.Location> PolylineArr;

        @Override
        public void onSuccess(int i, BaseObject baseObject) {
            if (baseObject == null) {
                return;
            }
            if (polyline != null) {
                polyline.remove();
            }
            WalkingResultObject obj = (WalkingResultObject) baseObject;
            Log.e("res", obj.toString());
            if (obj.result.routes.size() > 0) {
                PolylineArr = obj.result.routes.get(0).polyline;
                List<LatLng> latLngs = new ArrayList<>();
                for (com.tencent.lbssearch.object.Location loc : PolylineArr) {
                    latLngs.add(new LatLng(loc.lat, loc.lng));
                }

                polyline = tencentMap.addPolyline(new PolylineOptions().
                        addAll(latLngs).
                        color(0xff0066cc).
                        width(10f));

                View holder = routeDialog.getHolderView();
                RecyclerView recycle = (RecyclerView) holder.findViewById(R.id.route_list);
                ImageView phone1 = (ImageView) holder.findViewById(R.id.img_phone1);
                ImageView phone2 = (ImageView) holder.findViewById(R.id.img_phone2);
                ImageView msg1 = (ImageView) holder.findViewById(R.id.img_message1);
                ImageView msg2 = (ImageView) holder.findViewById(R.id.img_message2);
                recycle.setItemAnimator(new DefaultItemAnimator());
                recycle.setLayoutManager(new LinearLayoutManager(mContext));
                recycle.setHasFixedSize(true);

                phone1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        call("sender");
                    }
                });

                phone2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        call("receiver");
                    }
                });

                msg1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        senMsg("sender");
                    }
                });
                msg2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        senMsg("receiver");
                    }
                });

                ArrayList<String> routes = new ArrayList<>();
                for (RoutePlanningObject.Step step : obj.result.routes.get(0).steps) {
                    routes.add(step.instruction);
                }
                TimeLineAdapter adapter = new TimeLineAdapter(routes, mContext);
                recycle.setAdapter(adapter);
                ivWheel.clearAnimation();
                dialog.dismiss();


            }


        }

        @Override
        public void onFailure(int i, String s, Throwable throwable) {
            CommonUtil.showToast(mContext, "路线规划失败!");
            ivWheel.clearAnimation();
            dialog.dismiss();
        }
    };

    private void senMsg(String person) {
        Uri uri = null;
        if (person.equals("sender")) {
            uri = Uri.parse("smsto:" + startPhone);
        } else {
            uri = Uri.parse("smsto:" + endPhone);
        }
        Intent intentMessage = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intentMessage);
    }

    private void call(String person) {
        if (person.equals("sender")) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + startPhone));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + endPhone));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(intent);
        }

    }


}
