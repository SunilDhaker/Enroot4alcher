package com.enrootapp.v2.main;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.enrootapp.v2.main.app.EnrootActivity;
import com.enrootapp.v2.main.util.Logger;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

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
        setContentView(R.layout.login3);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logIn(Arrays.asList("email", ParseFacebookUtils.Permissions.Friends.ABOUT_ME),
                        LoginActivity.this, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException err) {
                                // Code to handle login.

                            }
                        });
            }
        });
    }

    private static void getFacebookIdInBackground() {
        Request.executeMeRequestAsync(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (user != null) {
                    ParseUser.getCurrentUser().put("fbId", user.getId());
                    ParseUser.getCurrentUser().saveInBackground();
                }
            }
        });


    }

    public static void getInfo(){
        Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (user != null) {
                    // Display the parsed user info
                    Logger.d(TAG, "Response : " + response);
                    Logger.d(TAG, "UserID : " + user.getId());
                    Logger.d(TAG, "User FirstName: " + user.getName());

                    ParseUser.getCurrentUser().put("fbId", user.getId());
                    ParseUser.getCurrentUser().put("fbName" , user.getFirstName());
                    ParseUser.getCurrentUser().saveInBackground();

                   }
            }

        }).executeAsync();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }
}
