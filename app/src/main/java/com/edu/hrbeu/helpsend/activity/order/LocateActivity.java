package com.edu.hrbeu.helpsend.activity.order;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.adapter.LocatePoiAdapter;
import com.edu.hrbeu.helpsend.adapter.SuggestionAdapter;
import com.edu.hrbeu.helpsend.bean.LocatePoi;
import com.edu.hrbeu.helpsend.bean.Order;
import com.edu.hrbeu.helpsend.cache.ACache;
import com.edu.hrbeu.helpsend.databinding.ActivityLocateBinding;
import com.edu.hrbeu.helpsend.global.GlobalData;
import com.edu.hrbeu.helpsend.utils.CommonUtil;
import com.edu.hrbeu.helpsend.utils.DrawableUtil;
import com.edu.hrbeu.helpsend.utils.LocationOverlay;
import com.edu.hrbeu.helpsend.utils.LocationUtil;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.param.Geo2AddressParam;
import com.tencent.lbssearch.object.param.SuggestionParam;
import com.tencent.lbssearch.object.result.Geo2AddressResultObject;
import com.tencent.lbssearch.object.result.SuggestionResultObject;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.mapsdk.raster.model.BitmapDescriptor;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.Circle;
import com.tencent.mapsdk.raster.model.GeoPoint;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.TencentMap;
import com.tencent.tencentmap.mapsdk.map.UiSettings;

import java.util.ArrayList;
import java.util.List;

public class LocateActivity extends MapActivity implements TencentLocationListener, MaterialSearchBar.OnSearchActionListener, View.OnClickListener {

    private ActivityLocateBinding mBinding;
    private TencentMap tencentMap;

    // 用于记录定位参数, 以显示到 UI
    private String mRequestParams;
    private TencentLocationManager mLocationManager;
    private TencentLocation mLocation;
    private Marker marker;
    private Circle mAccuracyCircle;
    private LocationOverlay mLocationOverlay;
    private double latitude;
    private double longitude;
    private Bitmap bmpMarker;
    private Geo2AddressResultObject obj;
    private String provider;
    private Context mContext;
    private LatLng mapCenter;
    private LatLng latLngLocation;
    private LocatePoiAdapter adapter;
    private ACache mCache;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_locate);
        mContext = this;
        mCache = ACache.get(this);
        initMap();
        Log.e("initMap", "======initMap======");
        uiSetting();
        Log.e("uisetting", "======uisetting======");
        mLocationManager = TencentLocationManager.getInstance(this);
        // 设置坐标系为 gcj-02, 缺省坐标为 gcj-02, 所以通常不必进行如下调用
        mLocationManager.setCoordinateType(TencentLocationManager.COORDINATE_TYPE_GCJ02);

        mBinding.recyclerSuggestion.setLayoutManager(new LinearLayoutManager(this));
        mBinding.searchBar.setOnSearchActionListener(this);
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

    private void initMap() {
        mBinding.ivCenterMarker.setImageBitmap(DrawableUtil.zoomDrawable(getResources().getDrawable(R.drawable.center_marker), 60, 90));
        tencentMap = mBinding.mapview.getMap();
        //设置卫星底图
        //tencentMap.setSatelliteEnabled(true);
        //设置实时路况开启
        //tencentMap.setTrafficEnabled(true);
        //设置缩放级别
        tencentMap.setZoom(20);
        bmpMarker = BitmapFactory.decodeResource(getResources(), R.drawable.my_position);
        mLocationOverlay = new LocationOverlay(bmpMarker);
        //  mBinding.mapview.addOverlay(mLocationOverlay);


    }


    @Override
    public void onLocationChanged(TencentLocation location, int error, String reason) {
        // Log.e("error", String.valueOf(error));
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
            marker.showInfoWindow();
            mBinding.mapview.invalidate();

            mapCenter = tencentMap.getMapCenter();
            String str = String.valueOf(mapCenter.getLatitude()) + "," + String.valueOf(mapCenter.getLongitude());
            com.tencent.lbssearch.object.Location loc = LocationUtil.str2Coordinate(this, str);
            Geo2AddressParam geo2AddressParam = new Geo2AddressParam().
                    location(loc).get_poi(true);
            TencentSearch tencentSearch = new TencentSearch(this);
            tencentSearch.geo2address(geo2AddressParam, new HttpResponseListener() {
                @Override
                public void onSuccess(int arg0, BaseObject arg1) {
                    // TODO Auto-generated method stub
                    if (arg1 == null) {
                        return;
                    }
                    obj = (Geo2AddressResultObject) arg1;
                    ArrayList<LocatePoi> pois = new ArrayList<>();
                    for (Geo2AddressResultObject.ReverseAddressResult.Poi poi : obj.result.pois) {
                        LocatePoi newPoi = new LocatePoi();
                        newPoi.setTitle(poi.title);
                        newPoi.setAddress(poi.address);
                        pois.add(newPoi);
                    }
                    if (pois.size() < 4) {
                        adapter = new LocatePoiAdapter(mContext, pois);
                    } else {
                        ArrayList<LocatePoi> poisTemp = new ArrayList<LocatePoi>();
                        for (int i = 0; i < 3; i++) {
                            poisTemp.add(pois.get(i));
                        }
                        adapter = new LocatePoiAdapter(mContext, poisTemp);
                    }
                    mBinding.lvNearby.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    mBinding.lvNearby.setVisibility(View.VISIBLE);
                    mBinding.lvNearby.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            TextView tvTitle = (TextView) mBinding.lvNearby.getChildAt(i).findViewById(R.id.tv_title);
                            Order.Location locationTemp = new Order.Location();
                            locationTemp.setDescription(tvTitle.getText().toString());
                            locationTemp.setLatitude(String.valueOf(mapCenter.getLatitude()));
                            locationTemp.setLongitude(String.valueOf(mapCenter.getLongitude()));
                            if (GlobalData.LOCATE_DIRECTION.equals("start")) {
                                GlobalData.MY_ORDER.setStartLocation(locationTemp);
                                GlobalData.POSITION_POINTS.setStart(locationTemp);
                            } else {
                                GlobalData.MY_ORDER.setEndLocation(locationTemp);
                                GlobalData.POSITION_POINTS.setEnd(locationTemp);
                            }
                            finish();
                        }
                    });

                }

                @Override
                public void onFailure(int arg0, String arg1, Throwable arg2) {
                    // TODO Auto-generated method stub
                }
            });
            Log.e("center", "经度：" + tencentMap.getMapCenter().getLatitude() + "维度：" + tencentMap.getMapCenter().getLongitude());
        } else {
            Log.e("reason", reason);
        }

    }

    @Override
    public void onStatusUpdate(String name, int status, String desc) {
        String message = "{name=" + name + ", new status=" + status + ", desc="
                + desc + "}";
        Log.e("message", message);

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
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(DrawableUtil.zoomDrawable(getResources().getDrawable(R.drawable.my_position), 60, 60));
            marker = tencentMap.addMarker(new MarkerOptions()
                    .position(new LatLng(39.922376, 116.394653))
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
    protected void onResume() {
        super.onResume();
        startLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocation();
    }

    private static GeoPoint of(TencentLocation location) {
        GeoPoint ge = new GeoPoint((int) (location.getLatitude() * 1E6),
                (int) (location.getLongitude() * 1E6));
        return ge;
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        String searchContent = mBinding.searchBar.getText().toString();
        if (searchContent.trim().length() == 0) {
            mBinding.recyclerSuggestion.setVisibility(View.GONE);
            return;
        }
        TencentSearch tencentSearch = new TencentSearch(this);
        SuggestionParam suggestionParam = new SuggestionParam().keyword(searchContent);
        tencentSearch.suggestion(suggestionParam, new HttpResponseListener() {
            @Override
            public void onSuccess(int i, BaseObject baseObject) {
                if (baseObject == null || searchContent.trim().length() == 0) {
                    mBinding.recyclerSuggestion.setVisibility(View.GONE);
                    return;
                }
                SuggestionResultObject result = (SuggestionResultObject) baseObject;
                List<SuggestionResultObject.SuggestionData> datas = new ArrayList<SuggestionResultObject.SuggestionData>();
                if (result.count < 3) {
                    datas = result.data;
                } else {
                    for (i = 0; i < 3; i++) {
                        SuggestionResultObject.SuggestionData data = result.data.get(i);
                        datas.add(data);
                    }
                }
                if (datas.size() == 0) {
                    mBinding.recyclerSuggestion.setVisibility(View.GONE);
                    return;
                }
                SuggestionAdapter adapter = new SuggestionAdapter(mContext, datas);
                mBinding.recyclerSuggestion.setAdapter(adapter);
                mBinding.recyclerSuggestion.setVisibility(View.VISIBLE);


            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {
                CommonUtil.showToast(mContext, s);
            }
        });

    }


    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode) {
            case MaterialSearchBar.BUTTON_NAVIGATION:
                finish();
                break;
            case MaterialSearchBar.VIEW_INVISIBLE:
                mBinding.recyclerSuggestion.setVisibility(View.GONE);
                break;
            case MaterialSearchBar.VIEW_VISIBLE:
                mBinding.recyclerSuggestion.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_loc:
                mCache.put("mLat", String.valueOf(latLngLocation.getLatitude()));
                mCache.put("mLng", String.valueOf(latLngLocation.getLongitude()));
                tencentMap.setCenter(latLngLocation);
                break;
        }
    }


}
