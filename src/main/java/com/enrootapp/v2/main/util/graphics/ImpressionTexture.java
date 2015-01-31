package com.enrootapp.v2.main.util.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import com.enrootapp.v2.main.data.Impression;
import com.enrootapp.v2.main.util.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLUtils.texImage2D;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * Created by sdhaker on 18-01-2015.
 */


public class ImpressionTexture {
    public static final float CLUB_ANGLE = 45f;
    // number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 3;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private static final String TAG = "ImpressionTexture";
    private static final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 u_Matrix;" +
                    "attribute vec2 inputTextureCoordinate;" +
                    "varying vec2 textureCoordinate;" +
                    "void main()" +
                    "{" +
                    "gl_Position = u_Matrix * vPosition;" +
                    "textureCoordinate = inputTextureCoordinate;" +
                    "}";
    private static final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec2 textureCoordinate;\n" +
                    "uniform sampler2D s_texture;\n" +
                    "void main() {" +
                    "  gl_FragColor = texture2D( s_texture, textureCoordinate );\n" +
                    "}";
    private final int mProgram;
    public float direction = 0.0f;
    public int clubed_direction_no = 0;
    public float stackPosition = 0;
    public int distance = 8;
    float squareCoords[] = {
            -1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f
    };
    float textureVertices[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
    };
    float[] temp = new float[16];
    float[] temp2 = new float[16];
    float[] modalMatrix = new float[16];
    Context c;
    //Impression propeties
    Impression impression;
    private FloatBuffer vertexBuffer, textureVerticesBuffer;
    private ShortBuffer drawListBuffer;
    private int mPositionHandle;
    private int mTextureCoordHandle;
    private int uMatrixHandler;
    private float[] uMat = new float[16];
    private short drawOrder[] = {0, 1, 2, 0, 2, 3}; // order to draw vertices
    private int texture;

    public ImpressionTexture(Impression imp) {
        impression = imp;
        this.direction = imp.getDirection();
        this.clubed_direction_no = (int) (imp.getDirection() / CLUB_ANGLE);

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        ByteBuffer bb2 = ByteBuffer.allocateDirect(textureVertices.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        textureVerticesBuffer = bb2.asFloatBuffer();
        textureVerticesBuffer.put(textureVertices);
        textureVerticesBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // creates OpenGL ES program executables
        if (impression.getImpression() != null)
            loadTexture2();


    }

    public ImpressionTexture(Impression imp, Context mContext) {
        this.c = mContext;
        impression = imp;
        this.direction = imp.getDirection();
        this.clubed_direction_no = (int) (imp.getDirection() / CLUB_ANGLE);

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        ByteBuffer bb2 = ByteBuffer.allocateDirect(textureVertices.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        textureVerticesBuffer = bb2.asFloatBuffer();
        textureVerticesBuffer.put(textureVertices);
        textureVerticesBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // creates OpenGL ES program executables
        if (impression.getImpression() != null)
            loadTexture2();


    }

    public static int loadShader(int type, String shaderCode) {

        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public float getStackPosition() {
        return stackPosition;
    }

    public void setStackPosition(float stackPosition) {
        this.stackPosition = stackPosition;
    }

    public void setContext(Context c) {
        this.c = c;
    }

    public void draw(float[] projectionMat, float[] viewMatrix) {
        GLES20.glUseProgram(mProgram);

        setIdentityM(modalMatrix, 0);

//        translateM(modalMatrix , 0 , (float)(distance*Math.sin(direction)) , 0 , (float)(distance*Math.cos(direction)));
        rotateM(modalMatrix, 0, -clubed_direction_no * 45f, 0, 1, 0);
        translateM(modalMatrix, 0, 0, 0, distance);
        translateM(modalMatrix, 0, 0, getStackPosition() * 0.20f, Math.abs(getStackPosition()) * 0.25f);
        rotateM(modalMatrix, 0, 180, 0, 0, 1);
        multiplyMM(temp2, 0, viewMatrix, 0, modalMatrix, 0);
        multiplyMM(temp, 0, projectionMat, 0, temp2, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        uMatrixHandler = GLES20.glGetUniformLocation(mProgram, "u_Matrix");


        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        glUniformMatrix4fv(uMatrixHandler, 1, false, temp, 0);
        // Prepare the <insert shape here> coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);

        GLES20.glVertexAttribPointer(mTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 8, textureVerticesBuffer);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
    }

    public void loadTexture2() {
        texture = loadTexture(impression.getImpression());
    }

    public int loadTexture(Bitmap bitmap) {
        int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);
        if (textureObjectIds[0] == 0) {
            //if (LoggerConfig.ON) {
            Logger.d(TAG, "Could not generate a new OpenGL texture object.");
            //}
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // Load the bitmap into the bound texture.
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
        bitmap.recycle(); //TODO remove it later
        return textureObjectIds[0];
    }

}