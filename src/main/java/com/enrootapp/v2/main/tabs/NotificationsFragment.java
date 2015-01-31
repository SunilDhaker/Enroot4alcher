package com.enrootapp.v2.main.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by rmuttineni on 15/01/2015.
 */
public class NotificationsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        RecyclerView rv = new RecyclerView(getActivity());
        rv.setHasFixedSize(true);
        rv.setAdapter(new NotificationAdapter());
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rv;
    }
}
