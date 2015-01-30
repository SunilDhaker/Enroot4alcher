package com.enrootapp.v2.main.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.enrootapp.v2.main.MapsActivity;
import com.enrootapp.v2.main.R;
import com.enrootapp.v2.main.app.EnrootFragment;
import com.enrootapp.v2.main.util.ar.SelfieActivity;

/**
 * Created by sdhaker on 15-01-2015.
 */
public class TrailsFragment extends EnrootFragment {

    ImageView map, add;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.trail_card_fragment, null);
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.trail_card_container);
        // FloatingActionButton fab =  new FloatingActionButton(getActivity());
//        rv.addView(fab);
        add = (ImageView) v.findViewById(R.id.trails_add);
        map = (ImageView) v.findViewById(R.id.trails_map);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SelfieActivity.class);
                startActivity(i);
            }
        });
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MapsActivity.class);
                startActivity(i);
            }
        });
        rv.setHasFixedSize(true);
       // rv.setAdapter(new TrailsAdapter(mApp.getTrail()));
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }

}
