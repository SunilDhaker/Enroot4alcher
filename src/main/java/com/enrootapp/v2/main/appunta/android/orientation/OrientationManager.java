/*
   Copyright Sergi Mart�nez (@sergiandreplace)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */

package com.enrootapp.v2.main.appunta.android.orientation;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.enrootapp.v2.main.data.ConstantLib;

/**
 * This class is responsible for providing the measure of the compass (in the 3
 * axis) everytime it changes and dealing with the service
 *
 * @author Sergi Mart�nez
 */

public class OrientationManager implements SensorEventListener {

    public static final int MODE_COMPASS = 0;
    public static final int MODE_AR = 1;
    private int axisMode = MODE_AR;
    private static final float CIRCLE = (float) (Math.PI * 2);
    private static float SMOOTH_THRESHOLD = CIRCLE
            / ConstantLib.THERSHOLD_DIVISION;
    private static float SMOOTH_FACTOR = SMOOTH_THRESHOLD
            / ConstantLib.SMOOTH_DIVISION/*
                                         * , 0.14 , 0.13f
										 */;
    private static final int MIN_TIME = 100; // 2 min
    private static final int MIN_DISTANCE = 1; // 5 meters
    public static float zRotation = 0.0f;
    float x, y, z;
    int maxsize = 16;
    float oldDeviceAngle = -9999;
    private SensorManager sensorManager;
    private OrientationDevice orientationDevice = new OrientationDevice();
    private OrientationDevice oldOrientationDevice;
    private boolean sensorRunning = false;
    private OnOrientationChangedListener onOrientationChangeListener;
    private int firstAxis = SensorManager.AXIS_Y;
    private int secondAxis = SensorManager.AXIS_MINUS_X;
    private float[] mGravs = new float[3];
    private float[] mGeoMags = new float[3];
    private float[] mOrientation = new float[3];
    private float[] mRotationM = new float[9];
    private float[] mRemapedRotationM = new float[9];
    private boolean mFailed;
    private float lowPassValue;
    private Sensor mAccelerometerSensor, mMegneticSensor;

    // public void setmGavs(float[] mGravs) {
    // // TODO Auto-generated method stub
    // this.mGravs = mGravs;
    // }

    /**
     * This constructor will generate and start a Compass Manager
     *
     * @param activity The activity where the service will work
     */
    public OrientationManager(Activity activity) {
        sensorManager = (SensorManager) activity
                .getSystemService(Context.SENSOR_SERVICE);
        mMegneticSensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometerSensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        startSensor(activity);
    }

    public static int getPhoneRotation(Activity activity) {
        return 0;//activity.getWindowManager().getDefaultDisplay().getRotation(); TODO check this
    }

    // accelerometer data)

    /**
     * This method registers this class as a listener of the Sensor service
     *
     * @param activity The activity over this will work
     */

    public void startSensor(Activity activity) {

        sensorManager.registerListener(this, mAccelerometerSensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, mMegneticSensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * Detects a change in a sensor and warns the appropiate listener.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        boolean fromACC = false, fromMAG = false;
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                mGravs[0] = event.values[0];
                mGravs[1] = event.values[1];
                mGravs[2] = event.values[2];

                zRotation = (float) Math.toDegrees(Math.atan(mGravs[1] / mGravs[0]));
                fromACC = true;
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                // sensorManagerHere let's try another way:
                mGeoMags[0] = event.values[0];
                mGeoMags[1] = event.values[1];
                mGeoMags[2] = event.values[2];


                fromMAG = true;
                break;
            default:
                return;
        }

        // ProjectUtils.LogTag("SENSORS", "Accelerometer : " + mGravs[0] + " , "
        // + mGravs[1] + " , " + mGravs[2]);

        if (SensorManager.getRotationMatrix(mRotationM, null, mGravs, mGeoMags)) {
            // Rotate to the camera's line of view (Y axis along the camera's
            // axis)
            SensorManager.remapCoordinateSystem(mRotationM, firstAxis,
                    secondAxis, mRemapedRotationM);
            SensorManager.getOrientation(mRemapedRotationM, mOrientation);

            // float newDeviceAngle = (float) ProjectUtils
            // .getDeviceAngle(mOrientation[0]);
            //
            // if (oldDeviceAngle == -9999) {
            // oldDeviceAngle = newDeviceAngle;
            // } else if (fromACC
            // && Math.abs(newDeviceAngle - oldDeviceAngle) <
            // ConstantLib.ANGLE_Y) {
            // return;
            // } else if (fromMAG
            // && Math.abs(newDeviceAngle - oldDeviceAngle) <
            // ConstantLib.ANGLE_X) {
            // return;
            // }
            // oldDeviceAngle = newDeviceAngle;

            onSuccess();
        }// else
        // onFailure();}
    }

    void onSuccess() {
        if (mFailed)
            mFailed = false;

        // Convert the azimuth to degrees in 0.5 degree resolution.
        x = mOrientation[1];
        y = mOrientation[0];
        z = mOrientation[2];

        if (axisMode == MODE_AR) {
            // y=y+(getPhoneRotation(activity))*CIRCLE/4;
        }

        if (oldOrientationDevice == null) {
            orientationDevice.setX(x);
            orientationDevice.setY(y);
            orientationDevice.setZ(z);
            oldOrientationDevice = new OrientationDevice();
        } else {
            orientationDevice.setX(lowPass(x, oldOrientationDevice.getX()));
            orientationDevice.setY(lowPass(y, oldOrientationDevice.getY()));
            orientationDevice.setZ(lowPass(z, oldOrientationDevice.getZ()));
        }

        oldOrientationDevice.setX(orientationDevice.getX());
        oldOrientationDevice.setY(orientationDevice.getY());
        oldOrientationDevice.setZ(orientationDevice.getZ());

        // oldOrientationDevice = orientationDevice;

        if (getOnCompassChangeListener() != null) {
            getOnCompassChangeListener()
                    .onOrientationChanged(orientationDevice);

        }
    }

    /**
     * Applies a lowpass filter to the change in the lecture of the sensor
     *
     * @param newValue the new sensor value
     * @param lowValue the old sensor value
     * @return and intermediate value
     */
    public float lowPass(float newValue, float lowValue) {

        boolean bypass = false;
        if (bypass) {
            return newValue;
        }

        if (Math.abs(newValue - lowValue) < CIRCLE / 2) {
            if (Math.abs(newValue - lowValue) > SMOOTH_THRESHOLD) {
                lowPassValue = newValue;
            } else {
                lowPassValue = lowValue + SMOOTH_FACTOR * (newValue - lowValue);
            }
        } else {

            if (CIRCLE - Math.abs(newValue - lowValue) > SMOOTH_THRESHOLD) {
                lowPassValue = newValue;
            } else {
                if (lowValue > newValue) {
                    lowPassValue = (lowValue + SMOOTH_FACTOR
                            * ((CIRCLE + newValue - lowValue) % CIRCLE) + CIRCLE)
                            % CIRCLE;
                } else {
                    lowPassValue = (lowValue - SMOOTH_FACTOR
                            * ((CIRCLE - newValue + lowValue) % CIRCLE) + CIRCLE)
                            % CIRCLE;
                }
            }
        }
        return lowPassValue;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Space for rent
    }

    /**
     * We stop "hearing" the sensors
     */
    public void stopSensor() {
        sensorManager.unregisterListener(this);

    }

    /**
     * Just in case, we stop the sensor
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        stopSensor();
    }

    // Setters and getter for the three listeners (Bob, Moe and Curly)

    public OnOrientationChangedListener getOnCompassChangeListener() {
        return onOrientationChangeListener;
    }

    public void setOnOrientationChangeListener(
            OnOrientationChangedListener onOrientationChangeListener) {
        this.onOrientationChangeListener = onOrientationChangeListener;
    }

    public OrientationDevice getOrientation() {
        return orientationDevice;
    }

    public void setOrientation(OrientationDevice orientation) {
        this.orientationDevice = orientation;
    }

    public int getAxisMode() {
        return axisMode;
    }

    public void setAxisMode(int axisMode) {
        this.axisMode = axisMode;
        if (axisMode == MODE_COMPASS) {
            firstAxis = SensorManager.AXIS_Y;
            secondAxis = SensorManager.AXIS_MINUS_X;
        }
        if (axisMode == MODE_AR) {
            firstAxis = SensorManager.AXIS_X;
            secondAxis = SensorManager.AXIS_Z;
        }
    }

    public void changeValue() {
        // TODO Auto-generated method stub
        // /// for only testing
        SMOOTH_THRESHOLD = CIRCLE / ConstantLib.THERSHOLD_DIVISION;
        SMOOTH_FACTOR = SMOOTH_THRESHOLD / ConstantLib.SMOOTH_DIVISION;

//		ProjectUtils.LogTag("ENROOT", "SMOOTH_THRESHOLD : " + SMOOTH_THRESHOLD
//				+ " , ConstantLib.THERSHOLD_DIVISION : "
//				+ ConstantLib.THERSHOLD_DIVISION);
//
//		ProjectUtils.LogTag("ENROOT", "SMOOTH_FACTOR : " + SMOOTH_FACTOR
//				+ " , ConstantLib.SMOOTH_DIVISION : "
//				+ ConstantLib.SMOOTH_DIVISION);

        // ------
    }

    public interface OnOrientationChangedListener {
        /**
         * This method will be invoked when the magnetic orientation of the
         * phone changed
         *
         * @param orientation Orientation on degrees. 360-0 is north.
         */
        public void onOrientationChanged(OrientationDevice orientation);

    }

}
