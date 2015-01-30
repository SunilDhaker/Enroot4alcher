package com.enrootapp.v2.main;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.enrootapp.v2.main.app.EnrootActivity;
import com.enrootapp.v2.main.tabs.ArBrowserFragment;

/**
 * Created by sdhaker on 25-01-2015.
 */
public class TabContainerActivity extends EnrootActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_container);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.tabs_container, new ArBrowserFragment());
    }
}
