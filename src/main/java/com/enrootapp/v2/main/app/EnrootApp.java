package com.enrootapp.v2.main.app;

import android.app.Application;
import android.graphics.Bitmap;

import com.enrootapp.v2.main.data.GeoName;
import com.enrootapp.v2.main.data.Impression;
import com.parse.Parse;

/**
 * Created by rmuttineni on 15/01/2015.
 */
public class EnrootApp extends Application {
    private static final String TAG = "EnrootApp";
    public static String CURRENT_GEOTAG_SELECTION;


    public String fbId ;
    public String fbName;
    public static Bitmap selfie ;
    public Impression imp ;

    public static EnrootApp getInstance() {
        return mApp;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getFbName() {
        return fbName;
    }

    public void setFbName(String fbName) {
        this.fbName = fbName;
    }

    public Bitmap getSelfie() {
        return selfie;
    }

    public void setSelfie(Bitmap selfie) {
        this.selfie = selfie;
    }

    public Impression getImp() {
        return imp;
    }

    public void setImp(Impression imp) {
        this.imp = imp;
    }

    public GeoName getCurrentGeoname() {
        return currentGeoname;
    }

    public void setCurrentGeoname(GeoName currentGeoname) {
        this.currentGeoname = currentGeoname;
    }

    public GeoName currentGeoname ;
    public static EnrootApp mApp ;

    public EnrootApp() {
        super();
        mApp = this;
 }

    @Override
    public void onCreate() {
        super.onCreate();
            Parse.initialize(this, "VKipN8qKOfPIzadjfVDcztnNXdUKr8J0IyFRtiLb", "DX7OTEIeGTQsbJ6Vf7Dj1xlyNhfD1vr2av00ZqZQ");
    }
}
