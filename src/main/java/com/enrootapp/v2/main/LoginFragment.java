package com.enrootapp.v2.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enrootapp.v2.main.app.EnrootApp;
import com.enrootapp.v2.main.app.EnrootFragment;
import com.enrootapp.v2.main.data.GeoName;
import com.enrootapp.v2.main.data.Impression;
import com.enrootapp.v2.main.util.Logger;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by rmuttineni on 17/01/2015.
 */
public class LoginFragment extends EnrootFragment {
    LoginButton authButton;
    TextView waiting;
    private UiLifecycleHelper uiHelper;
    ArrayList<Impression> imps = new ArrayList<Impression>();
    ArrayList<GeoName> gname = new ArrayList<GeoName>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate");
        uiHelper = new UiLifecycleHelper(getActivity(), new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChange(session, state, exception);
            }
        });
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.login, container, false);
        authButton = (LoginButton) view.findViewById(R.id.authButton);
        waiting = (TextView) view.findViewById(R.id.wait_text);
        waiting.setVisibility(View.GONE);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("email", "user_hometown",
                 "user_tagged_places", "user_location", "user_photos"));
        return view;
    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        Logger.d(TAG, "onSessionStateChange");
        if (state.isOpened() ) {
            Logger.d(TAG, "Logged in...");
            if (ParseUser.getCurrentUser() == null) {
                getInfo(session);
            }



        }
    }

    public void getInfo(final Session session){
        Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (user != null) {
                    // Display the parsed user info
                    Logger.d(TAG, "Response : " + response);
                    Logger.d(TAG, "UserID : " + user.getId());
                    Logger.d(TAG, "User FirstName: " + user.getName());

                    EnrootApp.getInstance().setFbId(user.getId());
                    EnrootApp.getInstance().setFbName(user.getName());
                    getFbImpressions(session);
                }
            }
        }).executeAsync();
    }

    public void getFbImpressions(Session session) {
        new Request(
                session,
                "me/photos?fields=picture,place,name",
                null,
                HttpMethod.GET,
                new Request.Callback() {
                    @Override
                    public void onCompleted(Response response) {
                        try {

                            GraphObject go = response.getGraphObject();
                            JSONObject jso = go.getInnerJSONObject();
                            JSONArray data = jso.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject photo = data.getJSONObject(i);
                                JSONObject place = photo.getJSONObject("place");
                                String name = place.getString("name");
                                JSONObject location = place.getJSONObject("location");
                                double latitude = location.getDouble("latitude");
                                double longitude = location.getDouble("longitude");
                                String id = place.getString("id");
                                //Create the impressions here
                                String caption = photo.getString("caption");
                                String image = photo.getString("picture");
                                Impression imp = new Impression();
                               // ParseUser current = ParseUser.getCurrentUser();
                               // imp.setOwner(current);
                                imp.setOwnerId(mApp.getFbId());
                                imp.setOwnerName(mApp.getFbName());
                                imp.setDirection((int) (Math.random() * 360));
                                imp.setTimestamp(new Date(System.currentTimeMillis()));
                                imp.setCaption(caption);
                                imp.setImageUrl(image);
                                imp.isFromFacebook(true);
                                imp.setType(0);

                                GeoName g = new GeoName();
                                g.setName(name);
                                g.setId(id);
                                g.setCoordinates(new ParseGeoPoint(latitude, longitude));
                                imp.setGeoname(g);
                                imps.add(imp);
                                gname.add(g);
                            }

                            ParseUser.saveAllInBackground(gname, new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null) Logger.d(TAG, "Failed to save geonames.", e);
                                    else {
                                        ParseUser.saveAllInBackground(imps, new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e != null) Logger.d(TAG, "Failed to save geonames.", e);
                                                else {
                                                    Logger.d(TAG, "Successfully saved all impressions + geonames.");
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        } catch (Exception e) {
                            Logger.d(TAG, "Failed to create impression from facebook.", e);
                        }
                    }
                }
        ).executeAsync();

    }


    @Override
    public void onResume() {
        super.onResume();
        Logger.d(TAG, "onResume");
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.d(TAG, "onPause");
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
}
