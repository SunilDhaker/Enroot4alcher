package com.enrootapp.v2.main.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by rmuttineni on 17/01/2015.
 */
public class EnrootFragment extends Fragment {
    protected EnrootApp mApp;
    protected EnrootActivity mActivity;
    public String TAG;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (EnrootActivity) getActivity();
        mApp = mActivity.mApp;
        TAG = getClass().getName();
    }
}
