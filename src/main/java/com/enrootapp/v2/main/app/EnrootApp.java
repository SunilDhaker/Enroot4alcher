package com.enrootapp.v2.main.app;

import android.app.Application;
import android.graphics.Bitmap;

import com.enrootapp.v2.main.data.GeoName;
import com.enrootapp.v2.main.data.Impression;
import com.parse.Parse;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by rmuttineni on 15/01/2015.
 */
public class EnrootApp extends Application {
    private static final String TAG = "EnrootApp";
    public static String CURRENT_GEOTAG_SELECTION;

    public ArrayList<Impression> imressionsAt  = new ArrayList<Impression>();
   public  ArrayList<Impression> myTrails =new ArrayList<Impression>();
    public ArrayList<GeoName> nearbyGeoPoints = new ArrayList<GeoName>();
    public ArrayList<OnDataChangeListner> listners = new ArrayList<>();

    public HashSet<String> isLoadingFlags  = new HashSet<String>();

    public String fbId  = "fbid123";
    public String fbName ="fbname123";
    public static Bitmap selfie ;
    public Impression imp ;

    public void datachanged(){
        for( OnDataChangeListner odcl : listners){
            odcl.onDtaChange();
        }
    }

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
            ParseObject.registerSubclass(GeoName.class);
        ParseObject.registerSubclass(Impression.class);
        Parse.initialize(this, "QUVn4wgqaytKbF8WLE4mp7EwqUVgMRTeyiSWiunS", "blOQ11g9fVPott4RO9QFn4yDBt1d3toDXdcd9WiI");
    }


    public static interface OnDataChangeListner{

        public void onDtaChange();
    }
}
