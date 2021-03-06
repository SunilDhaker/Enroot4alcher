package com.enrootapp.v2.main.tabs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enrootapp.v2.main.R;
import com.enrootapp.v2.main.TrailDetail;
import com.enrootapp.v2.main.app.EnrootApp;
import com.enrootapp.v2.main.data.GeoName;
import com.enrootapp.v2.main.data.Impression;
import com.enrootapp.v2.main.util.DateUtil;
import com.enrootapp.v2.main.util.TestUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by sdhaker on 15-01-2015.
 */
public class TrailsAdapter extends RecyclerView.Adapter<TrailsAdapter.ViewHolder> {

    private ArrayList<Impression> mDataset;

    public TrailsAdapter(ArrayList<Impression> trail) {
        mDataset = new ArrayList<Impression>();
        mDataset.addAll(trail); //Maybe reverse
    }

    @Override
    public TrailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {

        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trail_card, parent, false), parent.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.render(mDataset.get(position));
        //TODO do it later
    }

    @Override
    public int getItemCount() {
        return mDataset.size(); //TODO remove it
        //return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View discoverContainer;
        private final View divider;
        public View.OnClickListener onClickListener;
        public int position = 0;
        private Context context;
        private ImageView impressionImgae;
        private TextView timestamp, viewCounter;
        private RelativeLayout cardInfoContainer;
        private TextView locationStamp1, locationStamp2, taggedPeople;

        public ViewHolder(View v, final Context context) {
            super(v);
            // CardView cv = (CardView) v ;
            //cv.setContentPadding(0,0,0,0);
            //cv.setUseCompatPadding(true);
            this.context = context ;
            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, TrailDetail.class);
                    // i.putExtra("dataNumber" , i);
                    context.startActivity(i);
                }
            };
            this.context = context;
            impressionImgae = (ImageView) v.findViewById(R.id.trail_card_impression_image);
            locationStamp1 = (TextView) v.findViewById(R.id.trail_card_location_stamp1);
            locationStamp2 = (TextView) v.findViewById(R.id.trail_card_location_stamp2);
            timestamp = (TextView) v.findViewById(R.id.trail_card_time_stamp);
            viewCounter = (TextView) v.findViewById(R.id.trail_card_views_counter);
            taggedPeople = (TextView) v.findViewById(R.id.trail_card_tagged_people);
            cardInfoContainer = (RelativeLayout) v.findViewById(R.id.trail_card_info_container);
            v.findViewById(R.id.trail_ripple).setOnClickListener(onClickListener);
            divider = v.findViewById(R.id.divider);
            discoverContainer = v.findViewById(R.id.discover_container);
        }

        public void render(Impression impression) {
            //TODO: change to loading icon

            Impression i  = impression ;
            impressionImgae.setImageBitmap(TestUtil.getObjectImage(impression , context));

        }

    }


}