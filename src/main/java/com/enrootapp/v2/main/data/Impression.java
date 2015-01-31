package com.enrootapp.v2.main.data;

import android.graphics.Bitmap;

import com.enrootapp.v2.main.util.FileUtils;
import com.koushikdutta.ion.Ion;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.util.Collection;
import java.util.Date;

/**
 * Created by sdhaker on 15-01-2015.
 */

@ParseClassName("imp")
public class Impression extends ParseObject implements Comparable<Impression> {

    private static final String TAG = "Impression";

    public Impression() {

    }

    public Date getTimestamp() {
        return getDate("timestamp");
    }

    public void setTimestamp(Date timestamp) {
        put("timestamp",timestamp);
    }

    @Override
    public int compareTo(Impression impression) {
        return getObjectId().compareTo(impression.getObjectId());
    }

    public GeoName getGeoname() {
        return (GeoName)get("geoname");
    }

    public void setGeoname(GeoName geoname) {
        put("geoname" , geoname );
    }

    public String getCaption() {
        return getString("caption");
    }

    public void setCaption(String caption) {
        put("caption" , caption);
    }


    public float getDirection() {
        return getInt("direction");
    }

    public void setDirection(float direction) {
        put("direction" , (int)direction);
    }

    public int getType() {
        /**
         * 0 is public 1 is private
         */
        return getInt("type");
    }

    public void setType(int type) {
        put("type" ,type);
    }


    public ParseFile getImpression() {
        return getParseFile("impression");
    }

    public void setImpression(ParseFile impression) {
        put("impression" , impression);
    }

    public void isFromFacebook(boolean b) {
        put("isFromFacebook", b);
    }

    public boolean isFromFacebook() {
        return getBoolean("isFromFacebook");
    }

    public String getOwnerName() {
        return getString("owner_name");
    }

    public void setOwnerName(String ownerName) {
        put("owner_name" , ownerName);
        ParseFile ff ;

    }



    public String getOwnerId() {
        return getString("owner_id");
    }

    public void setOwnerId(String ownerName) {
        put("owner_id" , ownerName);
        ParseFile ff ;

    }

    public void setImageUrl(String url) {
        put("imageUrl", url);
    }

    public String getImageUrl() {
        return getString("imageUrl");
    }


    public ParseUser getOwner() {
        return getParseUser("owner");
    }

    public void setOwner(ParseUser owner) {
        put("owner" , owner);

    }

}
