package com.enrootapp.v2.main.app;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.enrootapp.v2.main.util.Logger;
import com.enrootapp.v2.main.util.ar.MyData;

/**
 * Created by rmuttineni on 15/01/2015.
 */
public class EnrootActivity extends ActionBarActivity implements LocationListener {
    private static final int MIN_TIME = 100;
    private static final int MIN_DISTANCE = 100;
    private static final int TWO_MINUTES = 1000 * 60 * 1;
    public static boolean isCompassAvailable = false;
    public static boolean isfrontCameraAvailable = false;
    public static Location currentLoc;
    public String TAG;
    public Location lastLoc;
    public OnLocationChangedBy100m onLocationChangedBy100m;
    protected EnrootApp mApp;
    private LocationManager locationMgr;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (EnrootApp) getApplication();
        TAG = getClass().getName();
        isfrontCameraAvailable = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
        isCompassAvailable = ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null ? false : true;

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

        Logger.persist();
    }

    protected void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

        if (lastLoc == null) {
            lastLoc = location;
            currentLoc = location;

        } else {
            if (isBetterLocation(location, lastLoc))
                currentLoc = location;

            if (lastLoc.distanceTo(location) > 100) {
                lastLoc = currentLoc;
                if (onLocationChangedBy100m != null)
                    onLocationChangedBy100m.onLocationChangedBy100m(location);
            }
        }
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

    public void setOnLocationChangedBy100m(OnLocationChangedBy100m onLocationChangedBy100m) {
        this.onLocationChangedBy100m = onLocationChangedBy100m;
    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public static interface OnLocationChangedBy100m {

        public void onLocationChangedBy100m(Location location);
    }

}
