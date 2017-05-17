package com.edu.hrbeu.helpsend.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.tencent.mapsdk.raster.model.GeoPoint;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.Overlay;
import com.tencent.tencentmap.mapsdk.map.Projection;


public class LocationOverlay extends Overlay{
    GeoPoint geoPoint;
    Bitmap bmpMarker;
    float fAccuracy = 0f;

    public LocationOverlay(Bitmap mMarker) {
        bmpMarker = mMarker;
    }

    public void setGeoCoords(GeoPoint point) {
        if (geoPoint == null) {
            geoPoint = new GeoPoint(point.getLatitudeE6(),
                    point.getLongitudeE6());
        } else {
            geoPoint.setLatitudeE6(point.getLatitudeE6());
            geoPoint.setLongitudeE6(point.getLongitudeE6());
        }
    }

    public void setAccuracy(float fAccur) {
        fAccuracy = fAccur;
    }

    @Override
    public void draw(Canvas canvas, MapView mapView) {
        if (geoPoint == null) {
            return;
        }
        Projection mapProjection = mapView.getProjection();
        Paint paint = new Paint();
        Point ptMap = mapProjection.toPixels(geoPoint, null);
        paint.setColor(Color.BLUE);
        paint.setAlpha(8);
        paint.setAntiAlias(true);

        float fRadius = mapProjection.metersToEquatorPixels(fAccuracy);
        canvas.drawCircle(ptMap.x, ptMap.y, fRadius, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAlpha(200);
        canvas.drawCircle(ptMap.x, ptMap.y, fRadius, paint);

        if (bmpMarker != null) {
            paint.setAlpha(255);
            canvas.drawBitmap(bmpMarker, ptMap.x - bmpMarker.getWidth() / 2,
                    ptMap.y - bmpMarker.getHeight() / 2, paint);
        }

        super.draw(canvas, mapView);
    }
}
