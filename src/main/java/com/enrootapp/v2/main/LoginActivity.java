package com.enrootapp.v2.main;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInstaller;
import android.os.Bundle;
import android.view.View;

import com.enrootapp.v2.main.app.EnrootActivity;
import com.enrootapp.v2.main.app.EnrootApp;
import com.enrootapp.v2.main.data.GeoName;
import com.enrootapp.v2.main.data.Impression;
import com.enrootapp.v2.main.util.Logger;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
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
public class LoginActivity extends EnrootActivity {

    private static String TAG = "LOGIN ACTIVITY";
    private LoginFragment loginFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login2);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /*findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logIn(Arrays.asList("email", ParseFacebookUtils.Permissions.Friends.ABOUT_ME , "user_photos"),
                        LoginActivity.this, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException err) {
                                if (err != null) Logger.d(TAG, "Error in parse login", err);
                                else {
                                    if (user != null) {
                                        ;
                                    } else {
                                        Logger.d(TAG, "User is null.");
                                    }
                                }
                            }
                        });
            }
        });*/
    }




}
