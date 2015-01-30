package com.enrootapp.v2.main;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enrootapp.v2.main.data.Impression;

import java.util.ArrayList;


/**
 * Created by sdhaker on 15-01-2015.
 */
public class StaticBrowserAdapter extends RecyclerView.Adapter<StaticBrowserAdapter.ViewHolder> {
    public RecyclerView rclView;
    public Context context;
    private ArrayList<Impression> mDataset;
    private ArrayList<Impression> searchedData;
    private ArrayList<Impression> filteredData;

    public StaticBrowserAdapter(RecyclerView rclView, Context context) {

        this.rclView = rclView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);
        // v.setMinimumHeight(rclView.getHeight());
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // holder.infoText.setText(mDataset[position]);

    }

    @Override
    public int getItemCount() {
        return 20;//TODO;
    }

    public void search(String query) {
        if (query == "" || query == " ")
            searchedData.addAll(mDataset);
        else {
            searchedData.clear();

            for (Impression i : mDataset) {
                if (i.getCaption().contains(query)) //TODO add searching on people also
                    searchedData.add(i);
            }
        }
        notifyDataSetChanged();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView infoText;

        public ViewHolder(View v) {
            super(v);
            //infoText = (TextView) v.findViewById(R.id.cardview_impression);
            // infoText.setWidth;
        }
    }

}