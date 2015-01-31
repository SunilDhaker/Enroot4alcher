package com.enrootapp.v2.main.util;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.enrootapp.v2.main.R;
import com.enrootapp.v2.main.app.EnrootActivity;
import com.enrootapp.v2.main.data.GeoName;
import com.enrootapp.v2.main.util.ar.SelfieActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sdhaker on 23-01-2015.
 */
public class SelectLocationActivity extends EnrootActivity implements LocationListener {

    private static final int MIN_TIME = 100;
    private static final int MIN_DISTANCE = 100;
    public boolean isFoursquareReturned = false;
    public boolean isEnrootBackendreturned = false;
    LocationAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    RecyclerView rv;
    HashMap<String, GeoName> comb = new HashMap<String, GeoName>();
    ArrayList<GeoName> locs = new ArrayList<GeoName>();
    private LocationManager locationMgr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.select_location);
        rv = (RecyclerView) findViewById(R.id.select_location_item_container);
        rv.setHasFixedSize(true);
        mAdapter = new LocationAdapter();
        rv.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(mLayoutManager);
        // getRequestedOrientation(ORIent)
        rv.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View v = rv.findChildViewUnder(e.getX(), e.getY());
                if (v != null) {
                    int pos = rv.getChildPosition(v);
                    synchronized (locs) {
                        selectLoc(pos);
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }
        });

        Toast.makeText(getApplication(), "Loading location nearby ", Toast.LENGTH_LONG).show();
        comb.clear();
        fetchFourSquare(EnrootActivity.currentLoc);
        fetchParse(EnrootActivity.currentLoc);
    }


    private void selectLoc(int pos) {

        Intent i = new Intent(this, SelfieActivity.class);
        startActivity(i);
    }


    public void fetchFourSquare(final Location location) {
        Ion.with(this)
                .load("https://api.foursquare.com/v2/venues/search?client_id=DIQIR4HISXC0XWJ4K0JTRC1OX5YTUXZQWO3JSRKNT52GAI2X&client_secret=F0J4P4EVD0O0OFODCI5DVJOD1WQ1N2MUFDYRPW5OHVRCXIMT&v=20130815&ll=" + location.getLatitude() + "," + location.getLongitude() + "&radius=25")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if (e == null) {
                            JsonObject response = result.getAsJsonObject("response");
                            JsonArray venues = response.getAsJsonObject().getAsJsonArray("venues");
                            for (int i = 0; i < venues.size(); i++) {
                                Log.d(TAG, venues.get(i).getAsJsonObject().get("name").toString() + "\n");
                                GeoName loc = new GeoName();
                                loc.setName(TestUtil.trimQuotes(venues.get(i).getAsJsonObject().get("name").toString()));
                                loc.setId(TestUtil.trimQuotes(venues.get(i).getAsJsonObject().get("id").toString()));
                                JsonObject js = venues.get(i).getAsJsonObject().get("location").getAsJsonObject();
                                LatLng ll = new LatLng(
                                        Double.parseDouble(TestUtil.trimQuotes(js.get("lat").toString())),
                                        Double.parseDouble(TestUtil.trimQuotes(js.get("lng").toString())));
                                loc.setCoordinates(new ParseGeoPoint(ll.latitude , ll.longitude));
                                Log.d(TAG, ll.toString());
                                comb.put(loc.getObjectId(), loc);
                            }

                            locs = new ArrayList<GeoName>(comb.values());
                            mAdapter.mDataset = locs;
                            mAdapter.notifyDataSetChanged();
                            isFoursquareReturned = true;

                        } else {
                            Logger.d(TAG, "FourSquare exception.", e);
                            //Toast.makeText(getApplication(), "problem :" + e.getMessage(), Toast.LENGTH_LONG).show();
                            //fetchFourSquare(location);
                        }
                    }
                });
    }


    public void fetchParse(Location location){

        ParseQuery<GeoName> query = ParseQuery.getQuery(GeoName.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.whereWithinKilometers("coordinates" ,new ParseGeoPoint(location.getLatitude() , location.getLongitude()) , 0.5 );

        query.findInBackground(new FindCallback<GeoName>() {
            @Override
            public void done(List<GeoName> geoNames, ParseException e) {
                synchronized (comb) {
                    for (GeoName g : geoNames) {
                        comb.put(g.getId(), g);
                    }
                }
            }
        });
    }

}
