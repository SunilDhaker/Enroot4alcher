package com.enrootapp.v2.main.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import com.enrootapp.v2.main.R;

import java.util.StringTokenizer;

/**
 * Created by rmuttineni on 15/01/2015.
 */
public class TestUtil {


    public static Bitmap GetBitmapClippedCircle(Bitmap bitmap) {

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);

        final Path path = new Path();
        path.addCircle((float) (width / 2), (float) (height / 2),
                (float) Math.min(width, (height / 2) - 5.0), Path.Direction.CCW);

        final Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }


    public static Bitmap GetBitmapClippedSquare(Bitmap bitmap) {

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);

        final Path path = new Path();
        path.addCircle((float) (width / 2), (float) (height / 2),
                (float) Math.min(width, (height / 2) - 5.0), Path.Direction.CCW);

        final Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }

    public static String[] splitIntoLine(String input, int maxCharInLine) {

        StringTokenizer tok = new StringTokenizer(input, " ");
        StringBuilder output = new StringBuilder(input.length());
        int lineLen = 0;
        while (tok.hasMoreTokens()) {
            String word = tok.nextToken();

            while (word.length() > maxCharInLine) {
                output.append(word.substring(0, maxCharInLine - lineLen) + "\n");
                word = word.substring(maxCharInLine - lineLen);
                lineLen = 0;
            }

            if (lineLen + word.length() > maxCharInLine) {
                output.append("\n");
                lineLen = 0;
            }
            output.append(word + " ");

            lineLen += word.length() + 1;
        }
        return output.toString().split("\n");
    }


    public static Bitmap getArImage(Bitmap image, Bitmap userpic ,String userName, Context context) {
        Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_4444);

        Canvas canvas = new Canvas(bitmap);
        Paint pnt = new Paint();
        //
        // get a canvas to paint over the bitmap
        if (image == null) {
            image = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        }
        canvas.drawBitmap(image, null, new Rect(0, 0, 200, 200), pnt);

        // get a background image from resources


        // note the image format must match the bitmap format
        if (userpic == null) {
            userpic = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        }


        canvas.drawBitmap(userpic, null, new Rect(10, 10, 45 + 10, 45 + 10), pnt);

        // Draw the text

        Paint textPaint = new Paint();
        // title
        textPaint.setTextSize(25);
        textPaint.setColor(Color.WHITE);


        canvas.drawText(userName, 60, 35, textPaint);


        // message
//
//        textPaint.setAlpha(150);
//        textPaint.setTextSize(20);
//        textPaint.setAntiAlias(true);
//        textPaint.setARGB(0xff, 2, 0x00, 0x00);
//        textPaint.setDither(true);

        return bitmap;

    }


    public static Bitmap squureCropeSelfie(Bitmap src) {
        Bitmap dstBmp = Bitmap.createBitmap(
                src,
                0,
                56,
                src.getWidth(),
                src.getWidth()
        );
        return dstBmp;
    }

    public static String trimQuotes(String s) {
        if (s.charAt(0) == '"')
            return s.substring(1,s.length() - 1);
        else
            return s;
    }

}
