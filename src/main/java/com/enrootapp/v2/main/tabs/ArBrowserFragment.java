package com.enrootapp.v2.main.tabs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.enrootapp.v2.main.MainActivity;
import com.enrootapp.v2.main.R;
import com.enrootapp.v2.main.app.EnrootActivity;
import com.enrootapp.v2.main.app.EnrootApp;
import com.enrootapp.v2.main.appunta.android.orientation.OrientationDevice;
import com.enrootapp.v2.main.appunta.android.orientation.OrientationManager;
import com.enrootapp.v2.main.data.Impression;
import com.enrootapp.v2.main.util.SelectLocationActivity;
import com.enrootapp.v2.main.util.TestUtil;
import com.enrootapp.v2.main.util.ar.ArSurfaceView;
import com.enrootapp.v2.main.util.ar.MyCamera;
import com.enrootapp.v2.main.util.ar.SelfieActivity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sdhaker on 18-01-2015.
 */

public class ArBrowserFragment extends Fragment implements OrientationManager.OnOrientationChangedListener, View.OnClickListener , EnrootApp.OnDataChangeListner{

    private static final String TAG = "ArBrowser Fragment";
    public static boolean flagControlVisible = true;
    public MyCamera myCamera;
    ArSurfaceView ar;
    OrientationManager compass;
    ImageButton arSearch, arStatic, arSelfei;
    View actionBarLocation;
    TextView actionBarLocationtext;
    View hidingView, emptyViewTop;
    Timer t;
    private long lastTouchTime = System.currentTimeMillis();
    private boolean doesHideControl = true;

    HashSet<String> drawed = new HashSet() ;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        myCamera = new MyCamera();
        //ar = new ArSurfaceView(getActivity() ,myCamera);
        // ar.onResume();
        View v = inflater.inflate(R.layout.ar_browser, null);
        ar = (ArSurfaceView) v.findViewById(R.id.ar_view);
        //ar.setOnTouchListener(this);
        compass = new OrientationManager(getActivity());
        compass.setAxisMode(OrientationManager.MODE_AR);
        compass.setOnOrientationChangeListener(this);
        compass.startSensor(getActivity());
        arSearch = (ImageButton) v.findViewById(R.id.ar_controls_search);
        arSelfei = (ImageButton) v.findViewById(R.id.ar_controls_selfie);
        arStatic = (ImageButton) v.findViewById(R.id.ar_controls_static);
        emptyViewTop = v.findViewById(R.id.emty_view);
        actionBarLocation = v.findViewById(R.id.custome_action_bar_location_container);
        actionBarLocationtext = (TextView) v.findViewById(R.id.custome_action_bar_location);
        arSearch.setOnClickListener(this);
        arStatic.setOnClickListener(this);
        arSelfei.setOnClickListener(this);
        actionBarLocation.setOnClickListener(this);
        hidingView = v;
        startTimer();
        ar.containerFragment = this;
        return v;
    }


    private void startTimer() {
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - lastTouchTime > 10000) {
                    lastTouchTime = System.currentTimeMillis();
                    if (flagControlVisible && doesHideControl) {
                        hideControls();
                        flagControlVisible = false;
                    }

                }
            }
        };
        t = new Timer();
        t.schedule(tt, 8000, 5000);
       TimerTask tt2 = new TimerTask() {
            @Override
            public void run() {
                addMax20Texture();
            }
        };
        Timer t2 = new Timer();
        t2.schedule(tt2 , 8000 , 5000 );
    }

    private void addMax20Texture() {

        for(Impression i : EnrootApp.getInstance().imressionsAt){

        }
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();
        doesHideControl = false;
        ar.onPause();
        t.cancel();
        this.onDestroyView();

    }

    @Override
    public void onResume() {
        doesHideControl = true;
        super.onResume();
        ar.onResume();
    }

    @Override
    public void onOrientationChanged(OrientationDevice orientation) {
        // Log.d(TAG, "on orientation changeg listner");
        ar.setOrientation(orientation);
        ar.setPhoneRotation(OrientationManager.getPhoneRotation(getActivity()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ar_controls_search:
                //TODO do searching
                break;
            case R.id.ar_controls_selfie:
                Intent i = new Intent(getActivity(), SelfieActivity.class);
                startActivity(i);
                break;
            case R.id.ar_controls_static:
//                Intent i1 = new Intent(getActivity(), StaticBrowserActivity.class);
                //startActivity(i1);
//                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
//                fm.replace(new StaticBrowserFragment() , this);
                MainActivity.USER_SELECTION = MainActivity.DYNAMIC;
                ((MainActivity) getActivity()).notifyBrowserChanged();
                break;
            case R.id.custome_action_bar_location_container:
                Intent i2 = new Intent(getActivity(), SelectLocationActivity.class);
                startActivity(i2);
        }

    }

    public void hideControls() {
        hidingView.post(new Runnable() {
            @Override
            public void run() {
                arSelfei.setVisibility(View.INVISIBLE);
                arSearch.setVisibility(View.INVISIBLE);
                emptyViewTop.setVisibility(View.GONE);
                ((MainActivity) getActivity()).hideTab();
            }
        });
    }

    public void showControls() {
        hidingView.post(new Runnable() {
            @Override
            public void run() {
                arSelfei.setVisibility(View.VISIBLE);
                arSearch.setVisibility(View.VISIBLE);
                emptyViewTop.setVisibility(View.VISIBLE);
                ((MainActivity) getActivity()).showTabs();
                startTimer();
            }
        });
    }


    public boolean touch(MotionEvent event) {
        lastTouchTime = System.currentTimeMillis();
        if (!flagControlVisible) {
            showControls();
            flagControlVisible = true;
        }
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDtaChange() {

    }


}
