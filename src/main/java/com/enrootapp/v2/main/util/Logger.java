package com.enrootapp.v2.main.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rmuttineni on 7/09/2014.
 */
public class Logger {
    public static StringBuilder log = new StringBuilder();

    public static void d(String tag, String message) {
        Log.d(tag, message);
        log.append(getCurrentTimeStamp() + " " + tag + ": " + message + "\n");
    }

    public static void d(String tag, String where, Exception e) {
        StringBuilder sb = new StringBuilder("EXCEPTION@" + where + ": " + e.toString() + " " + e.getMessage() + "\n");
        for (StackTraceElement ste : e.getStackTrace()) {
            sb.append(ste.toString() + "\n");
        }
        d(tag, sb.toString());
    }

    public static String getCurrentTimeStamp() {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static void persist() {
        if (log.toString().length() <= 0) return;
        File logf = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/log.txt");

        try {
            FileWriter fw = new FileWriter(logf, true);
            fw.append(log.toString());
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.delete(0, log.length() - 1);
    }
}