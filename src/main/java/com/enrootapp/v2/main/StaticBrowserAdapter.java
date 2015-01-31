package com.enrootapp.v2.main;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.enrootapp.v2.main.data.Impression;
import com.enrootapp.v2.main.util.DateUtil;
import com.enrootapp.v2.main.util.TestUtil;

import java.util.ArrayList;


/**
 * Created by sdhaker on 15-01-2015.
 */
public class StaticBrowserAdapter extends RecyclerView.Adapter<StaticBrowserAdapter.ViewHolder> {
    public RecyclerView rclView;
    public Context context;
    private ArrayList<Impression> mDataset = new ArrayList<>();
    private ArrayList<Impression> searchedData =new ArrayList<>();
    private ArrayList<Impression> filteredData = new ArrayList<>();

    public StaticBrowserAdapter(RecyclerView rclView, Context context, ArrayList<Impression> imressionsAt) {
         this.context = context ;
        this.rclView = rclView;
        mDataset = imressionsAt ;
        searchedData.addAll(mDataset);

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
         holder.render(searchedData.get(position) , context);

    }

    @Override
    public int getItemCount() {
        return searchedData.size();//TODO;
    }

    public void search(String query) {
        if (query == "" || query == " ")
            searchedData.addAll(mDataset);
        else {
            searchedData.clear();

            for (Impression i : mDataset) {
                if (i.getCaption().toLowerCase().contains(query.toLowerCase()) || i.getOwnerName().toLowerCase().contains(query.toLowerCase()))
                    searchedData.add(i);
            }
        }
        notifyDataSetChanged();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView timestamp , username , caption;
        ImageView impressionImage  , userlogo;



        public ViewHolder(View v) {
            super(v);
            //infoText = (TextView) v.findViewById(R.id.cardview_impression);
            userlogo = (ImageView) v.findViewById(R.id.userlogo);
            impressionImage = (ImageView) v.findViewById(R.id.cardview_impression);
            username = (TextView) v.findViewById(R.id.static_view_username);
            timestamp = (TextView) v.findViewById(R.id.static_view_timestap);
            caption = (TextView) v.findViewById(R.id.capation);

        }
        public void render(Impression imp , Context c){
             impressionImage.setImageBitmap(TestUtil.getObjectImage(imp ,c ));
             caption.setText(imp.getCaption());
             username.setText(imp.getOwnerName());
             userlogo.setImageBitmap(TestUtil.getProfileImage(imp , c));
            timestamp.setText(DateUtil.elapsedTime(imp.getTimestamp().getTime()));
        }
    }

}