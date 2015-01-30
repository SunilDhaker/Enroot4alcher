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

import com.enrootapp.v2.main.R;
import com.enrootapp.v2.main.app.EnrootFragment;
import com.enrootapp.v2.main.util.Logger;
import com.enrootapp.v2.main.util.ar.SelfieActivity;

/**
 * Created by rmuttineni on 15/01/2015.
 */
public class ChatFragment extends EnrootFragment {

    public RecyclerView rv;
    ImageView add;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.chat_fragment, null);
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.conversation_card_container);
        // FloatingActionButton fab =  new FloatingActionButton(getActivity());
//        rv.addView(fab);

        rv.setOnScrollListener(new ScrollListener());

        add = (ImageView) v.findViewById(R.id.conversations_add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SelfieActivity.class);
                startActivity(i);
            }
        });
        rv.setHasFixedSize(true);

//
//        adapter = new ChatAdapter(mApp.getFbId(), mApp.getMsgCache());
//        rv.setAdapter(adapter);
//        rv.setLayoutManager(new LinearLayoutManager(getActivity()));


        return v;
    }

    public class ScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView view, int scrollState) {
            Logger.d(TAG, "scrolling...");
        }


        @Override
        public void onScrolled(RecyclerView rv, int dx, int dy) {
            Logger.d(TAG, "dx: " + dx + " dy: " + dy);

        }
    }


}
