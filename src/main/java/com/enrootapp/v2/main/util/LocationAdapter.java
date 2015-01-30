package com.enrootapp.v2.main.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.enrootapp.v2.main.R;
import com.enrootapp.v2.main.data.GeoName;

import java.util.ArrayList;


/**
 * Created by sdhaker on 24-01-2015.
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {


    public ArrayList<GeoName> mDataset = new ArrayList<GeoName>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_item, parent, false), parent.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.render(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private TextView locationName;
        private TextView locationImressions;
        private TextView locationToDiscover;
        private CheckBox selectLocation;
        private ImageView locationLogo;

        public ViewHolder(View v, Context context) {
            super(v);
            this.context = context;

            locationName = (TextView) v.findViewById(R.id.location_item_name);
            locationImressions = (TextView) v.findViewById(R.id.location_item_cont);
            locationToDiscover = (TextView) v.findViewById(R.id.location_item_to_discover);
            locationLogo = (ImageView) v.findViewById(R.id.location_item_logo);
        }

        public void render(GeoName loc) {

            String nam = loc.getName();
            nam.trim();
            locationName.setText(loc.getName());
        }

    }
}


