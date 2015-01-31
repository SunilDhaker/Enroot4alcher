package com.enrootapp.v2.main.util.ar;

/**
 * Created by rmuttineni on 15/01/2015.
 */

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.enrootapp.v2.main.MainActivity;
import com.enrootapp.v2.main.R;
import com.enrootapp.v2.main.app.EnrootActivity;
import com.enrootapp.v2.main.appunta.android.orientation.OrientationDevice;
import com.enrootapp.v2.main.appunta.android.orientation.OrientationManager;
import com.enrootapp.v2.main.data.GeoName;
import com.enrootapp.v2.main.data.Impression;
import com.enrootapp.v2.main.util.FileUtils;
import com.enrootapp.v2.main.util.Logger;
import com.enrootapp.v2.main.util.SelectLocationActivity;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * This class extends the SensorsActivity and is designed tie the AugmentedView
 *
 * @author Sunil dhaker <sunil965@live.com>
 */

public class SelfieActivity extends EnrootActivity implements OrientationManager.OnOrientationChangedListener, View.OnClickListener, View.OnTouchListener, TextWatcher {
    public static final int SELFIE_SCREEN = 0, FEED_BACK_SCREEN = 1, TAG_SCREEN = 2;
    private static final String TAG = "Selfie Activity ";
    public static boolean useDataSmoothing = true;
    public static int CURENT_SCREEN = 0;
    public SurfaceHolder holder;
    public float currentTagPositionX = 0.0f;
    public float currentTagPositionY = 0.0f;
    public RecyclerView rv2;
    SurfaceHolder mSurfaceHolder;
    FragmentManager fm;
    SelfieView mGLSurfaceView;
    OrientationManager compass;
    Button switchcam, goLive;
    ImageButton shutter;
    RelativeLayout controlContainer, customActionBarContainer, bottomBarContainer, capationContainer, shutterContainer;
    LinearLayout editLocationContainer;
    ImageView backButton, forwordButton, bottoBarNext, tagCross;
    TextView actionBarLocation, actionBarTag, tagAndShareText;
    View emptyView, ev3, taggingOverlay, rv;
    EditText taggingText, capationText;
    private Camera camera;
    private SurfaceView tagView;
    private MyCamera mCamera;
    Impression imp ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCamera = new MyCamera();
        MyData.setContext(this);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        mGLSurfaceView = new SelfieView(this, mCamera);
        addContentView(mGLSurfaceView, new LayoutParams(
                android.app.ActionBar.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mGLSurfaceView.setZOrderMediaOverlay(true);

        View v = getLayoutInflater().inflate(R.layout.selfie_overlay, null);

        addContentView(v, new LayoutParams(
                android.app.ActionBar.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        taggingOverlay = getLayoutInflater().inflate(R.layout.tagged_overlay, null);

        addContentView(taggingOverlay, new LayoutParams(
                android.app.ActionBar.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        rv = findViewById(R.id.select_friend_rv_selfie);

        rv2 = (RecyclerView) findViewById(R.id.tag_recycler);
        rv2.setLayoutManager(new LinearLayoutManager(this));

//        rv2.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//                View v = rv.findChildViewUnder(e.getX(), e.getY());
//                if (v != null) {
//                    LightFreindsAdpter.ViewHolder o = (LightFreindsAdpter.ViewHolder) rv.getChildViewHolder(v);
//                    if (o != null)
//                        selectFreind(o.u);
//
//                }
//                return true;
//            }
//
//            @Override
//            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//
//            }
//        });

        shutter = (ImageButton) findViewById(R.id.shutter_extended);
        switchcam = (Button) findViewById(R.id.selfie_switch_cam);
        goLive = (Button) findViewById(R.id.selfie_btn_golive);
        controlContainer = (RelativeLayout) findViewById(R.id.control_cam);
        customActionBarContainer = (RelativeLayout) findViewById(R.id.custome_action_bar_container);
        editLocationContainer = (LinearLayout) findViewById(R.id.custome_action_bar_location_container);
        actionBarLocation = (TextView) findViewById(R.id.custome_action_bar_location);
        //forwordButton = (ImageView) findViewById(R.id.custome_action_bar_forword_ok);
        backButton = (ImageView) findViewById(R.id.custome_action_bar_back);
        bottoBarNext = (ImageView) findViewById(R.id.bottom_bar_btn_next);
        bottomBarContainer = (RelativeLayout) findViewById(R.id.bottom_bar_container);
        actionBarTag = (TextView) findViewById(R.id.custome_action_bar_tag_text);
        tagAndShareText = (TextView) findViewById(R.id.tag_and_share_text);
        capationContainer = (RelativeLayout) findViewById(R.id.capation_container);
        emptyView = findViewById(R.id.empty_view);
        ev3 = findViewById(R.id.empty_view3);
        shutterContainer = (RelativeLayout) findViewById(R.id.shutter_extended_container);
        tagView = (SurfaceView) findViewById(R.id.surface_for_tagging_people);
        holder = tagView.getHolder();
        tagView.setZOrderMediaOverlay(true);
        tagView.setZOrderOnTop(true);
        taggingText = (EditText) findViewById(R.id.tag_search);
        tagCross = (ImageView) findViewById(R.id.tag_cross);
        capationText = (EditText) findViewById(R.id.edit_capation);


        setSelfieScreen();

        shutter.setOnClickListener(this);
        switchcam.setOnClickListener(this);
        goLive.setOnClickListener(this);
        editLocationContainer.setOnClickListener(this);
        bottoBarNext.setOnClickListener(this);
        //forwordButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        tagView.setOnTouchListener(this);
        taggingText.addTextChangedListener(this);
        tagCross.setOnClickListener(this);

        MaterialRippleLayout.on(shutter)
                .rippleColor(Color.parseColor("#EEFF41"))
                .rippleAlpha(1f)
                .rippleDuration(250)
                .rippleOverlay(true)
                .rippleHover(true)
                .create();
        MaterialRippleLayout.on(goLive)
                .rippleColor(Color.parseColor("#727272"))
                .rippleAlpha(1f)
                .rippleHover(true)
                .create();
        MaterialRippleLayout.on(switchcam)
                .rippleColor(Color.parseColor("#727272"))
                .rippleAlpha(1f)
                .rippleAlpha(1f)
                .rippleHover(true)
                .create();
        MaterialRippleLayout.on(bottoBarNext)
                .rippleColor(Color.parseColor("#EEFF41"))
                .rippleAlpha(1f)
                .rippleOverlay(true)
                .rippleHover(true)
                .create();

        compass = new OrientationManager(this);
        compass.setAxisMode(OrientationManager.MODE_AR);
        compass.setOnOrientationChangeListener(this);
        compass.startSensor(this);


//
//        Ion.with(this)
//                .load("http://graph.facebook.com/" + mApp.getFbId() + "/picture?type=large")
//                .write(FileUtils.getFile("UserPic"))
//                .setCallback(new FutureCallback<File>() {
//            @Override
//            public void onCompleted(Exception e, File file) {
//                Logger.d(TAG, "YOUR" + " image downloaded");
//            }
//        });


        
        downloadMyTrails();
        downloadImpressionAt(mApp.getCurrentGeoname());
        

    }



    @Override
    public void onResume() {

        super.onResume();
        mGLSurfaceView.onResume();
        setSelfieScreen();
    }

    @Override
    public void onPause() {

        super.onPause();
        mGLSurfaceView.onPause();

    }


    public void openCamera() {
        //mCamera.switchCam(mSurfaceHolder);
        //mGLSurfaceView.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onOrientationChanged(OrientationDevice orientation) {

        //Log.d(TAG, "on orientation changeg listner");
        mGLSurfaceView.setOrientation(orientation);
        mGLSurfaceView.setPhoneRotation(OrientationManager.getPhoneRotation(this));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.shutter_extended:
                Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                animFadeIn.setDuration(500);
                rv.setVisibility(View.VISIBLE);

                //rv.setBackgroundColor(Color.argb(25 , 0 , 0 , 0));
                mGLSurfaceView.capture(true);
                break;
            case R.id.selfie_btn_golive:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                break;
            case R.id.selfie_switch_cam:
                if (EnrootActivity.isCompassAvailable)
                    mGLSurfaceView.switchCam();
                break;
            case R.id.custome_action_bar_location_container:
                Intent i5 = new Intent(this, SelectLocationActivity.class);
                startActivity(i5);
                break;
            case R.id.custome_action_bar_back:
                if (CURENT_SCREEN == FEED_BACK_SCREEN) {
                    //setSelfieScreen();
                    recreate();
                }
                if (CURENT_SCREEN == TAG_SCREEN) {
                    if (EnrootActivity.isCompassAvailable)
                        setFeedBackScreen();
                    else recreate();
                }
                break;
            case R.id.bottom_bar_btn_next:
                if (CURENT_SCREEN == FEED_BACK_SCREEN) {
                    setTagAndShareScreen();
                    break;
                }
                if (CURENT_SCREEN == TAG_SCREEN) {
                    imp = new Impression();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    mApp.selfie.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    // get byte array here
                    byte[] bytearray= stream.toByteArray();
                    ParseFile pf = new ParseFile("pp.jpg" ,bytearray);
                    imp.setImpression(pf);
                    imp.setDirection(mGLSurfaceView.picAzimuth);
                    imp.setCaption(capationText.getText().toString());
                    imp.setOwnerName(mApp.fbName);
                    imp.setTimestamp(new Date(System.currentTimeMillis()));
                    //imp.setOwner(ParseUser.getCurrentUser());
                    imp.setOwnerId(mApp.fbId);
                    imp.setGeoname(mApp.currentGeoname);
                    mApp.imp = imp;
                    pf.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            imp.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                   if(e!= null)
                                    Log.d(TAG , e.getMessage());
                                }
                            });
                            if(e == null)Log.d(TAG, "Your impression is saved ");
                            else runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext() , "Problem in saving impression" , Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                    Intent i2 = new Intent(this,MainActivity.class);
                    startActivity(i2);
                }

                break;
            case R.id.tag_cross:
                closeTagBox();

        }
    }

    private void closeTagBox() {
        taggingOverlay.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);
    }

    private void openTagBox() {

        taggingOverlay.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.GONE);
//        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
//                SelfieActivity.this);
//        builderSingle.setIcon(R.drawable.ic_launcher);
//        builderSingle.setTitle("Tag Friend:-");
//        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
//                SelfieActivity.this,
//                android.R.layout.select_dialog_singlechoice);
//
//        arrayAdapter.add("Archit");
//        arrayAdapter.add("Jignesh");
//        arrayAdapter.add("Umang");
//        arrayAdapter.add("Gatti");
//        builderSingle.setNegativeButton("cancel",
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//
//        builderSingle.setAdapter(arrayAdapter,
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String strName = arrayAdapter.getItem(which);
//                        selectFreind(which);
//                        dialog.dismiss();
//                    }
//                });
//        builderSingle.show();
    }

    private void setTagAndShareScreen() {

        CURENT_SCREEN = TAG_SCREEN;
        // mGLSurfaceView.setTagScreen(true);
        // rv.setVisibility(View.VISIBLE);
        ev3.setVisibility(View.VISIBLE);
        taggingOverlay.setVisibility(View.GONE);
        rv.setVisibility(View.GONE);
        shutterContainer.setVisibility(View.VISIBLE);
        shutter.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        controlContainer.setVisibility(View.GONE);
        actionBarTag.setVisibility(View.VISIBLE);
        tagAndShareText.setText("Share");
        capationContainer.setVisibility(View.VISIBLE);
        //forwordButton.setVisibility(View.VISIBLE);
        actionBarLocation.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);
        bottomBarContainer.setVisibility(View.VISIBLE);
        backButton.setImageDrawable(getResources().getDrawable(R.drawable.back));
        //forwordButton.setImageDrawable(getResources().getDrawable(R.drawable.back));
    }


    public void setFeedBackScreen() {
        //rv.clearAnimation();
        taggingOverlay.setVisibility(View.GONE);
        rv.setVisibility(View.GONE);
        ev3.setVisibility(View.GONE);
        shutter.setVisibility(View.GONE);
        shutterContainer.setVisibility(View.GONE);
        CURENT_SCREEN = FEED_BACK_SCREEN;
        emptyView.setVisibility(View.VISIBLE);
        actionBarTag.setVisibility(View.GONE);
        controlContainer.setVisibility(View.GONE);
        bottomBarContainer.setVisibility(View.VISIBLE);
        editLocationContainer.setVisibility(View.GONE);
        // forwordButton.setVisibility(View.GONE);
        tagAndShareText.setText("Tag And Share");
        backButton.setImageDrawable(getResources().getDrawable(R.drawable.cross));
        actionBarTag.setVisibility(View.GONE);
        capationContainer.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);
    }


    private void setSelfieScreen() {
        taggingOverlay.setVisibility(View.GONE);
        rv.setVisibility(View.GONE);
        CURENT_SCREEN = SELFIE_SCREEN;
        shutter.setVisibility(View.VISIBLE);
        ev3.setVisibility(View.GONE);
        shutterContainer.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.GONE);
        // forwordButton.setVisibility(View.GONE);
        actionBarTag.setVisibility(View.GONE);
        bottomBarContainer.setVisibility(View.GONE);
        capationContainer.setVisibility(View.GONE);
        controlContainer.setVisibility(View.VISIBLE);
        actionBarLocation.setVisibility(View.VISIBLE);

        actionBarLocation.setText(mApp.getCurrentGeoname().getName());
    }


    public void setFlagCaptured() {
        CURENT_SCREEN = FEED_BACK_SCREEN;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (EnrootActivity.isCompassAvailable)
                    setFeedBackScreen();
                else {
                    CURENT_SCREEN = TAG_SCREEN;
                    setTagAndShareScreen();
                }
            }
        });
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {


        if (CURENT_SCREEN == TAG_SCREEN) {

        }
        return false;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        //TODO first draw the other tagged people

        if (CURENT_SCREEN == TAG_SCREEN) {

           // lightFreindsAdapter.search(s.toString());

        }
    }


}
