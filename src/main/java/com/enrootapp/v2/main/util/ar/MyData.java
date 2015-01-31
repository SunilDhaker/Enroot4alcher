package com.enrootapp.v2.main.util.ar;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import javax.microedition.khronos.opengles.GL10;

/**
 * Abstract class which should be used to set global data
 *
 * @author Sunil Dhaker
 */
public abstract class MyData {

    public static final Location hardFix = new Location("ATL");

    static {
        hardFix.setLatitude(26.18771881);
        hardFix.setLongitude(91.69989288);
        hardFix.setAltitude(0);
    }

    private static Location currentLocation = hardFix;
    private static final String TAG = "MyData";
    private static final Object currentLocationLock = new Object();
    private static final Object rotationMatrixLock = new Object();
    private static final Object azimuthLock = new Object();
    private static final Object rollLock = new Object();
    private static final Object orientationLock = new Object();
    private static final Object orientationAngleLock = new Object();
    public static SharedPreferences prefManager;
    private static float azimuth = 0;
    private static float roll = 0;
    private static int orientationAngle = 0;
    private static GL10 gl;
    private static Context context;
    private static Screen screen = Screen.LIVE_VIEW;
    private static int glWidth = 320;
    private static int glHeight = 480;
    private static float accelZ;


    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        MyData.context = context;
    }

    private static void onLocationChanged(Location location) {

        //Log.v(TAG, "" + currentLocation.getLatitude());
        //MyMessages.checkForUpdates(location);

    }

    /**
     * Get the current Location.
     *
     * @return Location representing the current location.
     */
    public static Location getCurrentLocation() {
        synchronized (MyData.currentLocationLock) {
            return MyData.currentLocation;
        }
    }

    /**
     * Set the current location.
     *
     * @param currentLocation Location to set.
     * @throws NullPointerException if Location param is NULL.
     */
    public static void setCurrentLocation(Location currentLocation) {
        if (currentLocation == null)
            throw new NullPointerException();
        // Logger.d(TAG, "current location. location=" + currentLocation.toString());
        synchronized (MyData.currentLocationLock) {
            MyData.currentLocation = currentLocation;
        }
        onLocationChanged(currentLocation);
    }



    /**
     * Get the current Azimuth.
     *
     * @return azimuth float representing the azimuth.
     */
    public static float getAzimuth() {
        synchronized (MyData.azimuthLock) {
            return MyData.azimuth;
        }
    }

    /**
     * Set the current Azimuth.
     *
     * @param azimuth float representing the azimuth.
     */
    public static void setAzimuth(float azimuth) {
        synchronized (MyData.azimuthLock) {
            MyData.azimuth = azimuth;
        }
    }

    /**
     * Get the current Roll.
     *
     * @return roll float representing the roll.
     */
    public static float getRoll() {
        synchronized (MyData.rollLock) {
            return MyData.roll;
        }
    }

    /**
     * Set the current Roll.
     *
     * @param roll float representing the roll.
     */
    public static void setRoll(float roll) {
        synchronized (MyData.rollLock) {
            MyData.roll = roll;
        }
    }


    /**
     * Get the current orientation angle.
     *
     * @return angle int representing the orientation angle.
     */
    public static int getDeviceOrientationAngle() {
        synchronized (MyData.orientationAngleLock) {
            return MyData.orientationAngle;
        }
    }

    /**
     * Set the current orientation angle.
     *
     * @param angle int representing the orientation angle.
     */
    public static void setDeviceOrientationAngle(int angle) {
        synchronized (MyData.orientationAngleLock) {
            MyData.orientationAngle = angle;
        }
    }

    public static void setGL10(GL10 gl10) {
        gl = gl10;
    }

    public static GL10 getGl10() {
        return gl;
    }

    public static Screen getScreen() {

        return screen;
    }

    public static void setScreen(Screen screen) {
        MyData.screen = screen;
    }

    public static int getGlWidth() {
        return glWidth;
    }

    public static void setGlWidth(int glWidth) {
        MyData.glWidth = glWidth;
    }

    public static int getGlHeight() {
        return glHeight;
    }

    public static void setGlHeight(int glHeight) {
        MyData.glHeight = glHeight;
    }

    public static float getAccelZ() {
        return accelZ;
    }

    public static void setAccelZ(float accelZ) {
        MyData.accelZ = accelZ;
    }


    /* defaulting to our place */
    public static enum Screen {
        LIVE_VIEW, PUBLIC_OR_PRIVATE, PLAT_VIEW, TYPING
    }


}

