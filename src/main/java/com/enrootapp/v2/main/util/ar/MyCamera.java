package com.enrootapp.v2.main.util.ar;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.enrootapp.v2.main.util.Logger;

import java.io.IOException;

/**
 * Created by sdhaker on 10-01-2015.
 */
public class MyCamera {
    private final static String LOG_TAG = "MyCamera";
    public int width;
    public int height;
    int direction;
    SurfaceTexture txtur;
    private Camera mCamera;
    private Camera.Parameters mCameraParams;
    private Boolean running = false;

    void stop() {
        if (running) {
            Logger.d(LOG_TAG, "Stopping Camera");
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera = null;
            running = false;
        }
    }

    public void setParameters(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void openCam(SurfaceTexture texture, int direction) {
        Logger.d(LOG_TAG, "Opening Camera...");
        if (mCamera != null) mCamera.release();
        mCamera = Camera.open(direction);
        this.direction = direction;
        txtur = texture;
        mCameraParams = mCamera.getParameters();
        height = mCameraParams.getPreviewSize().height;
        width = mCameraParams.getPreviewSize().width;
        //mCameraParams.setPreviewSize(width, height);
        //mCameraParams.setRotation(90);
        // mCamera.setDisplayOrientation(90);
        mCamera.setParameters(mCameraParams);


        try {
            mCamera.setPreviewTexture(texture);
            mCamera.startPreview();

            running = true;
        } catch (IOException e) {
            Logger.d("MyCamera", "Unable to open camera.");
            Logger.d("MyCamera", "inside openCam", e);
        }
    }

    public void switchCam() {

        direction = (direction + 1) % 2;
        openCam(txtur, direction);
    }
}