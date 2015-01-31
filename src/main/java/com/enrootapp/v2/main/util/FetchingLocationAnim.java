package com.enrootapp.v2.main.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.enrootapp.v2.main.R;
import com.enrootapp.v2.main.app.EnrootActivity;
import com.enrootapp.v2.main.util.ar.MyData;

/**
 * Created by sdhaker on 26-01-2015.
 */
public class FetchingLocationAnim extends EnrootActivity implements SurfaceHolder.Callback, android.location.LocationListener, View.OnTouchListener, Runnable {

    private static final int MIN_TIME = 100;
    private static final int MIN_DISTANCE = 100;
    SurfaceView animView;
    int width, height;
    SurfaceHolder holder;
    Canvas canvas;
    private LocationManager locationMgr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().hide();

        setContentView(R.layout.anim_location);
        animView = (SurfaceView) findViewById(R.id.location_anim);
        holder = animView.getHolder();
        //  holder.addCallback(this);
        animView.setZOrderMediaOverlay(true);
        animView.setZOrderOnTop(true);
        animView.setOnTouchListener(this);


        locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                MIN_TIME, MIN_DISTANCE, this);


        try {
            Location gps = locationMgr
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location network = locationMgr
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (gps != null)
                onLocationChanged(gps);
            else if (network != null)
                onLocationChanged(network);
            else
                onLocationChanged(MyData.hardFix);
        } catch (Exception ex2) {
            onLocationChanged(MyData.hardFix);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public void drawCircle(float x, float y, float radius, int color, int outline) {

        holder = animView.getHolder();
        if (holder != null) {
            Canvas c = holder.lockCanvas();
            if (c != null) {
                c.drawColor(Color.WHITE);
                Paint p = new Paint();
                p.setColor(Color.BLUE);
                p.setStyle(Paint.Style.FILL);
                c.drawCircle(new RandomGen().nextInt(800), 100, 50, p);
                holder.unlockCanvasAndPost(c);
            }
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //this.holder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.holder = holder;
        this.width = width;
        this.height = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    @Override
    public void onLocationChanged(Location location) {

        drawCircle(width / 2, height / 2, location.getAccuracy(), Color.BLACK, Color.BLUE);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        holder = animView.getHolder();
//        Canvas c = holder.lockCanvas();
//        c.drawColor(Color.WHITE);
//        Paint p = new Paint();
//        p.setColor(Color.BLUE);
//        p.setStyle(Paint.Style.FILL);
//        c.drawCircle(100, 100, 50, p);
//        holder.unlockCanvasAndPost(c);
        return false;
    }

    @Override
    public void run() {

    }
}
