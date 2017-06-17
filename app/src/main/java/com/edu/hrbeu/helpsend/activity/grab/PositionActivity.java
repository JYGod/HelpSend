package com.edu.hrbeu.helpsend.activity.grab;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.cache.ACache;
import com.edu.hrbeu.helpsend.databinding.ActivityPositionBinding;
import com.edu.hrbeu.helpsend.utils.DrawableUtil;
import com.edu.hrbeu.helpsend.utils.LocationOverlay;
import com.edu.hrbeu.helpsend.utils.LocationUtil;
import com.edu.hrbeu.helpsend.utils.TopMenuHeader;
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
import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.TencentMap;
import com.tencent.tencentmap.mapsdk.map.UiSettings;

import java.util.List;


public class PositionActivity extends MapActivity implements TencentLocationListener, View.OnClickListener {
    private ActivityPositionBinding mBinding;
    private Context mContext;
    private TopMenuHeader top;
    private TencentMap tencentMap;
    private TencentLocationManager mLocationManager;
    private String mRequestParams;
    private String provider;
    private Marker marker;
    private TencentLocation mLocation;
    private LatLng latLngLocation;
    private LocationOverlay mLocationOverlay;
    private Bitmap bmpMarker;
    private ACache mCache;
    private String mLng, mLat;
    private LatLng target;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_position);
        mContext = this;
        mCache = ACache.get(this);
        mLng = mCache.getAsString("targetLng");
        mLat = mCache.getAsString("targetLat");
        Log.e("传递经纬度：", "经度:" + mLng + "-----纬度:" + mLat);
        target = new LatLng(Double.parseDouble(mLat), Double.parseDouble(mLng));
        initView();
        uiSetting();
        clickListener();
        mLocationManager = TencentLocationManager.getInstance(this);
        // 设置坐标系为 gcj-02, 缺省坐标为 gcj-02, 所以通常不必进行如下调用
        mLocationManager.setCoordinateType(TencentLocationManager.COORDINATE_TYPE_GCJ02);
    }

    private void clickListener() {
        top.topMenuLeft.setOnClickListener(this);
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

    private void uiSetting() {
        UiSettings uiSettings = mBinding.mapview.getUiSettings();
        //设置logo到屏幕底部中心
        uiSettings.setLogoPosition(UiSettings.LOGO_POSITION_CENTER_BOTTOM);
        //设置比例尺到屏幕右下角
        uiSettings.setScaleViewPosition(UiSettings.SCALEVIEW_POSITION_RIGHT_BOTTOM);
        //启用缩放手势(更多的手势控制请参考开发手册)
        uiSettings.setZoomGesturesEnabled(true);
    }

    private void initView() {
        top = new TopMenuHeader(getWindow().getDecorView());
        top.topMenuTitle.setText("详细位置信息");
        tencentMap = mBinding.mapview.getMap();
        tencentMap.setZoom(20);
        bmpMarker = BitmapFactory.decodeResource(getResources(), R.drawable.my_position);
        mLocationOverlay = new LocationOverlay(bmpMarker);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(DrawableUtil.zoomDrawable(getResources().getDrawable(R.drawable.map_locate), 90, 90));
        marker = tencentMap.addMarker(new MarkerOptions()
                .position(target)
                .title("目标点")
                .icon(icon)
                .draggable(true));
        marker.showInfoWindow();

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
            marker.showInfoWindow();
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
            marker.showInfoWindow();
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
            //  marker.showInfoWindow();
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
        }
    }
}
