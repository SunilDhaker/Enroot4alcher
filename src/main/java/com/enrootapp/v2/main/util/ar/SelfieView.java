package com.enrootapp.v2.main.util.ar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.enrootapp.v2.main.app.EnrootActivity;
import com.enrootapp.v2.main.app.EnrootApp;
import com.enrootapp.v2.main.appunta.android.math3d.Math3dUtil;
import com.enrootapp.v2.main.appunta.android.math3d.Trig1;
import com.enrootapp.v2.main.appunta.android.math3d.Trig3;
import com.enrootapp.v2.main.appunta.android.math3d.Vector1;
import com.enrootapp.v2.main.appunta.android.math3d.Vector3;
import com.enrootapp.v2.main.appunta.android.orientation.OrientationDevice;
import com.enrootapp.v2.main.util.FileUtils;
import com.enrootapp.v2.main.util.Logger;
import com.enrootapp.v2.main.util.TestUtil;
import com.enrootapp.v2.main.util.graphics.DirectVideo;
import com.enrootapp.v2.main.util.graphics.MatrixHelper;
import com.enrootapp.v2.main.util.graphics.SqureEmpty;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * Created by sdhaker on 10-01-2015.
 */
public class SelfieView extends GLSurfaceView implements GLSurfaceView.Renderer, View.OnTouchListener {

    private final static String LOG_TAG = "SelfieView";
    private final Activity mContext;
    public float picX = 0f, picZ = 0f, picAzimuth = 0.0f;
    float[] mtx = new float[16];
    float[] projectionMatrix = new float[16];
    float[] modelMatrix = new float[16];
    float[] viewMatrix = new float[16];
    float[] temp = new float[16];
    float[] temp2 = new float[16];
    float cout = -1.0f;
    int count = 0;
    double last = 0.0;
    float aspectRatio;
    private float[] mtxBack = new float[16];
    private MyCamera mCamera;
    private SurfaceTexture mSurface;
    private SurfaceTexture mBackSurface;
    private DirectVideo mDirectVideo;
    private DirectVideo mDirectVideoBack;
    private int width;
    private int height;
    private boolean capturing = false;
    private boolean capture = false;
    private boolean animate = false;
    private Bitmap snap;
    private boolean initialized = false;
    private View controls;
    private Vector3 camRot = new Vector3();
    private Trig3 camTrig = new Trig3();
    private Vector3 camPos = new Vector3();
    private Vector3 camRotDegree = new Vector3();
    private Vector1 screenRot = new Vector1();
    private Trig1 screenRotTrig = new Trig1();
    private OrientationDevice orientation;
    private int phoneRotation = 0;
    private boolean isTagging = false;
    private float[] ortho = new float[16];
    private SqureEmpty border;

    public SelfieView(Context context, MyCamera camera) {
        super(context);
        mContext = (Activity) context;
        mCamera = camera;
        setEGLContextClientVersion(2);
        setOnTouchListener(this);
        setRenderer(this);
    }

    public SelfieView(Context context, AttributeSet attrs) { /*IMPORTANT - this is the constructor that is used when you send your view ID in the main activity */
        super(context, attrs);
        mContext = (Activity) context;
    }

    public static int loadShader(int type, String shaderCode) {

        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public static void saveBitmapAsFile(Bitmap bitmap) {
        File file = FileUtils.getFile("test.jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Logger.d(LOG_TAG, "Surface Changed");
        if (!initialized) {
            initialized = true;
            this.width = width;
            this.height = height;
            GLES20.glViewport(0, 0, width, height);
            aspectRatio = width > height ?
                    (float) width / (float) height :
                    (float) height / (float) width;
            initializeTextures();
            if (width > height) {
                // Landscape
                orthoM(ortho, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
            } else {
                // Portrait or square
                orthoM(ortho, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
            }

            mCamera.setParameters(width, height);
            if (capture) {
                //   Logger.d(LOG_TAG, "Back cam");
                mCamera.openCam(mSurface, Camera.CameraInfo.CAMERA_FACING_BACK);
            } else {
                // Logger.d(LOG_TAG, "Front cam");
                if (EnrootActivity.isfrontCameraAvailable)
                    mCamera.openCam(mSurface, Camera.CameraInfo.CAMERA_FACING_FRONT);
                else
                    mCamera.openCam(mSurface, Camera.CameraInfo.CAMERA_FACING_BACK);
            }
            setIdentityM(projectionMatrix, 0);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (EnrootActivity.isCompassAvailable)
            preRender();
        //orthoM(ortho, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        MyData.setGL10(gl);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 1f, 1f);
        setIdentityM(modelMatrix, 0);
        mSurface.getTransformMatrix(mtx);
        mBackSurface.getTransformMatrix(mtxBack);
        drawBackCamera();

        if (animate) {

            if (SelfieActivity.CURENT_SCREEN == SelfieActivity.TAG_SCREEN) {
                drawTagging();
            } else {
                drawFeedBack();
            }
        } else {
            drawSelfieCamera();
        }
        if (capture && !capturing) {
            capturing = true;
            GLES20.glFlush();
            picZ = (float) camTrig.yCos;
            picX = (float) camTrig.ySin;
            picAzimuth = (float) camRotDegree.y;
            snap = TestUtil.squureCropeSelfie(createBitmapFromGLSurface(0, 0, width, height));
            saveBitmapAsFile(snap);
            EnrootApp.getInstance().setSelfie(snap);
            mCamera.openCam(mBackSurface, Camera.CameraInfo.CAMERA_FACING_BACK);
            animate = true;
            ((SelfieActivity) mContext).setFlagCaptured();
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    border = new SqureEmpty(mContext);
                }
            });
        }

    }

    public void drawSelfieCamera() {
        //rotateM(ortho, 0, 180, 0f, 1f, 0f);
        setIdentityM(modelMatrix, 0);
        rotateM(modelMatrix, 0, -90, 0f, 0f, 1f);
        translateM(modelMatrix, 0, -(aspectRatio - 1) + 0.01f + 0.3f, 0f, 0);
        mSurface.updateTexImage();
        multiplyMM(temp, 0, ortho, 0, modelMatrix, 0);
        mDirectVideo.draw(mtx, temp);
    }

    public void drawTagging() {

        setIdentityM(modelMatrix, 0);
        rotateM(modelMatrix, 0, -90, 0f, 0f, 1f);
        translateM(modelMatrix, 0, -(aspectRatio - 1) + 0.3f, 0f, 0);
        //mSurface.updateTexImage();
        multiplyMM(temp, 0, ortho, 0, modelMatrix, 0);
        mDirectVideo.draw(mtx, temp);
    }

    public void drawFeedBack() {
        GLES20.glEnable(GL_DEPTH_TEST);

        translateM(modelMatrix, 0, -(cout) * picX, 0.3f, (cout) * picZ);
        rotateM(modelMatrix, 0, -180 - picAzimuth, 0, 1, 0);
        cout = Math.min(8f, cout + .5f);
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 0, 0f, 0f, 3f, 0f, 1.0f, 0.0f);
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 0f, 50f);
        rotateM(viewMatrix, 0, (float) camRotDegree.x, 1f, 0f, 0f);
        rotateM(viewMatrix, 0, (float) camRotDegree.y, 0f, 1f, 0f);
        rotateM(modelMatrix, 0, -90, 0f, 0f, 1f);
        multiplyMM(temp2, 0, viewMatrix, 0, modelMatrix, 0);
        multiplyMM(temp, 0, projectionMatrix, 0, temp2, 0);
        mDirectVideo.draw(mtx, temp);
        border.draw(temp);
        GLES20.glDisable(GL_DEPTH_TEST);

    }

    public void drawBackCamera() {
        mBackSurface.updateTexImage();
        setIdentityM(temp2, 0);
        rotateM(temp2, 0, -90, 0f, 0f, 1f);
        mDirectVideoBack.draw(mtxBack, temp2);
    }

    @Override
    public void onPause() {
        super.onPause();
        //Logger.d(LOG_TAG, "glview onPause");
        initialized = false;
        mCamera.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Logger.d(LOG_TAG, "glview onresume");

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return true;
    }

    public void initializeTextures() {
        int texture = createTexture();
        int backTexture = createTexture();
        mDirectVideo = new DirectVideo(texture, (0.4f));
        mDirectVideoBack = new DirectVideo(backTexture);
        mSurface = new SurfaceTexture(texture);
        mBackSurface = new SurfaceTexture(backTexture);

    }

    private int createTexture() {
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

    private Bitmap createBitmapFromGLSurface(int x, int y, int w, int h)
            throws OutOfMemoryError {
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            GLES20.glReadPixels(x, y, w, h, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            return null;
        }
        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }

    public void setControls(View view, GLSurfaceView glView) {

        this.controls = view;

    }

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
        count++;
        Math3dUtil.getCamRotation(getOrientation(), getPhoneRotation(), camRot,
                camTrig, screenRot, screenRotTrig);
        camRotDegree.x = Math.toDegrees(camRot.x);
        camRotDegree.y = Math.toDegrees(camRot.y);
        camRotDegree.z = Math.toDegrees(camRot.z);
    }

    public void switchCam() {
        mCamera.switchCam();
    }

    public void capture(boolean b) {

        if (!capture) {
            mCamera.stop();
            capture = true;

        }
    }

}
