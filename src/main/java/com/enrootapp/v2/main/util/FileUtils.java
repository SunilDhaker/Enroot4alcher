package com.enrootapp.v2.main.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by rmuttineni on 30/01/2015.
 */
public class FileUtils {

    public static File getFile(String filename) {
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/enroot";
        File dir = new File(file_path);
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, filename);
    }

    public static File getFile(String directory, String filename) {
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/enroot/" + directory;
        File dir = new File(file_path);
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, filename);
    }


}