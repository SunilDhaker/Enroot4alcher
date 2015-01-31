package com.enrootapp.v2.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enrootapp.v2.main.app.EnrootFragment;
import com.enrootapp.v2.main.util.Logger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import java.util.Arrays;

/**
 * Created by rmuttineni on 17/01/2015.
 */
public class LoginFragment extends EnrootFragment {
    LoginButton authButton;
    TextView waiting;
    private UiLifecycleHelper uiHelper;

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

            authButton.setVisibility(View.INVISIBLE);
            waiting.setVisibility(View.VISIBLE);

        }
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
