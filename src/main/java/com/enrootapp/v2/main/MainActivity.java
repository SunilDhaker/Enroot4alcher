package com.enrootapp.v2.main;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.balysv.materialripple.MaterialRippleLayout;
import com.enrootapp.v2.main.app.EnrootActivity;


public class MainActivity extends EnrootActivity implements View.OnClickListener {


    public static final int AR = 0, DYNAMIC = 1;
    public static int USER_SELECTION = 0;
    public SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    ActionBar actionBar;
    ImageView tabLive, tabfriends, tabTrails, tabConversation, tabNotif;
    LinearLayout tabContainer;
    ImageView tabs[] = new ImageView[5];
    int blueIcons[] = {R.drawable.live_blue,
            R.drawable.friends_blue,
            R.drawable.trails_blue,
            R.drawable.conversations_blue,
            R.drawable.notifications_blue};
    int greyIcons[] = {R.drawable.live_grey,
            R.drawable.friends_grey,
            R.drawable.trails_grey,
            R.drawable.conversations_grey,
            R.drawable.notifications_grey};
    private int tabPosiotion = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        actionBar = getSupportActionBar();
        actionBar.hide();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setTab(position);
            }
        });


        tabLive = (ImageView) findViewById(R.id.tab_live);
        tabfriends = (ImageView) findViewById(R.id.tab_friends);
        tabTrails = (ImageView) findViewById(R.id.tab_trails);
        tabConversation = (ImageView) findViewById(R.id.tab_conversation);
        tabNotif = (ImageView) findViewById(R.id.tab_notif);
        tabs[0] = tabLive;
        tabs[1] = tabfriends;
        tabs[2] = tabTrails;
        tabs[3] = tabConversation;
        tabs[4] = tabNotif;
        tabContainer = (LinearLayout) findViewById(R.id.tabs_container);

        tabLive.setOnClickListener(this);
        tabfriends.setOnClickListener(this);
        tabTrails.setOnClickListener(this);
        tabConversation.setOnClickListener(this);
        tabNotif.setOnClickListener(this);


        MaterialRippleLayout.on(tabLive).rippleColor(Color.parseColor("#00bcd4"))
                .rippleAlpha(1f)
                .rippleOverlay(true)
                .rippleHover(true)
                .create();
        MaterialRippleLayout.on(tabfriends).rippleColor(Color.parseColor("#00bcd4"))
                .rippleAlpha(1f)
                .rippleOverlay(true)
                .rippleHover(true)
                .create();
        MaterialRippleLayout.on(tabTrails).rippleColor(Color.parseColor("#00bcd4"))
                .rippleAlpha(1f)
                .rippleOverlay(true)
                .rippleHover(true)
                .create();
        MaterialRippleLayout.on(tabConversation).rippleColor(Color.parseColor("#00bcd4"))
                .rippleAlpha(1f)
                .rippleOverlay(true)
                .rippleHover(true)
                .create();
        MaterialRippleLayout.on(tabNotif).rippleColor(Color.parseColor("#00bcd4"))
                .rippleAlpha(1f)
                .rippleOverlay(true)
                .rippleHover(true)
                .create();

        setTab(0);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    public void hideTab() {
        if (tabPosiotion == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tabContainer.setVisibility(View.GONE); //TODO hiding tab lagging app so much
                }
            });
        }
    }

    public void showTabs() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tabContainer.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_live:
                setTab(0);
                break;
            case R.id.tab_friends:
                setTab(1);
                break;
            case R.id.tab_trails:
                setTab(2);
                break;
            case R.id.tab_conversation:
                setTab(3);
                break;
            case R.id.tab_notif:
                setTab(4);
                break;
        }
    }

    public void setTab(int i) {
        for (int j = 0; j < 5; j++) {
            if (j == i)
                tabs[i].setImageDrawable(getResources().getDrawable(blueIcons[i]));
            else
                tabs[j].setImageDrawable(getResources().getDrawable(greyIcons[j]));
        }
        mViewPager.setCurrentItem(i);
        showTabs();
        tabPosiotion = i;
    }

    public void notifyBrowserChanged() {
        if (EnrootActivity.isCompassAvailable)
            mSectionsPagerAdapter.switchBrowser();
    }


}