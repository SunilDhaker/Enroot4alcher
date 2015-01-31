package com.enrootapp.v2.main;

/**
 * Created by sdhaker on 13-01-2015.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.enrootapp.v2.main.app.EnrootActivity;
import com.enrootapp.v2.main.tabs.ArBrowserFragment;
import com.enrootapp.v2.main.tabs.ChatFragment;
import com.enrootapp.v2.main.tabs.MapFragment;
import com.enrootapp.v2.main.tabs.NotificationsFragment;
import com.enrootapp.v2.main.tabs.StaticBrowserFragment;
import com.enrootapp.v2.main.tabs.TrailsFragment;

import java.util.Locale;

/**
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter extends FragmentPagerAdapter {

    private FragmentManager fm;
    private Fragment mFragmentAtPos0;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if (position == 0) {
            //return new ChatFragment();
            if (EnrootActivity.isCompassAvailable) {
                if (MainActivity.USER_SELECTION == MainActivity.AR) {
                    mFragmentAtPos0 = new ArBrowserFragment();
                    return mFragmentAtPos0;
                } else {
                    mFragmentAtPos0 = new StaticBrowserFragment();
                    return mFragmentAtPos0;
                }
            } else {
                mFragmentAtPos0 = new StaticBrowserFragment();
                return mFragmentAtPos0;
            }
        } else if (position == 1) {
            return new MapFragment();
        } else if (position == 2) {
            return new TrailsFragment();
        } else if (position == 3) {
            return new ChatFragment();
        } else {
            return new NotificationsFragment();
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
//        switch (position) {
//            case 0:
//                return "Fragment A";
//            case 1:
//                return "Fragment B";
//            case 2:
//                return "Fragment C";
//        }
        return null;
    }


    @Override
    public int getItemPosition(Object object) {
        if (object instanceof ArBrowserFragment || object instanceof StaticBrowserFragment)
            return POSITION_NONE;
        else return POSITION_UNCHANGED;
    }

    public void switchBrowser() {
        fm.beginTransaction().remove(mFragmentAtPos0)
                .commit();
        notifyDataSetChanged();
    }
}
