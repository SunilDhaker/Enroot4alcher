package com.enrootapp.v2.main.util.ar;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.enrootapp.v2.main.appunta.android.math3d.Math3dUtil;
import com.enrootapp.v2.main.appunta.android.math3d.Trig1;
import com.enrootapp.v2.main.appunta.android.math3d.Trig3;
import com.enrootapp.v2.main.appunta.android.math3d.Vector1;
import com.enrootapp.v2.main.appunta.android.math3d.Vector3;
import com.enrootapp.v2.main.appunta.android.orientation.OrientationDevice;
import com.enrootapp.v2.main.data.Impression;
import com.enrootapp.v2.main.tabs.ArBrowserFragment;
import com.enrootapp.v2.main.util.Logger;
import com.enrootapp.v2.main.util.graphics.DirectVideo;
import com.enrootapp.v2.main.util.graphics.ImpressionTexture;
import com.enrootapp.v2.main.util.graphics.MatrixHelper;
import com.enrootapp.v2.main.util.graphics.RadarTexture;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;

/**
 * Created by sdhaker on 18-01-2015.
 */
public class ArSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, GLSurfaceView.OnTouchListener {


    private static final String TAG = "Ar Surface View";
    public int width = 480;
    public int height = 720;
    public ArBrowserFragment containerFragment;
    Context mContext;
    MyCamera mCamera;
    DirectVideo liveCamerTexture;
    float[] mtx = new float[16];
    float[] ortho = new float[16];
    float[] projectionMatrix = new float[16];
    float[] modelMatrix = new float[16];
    float[] viewMatrix = new float[16];
    float[] temp2 = new float[16];
    ArrayList<Impression> impressions = new ArrayList<Impression>();
    ArrayList<Impression> tempImpressions = new ArrayList<Impression>();
    Impression tempImpression;
    ArrayList<ImpressionTexture> impressionTextures = new ArrayList<ImpressionTexture>();
    ArrayList<ImpressionTexture> filteredTextures = new ArrayList<ImpressionTexture>();
    ArrayList<ArrayList<ImpressionTexture>> stackPointer = new ArrayList<ArrayList<ImpressionTexture>>();
    int texture2;
    RadarTexture radar;
    boolean add;
    private SurfaceTexture mCameraSurface;
    private Vector3 camRot = new Vector3();
    private Trig3 camTrig = new Trig3();
    private Vector3 camRotDegree = new Vector3();
    private Vector1 screenRot = new Vector1();
    private Trig1 screenRotTrig = new Trig1();
    private OrientationDevice orientation;
    private int phoneRotation = 0;
    private int focusedStack;
    private float touchX;
    private float touchY;
    private boolean flagNewMessage;
    private ArrayList<ImpressionTexture> toBeUpdated = new ArrayList<ImpressionTexture>();

    public ArSurfaceView(Context context, MyCamera myCamera) {

        super(context);
        mContext = context;
        mCamera = myCamera;
        setEGLContextClientVersion(2);
        setOnTouchListener(this);
        setRenderer(this);

        for (int i = 0; i < 8; i++) {
            stackPointer.add(new ArrayList<ImpressionTexture>());
        }

    }


//Rendere Method Implemented

    public ArSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mCamera = new MyCamera();
        setEGLContextClientVersion(2);
        setOnTouchListener(this);
        setRenderer(this);

        for (int i = 0; i < 8; i++) {
            stackPointer.add(new ArrayList<ImpressionTexture>());
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        // btmp.recycle();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "in on surface chnged");
        this.width = width;
        this.height = height;
        GLES20.glViewport(0, 0, width, height);
        mCamera.setParameters(width, width);
        initializeTextures();
        mCamera.openCam(mCameraSurface, Camera.CameraInfo.CAMERA_FACING_BACK);
        setIdentityM(projectionMatrix, 0);
        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;

        if (width > height) {
            // Landscape
            orthoM(ortho, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            // Portrait or square
            orthoM(ortho, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
        radar = new RadarTexture(mContext);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        preRender();
        // if(!isTextureLoaded)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glDisable(GL_DEPTH_TEST);
        // gl.glEnable(GL10.GL_CULL_FACE);
        //gl.glCullFace(GL10.GL_BACK);
        GLES20.glClearColor(0.0f, 0.0f, 0f, 0f);
        setIdentityM(modelMatrix, 0);

        mCameraSurface.getTransformMatrix(mtx);
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 0, 0f, 0f, 3f, 0f, 1.0f, 0.0f);
        rotateM(viewMatrix, 0, 1 * (float) camRotDegree.x, 1, 0, 0);
        rotateM(viewMatrix, 0, (float) camRotDegree.y, 0, 1, 0);
        drawCameraSurface();
        GLES20.glEnable(GL_DEPTH_TEST);
        drawImpressions();
        drawRadar();

        if (flagNewMessage) {
            synchronized (toBeUpdated) {
                for (int i = 0; i < toBeUpdated.size(); i++) {
                    toBeUpdated.get(i).loadTexture2();
                    flagNewMessage = false;
                    toBeUpdated.clear();
                }
            }
        }
    }

    private void drawRadar() {
        if (ArBrowserFragment.flagControlVisible)
            radar.draw(ortho, false, getCurrentDirection(), impressions);

        else
            radar.draw(ortho, true, getCurrentDirection(), impressions);
    }

    // General use Method
    private void drawImpressions() {
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 50f);
        for (int i = 0; i < impressionTextures.size(); i++) {
            impressionTextures.get(i).draw(projectionMatrix, viewMatrix);
        }
    }

    private void drawCameraSurface() {
        mCameraSurface.getTransformMatrix(mtx);
        setIdentityM(temp2, 0);
        mCameraSurface.updateTexImage();
        rotateM(temp2, 0, -90, 0f, 0f, 1f);
        liveCamerTexture.draw(mtx, temp2);
    }

    public void addImpreession(final Impression impression) {

        tempImpression = impression;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                impressions.add(tempImpression);
                ImpressionTexture impt = new ImpressionTexture(tempImpression, mContext);
                impt.setContext(mContext);
                impressionTextures.add(impt);
                ImpressionTexture impt2;
                int num = (impt.clubed_direction_no) % 8;
                if (stackPointer.get(num).size() != 0) {
                    impt2 = stackPointer.get(num).get(stackPointer.get(num).size() - 1);
                    Log.d(TAG, "position is " + impt2.getStackPosition());
                    impt.setStackPosition(impt2.getStackPosition() + 1);
                }
                stackPointer.get((impt.clubed_direction_no) % 8).add(impt);

                // toBeUpdated.add(impt);

            }
        });
    }

    public void essionArray(ArrayList<Impression> impressions1) {
        tempImpressions = impressions1;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                impressions.addAll(tempImpressions);
                for (Impression imp : tempImpressions) {
                    addImpreession(imp);
                }
            }
        });

    }

    public void replaceImpressions(ArrayList<Impression> impressions1) {
        this.impressionTextures.clear();
        impressions = impressions1;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                for (Impression imp : tempImpressions) {
                    addImpreession(imp);
                }
            }
        });
    }

    public void filterImpressions(String query) {
        //TODO : coplite it later
    }

    private int createCameraTexture() {
        int[] textures = new int[1];

        // generate one texture pointer and bind it as an external texture.
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);

        // No mip-mapping with camera source.
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        // Clamp to edge is only option.
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        Logger.d("Enroot", "TextureId: " + textures[0]);
        return textures[0];
    }


// On touch Method Implementation

    public void initializeTextures() {
        int texture = createCameraTexture();
        liveCamerTexture = new DirectVideo(texture);
        mCameraSurface = new SurfaceTexture(texture);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "event is :" + event.toString());
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            focusedStack = ((int) ((((getCurrentDirection() + ImpressionTexture.CLUB_ANGLE / 2) + 360) % 360) / ImpressionTexture.CLUB_ANGLE)) % 8;
            // Log.d(TAG , "focused stack " +focusedStack);
            this.touchX = event.getX();
            this.touchY = event.getY();
            add = true;
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // Log.d(TAG , "focused stack " +focusedStack);
            if (containerFragment != null)
                if (add) {
                    containerFragment.touch(event);
                }
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            add = false;
            if ((event.getHistorySize() >= 1)) {
                Log.d(TAG, " moved by " + (event.getHistoricalY(0) - event.getY()));
                float diff = event.getHistoricalY(0) - event.getY();
                if (stackPointer.get(focusedStack).size() != 0) {
                    if ((stackPointer.get(focusedStack).get(0).stackPosition > 0) && (diff > 0)) {
//                       for (int i = 0; i < stackPointer.get(focusedStack).size(); i++) {
//                           stackPointer.get(focusedStack).get(i).stackPosition = stackPointer.get(focusedStack).get(i).stackPosition - stackPointer.get(focusedStack).get(0).stackPosition;
//                       }
                        return true;
                    }
                    if ((stackPointer.get(focusedStack).get(stackPointer.get(focusedStack).size() - 1).stackPosition < 0) && (diff < 0)) {
//                       for (int i = 0; i < stackPointer.get(focusedStack).size(); i++) {
//                           stackPointer.get(focusedStack).get(i).stackPosition = stackPointer.get(focusedStack).get(i).stackPosition - stackPointer.get(focusedStack).get(stackPointer.get(focusedStack).size() - 1).stackPosition;
//                       }
                        return true;
                    }
                }
                for (int i = 0; i < stackPointer.get(focusedStack).size(); i++) {
                    stackPointer.get(focusedStack).get(i).stackPosition = stackPointer.get(focusedStack).get(i).stackPosition + (event.getHistoricalY(0) - event.getY()) / 100f;
                }
            }
            return true;

        }
        return true;
    }


    public void scrollStackBy(float scroll) {

    }

    //Device rotation and orientation setter
    public OrientationDevice getOrientation() {
        return orientation;
    }

    public void setOrientation(OrientationDevice orientation) {
        this.orientation = orientation;

        this.invalidate();

    }

    public int getPhoneRotation() {
        return phoneRotation;
    }

    public void setPhoneRotation(int phoneRotation) {
        this.phoneRotation = phoneRotation;
    }

    protected void preRender() {

        Math3dUtil.getCamRotation(getOrientation(), getPhoneRotation(), camRot,
                camTrig, screenRot, screenRotTrig);
        camRotDegree.x = Math.toDegrees(camRot.x);
        camRotDegree.y = Math.toDegrees(camRot.y);
        camRotDegree.z = Math.toDegrees(camRot.z);

    }


    //View Method override
    @Override
    public void onPause() {
        mCamera.stop();
    }

    @Override
    public void onResume() {


    }

    public float getCurrentDirection() {

        return (float) camRotDegree.y;
    }


    public void addImpressionInRendererThread(ImpressionTexture imp) {
        this.flagNewMessage = true;
        toBeUpdated.add(imp);
    }


}
