package com.enrootapp.v2.main.app;

import android.app.Application;
import android.graphics.Bitmap;

import com.enrootapp.v2.main.data.GeoName;
import com.enrootapp.v2.main.data.Impression;

/**
 * Created by rmuttineni on 15/01/2015.
 */
public class EnrootApp extends Application {
    private static final String TAG = "EnrootApp";
    public static String CURRENT_GEOTAG_SELECTION;


    public String fbId ;
    public String fbName;
    public Bitmap selfie ;
    public Impression imp ;
    public GeoName currentGeoname ;
    EnrootApp mApp ;

    public EnrootApp() {
        super();
        mApp = this ;
 }
}
