package com.enrootapp.v2.main.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by rmuttineni on 27/01/2015.
 */
public class DateUtil {
    public static Date fromJSON(String timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(timestamp));
        return cal.getTime();
    }

    public static String elapsedTime(long millis) {
        if (millis >= 24 * 60 * 60 * 1000) {
            return (millis / (24 * 60 * 60 * 1000)) + " days ago";
        } else if (millis >= 3600000) {
            return (millis / 3600000) + "hrs ago";
        } else if (millis >= 60000) {
            return (millis / 60000) + "min ago";
        } else if (millis >= 1000) {
            return (millis / 1000) + "s ago";
        } else {
            return "Now";
        }
    }
}
