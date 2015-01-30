package com.enrootapp.v2.main.util.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.util.Log;

import com.enrootapp.v2.main.R;
import com.enrootapp.v2.main.data.Impression;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
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


public class RadarTexture {
    // number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 2;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private static final String TAG = "RadarTexture";
    private static final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 u_Matrix;" +
                    "attribute vec2 inputTextureCoordinate;" +
                    "varying vec2 textureCoordinate;" +
                    "void main()" +
                    "{" +
                    "gl_Position = u_Matrix * vPosition;" +
                    "textureCoordinate = inputTextureCoordinate;" +
                    "gl_PointSize = 10.0; }";
    private static final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec2 textureCoordinate;\n" +
                    "uniform sampler2D s_texture;\n" +
                    "void main() {" +
                    "  gl_FragColor = texture2D( s_texture, textureCoordinate );\n" +
                    "}";

    private final int mProgram;
    float squareCoords[];
    float textureVertices[];
    float[] temp = new float[16];
    float[] temp2 = new float[16];
    float[] modalMatrix = new float[16];
    Context c;
    private FloatBuffer vertexBuffer, textureVerticesBuffer;
    private ShortBuffer drawListBuffer;
    private int mPositionHandle;
    private int mTextureCoordHandle;
    private int uMatrixHandler;
    private float[] uMat = new float[16];
    private short drawOrder[]; // order to draw vertices
    private int texture;


    public RadarTexture(Context context) {
        this.c = context;
        makeCircle2d(360);
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
        texture = loadTexture(c, R.drawable.radar);


    }

    public static int loadTexture(Context context, int resourceId) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            Log.w(TAG, "Could not generate a new OpenGL texture object.");
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        // Read in the resource
        final Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resourceId, options);

        if (bitmap == null) {
            Log.w(TAG, "Resource ID " + resourceId + " could not be decoded.");
            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // Load the bitmap into the bound texture.
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        glGenerateMipmap(GL_TEXTURE_2D);
        bitmap.recycle();
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureObjectIds[0];
    }

    public static int loadShader(int type, String shaderCode) {

        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public void setContext(Context c) {
        this.c = c;
    }


    public void draw(float[] ortho, boolean isFullScreen, float direction, ArrayList<Impression> impressions) {
        GLES20.glUseProgram(mProgram);
        setIdentityM(modalMatrix, 0);
        if (isFullScreen)
            translateM(modalMatrix, 0, -0.7f, 1.0f, 0);
        else translateM(modalMatrix, 0, -0.7f, 0.7f, 0);
        rotateM(modalMatrix, 0, direction, 0, 0, 1);
        multiplyMM(temp, 0, ortho, 0, modalMatrix, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        // setIdentityM(temp , 0 );
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
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);

        GLES20.glDrawArrays(GL_POINTS, 0, 1);
        //GLES20.glEnable(POINT);
    }

    public void makeCircle2d(int points) {
        float[] verts = new float[points * 2 + 2];
        float[] txtcord = new float[points * 2 + 2];
        drawOrder = new short[points + 2];

        verts[0] = 0;
        verts[1] = 0;
        txtcord[0] = 0.5f;
        txtcord[1] = 0.5f;
        int c = 2;
        int j = 0;
        for (int i = 0; i < points; i++) {
            float fi = (float) (2 * Math.PI * (float) i / (float) points);
            float x = (float) Math.cos(fi + Math.PI);
            float y = (float) Math.sin(fi + Math.PI);

            verts[c] = x * 0.2f;
            verts[c + 1] = y * 0.2f;
            txtcord[c] = x * 0.5f + 0.5f;//scale the circle to 0.5f radius and plus 0.5f because we want the center of the circle tex cordinates to be at 0.5f,0.5f
            txtcord[c + 1] = y * 0.5f + 0.5f;
            drawOrder[j] = (short) j;
//            drawOrder[j+1] =(short) (c/2) ;
//            drawOrder[j+2] =(short) (c/2 + 1);

            c += 2;
            j += 1;
        }
        drawOrder[j] = (short) j;
        drawOrder[j + 1] = (short) 1;
        squareCoords = verts;
        textureVertices = txtcord;
    }

}