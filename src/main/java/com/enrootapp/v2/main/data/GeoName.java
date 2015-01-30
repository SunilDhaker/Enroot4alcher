package com.enrootapp.v2.main.data;


import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/**
 * Created by rmuttineni on 21/01/2015.
 */

@ParseClassName("geonames")
public class GeoName extends ParseObject{
    public static final String TAG = "GeoName";

    public GeoName() {
    }


    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name" , name);
    }

    public String getId() {
        return getString("id");
    }

    public void setId(String Id) {
        put("id" , Id);
    }

    public ParseGeoPoint getCoordinates() {
        return getParseGeoPoint("coordinates");
    }

    public void setCoordinates(ParseGeoPoint coordinates) {
        put("coordinates" , coordinates);
    }


}
