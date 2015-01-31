package com.enrootapp.v2.main.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

/**
 * Created by rmuttineni on 29/01/2015.
 */
public class Resources {
    private static final String TAG = "Resources";

    public static Bitmap loadBitmap(File f) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 6;
        return BitmapFactory.decodeFile(f.getAbsolutePath(), option);
    }
}
