package com.enrootapp.v2.main.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enrootapp.v2.main.MainActivity;
import com.enrootapp.v2.main.R;
import com.enrootapp.v2.main.StaticBrowserAdapter;
import com.enrootapp.v2.main.app.EnrootApp;
import com.enrootapp.v2.main.util.SelectLocationActivity;
import com.enrootapp.v2.main.util.ar.SelfieActivity;

/**
 * Created by sdhaker on 27-01-2015.
 */

public class StaticBrowserFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, TextWatcher , EnrootApp.OnDataChangeListner{

    TextView actionBarLocationtext;
    View actionBarLocation;
    ImageButton arSearch, arStatic, arSelfei;
    RelativeLayout searchControls;
    RadioGroup rg;
    EditText searchQuery;
    private RecyclerView mRecyclerView;
    private StaticBrowserAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = View.inflate(getActivity(), R.layout.activity_static_browser, null);


        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new StaticBrowserAdapter(mRecyclerView, getActivity(), EnrootApp.getInstance().imressionsAt);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        arSearch = (ImageButton) v.findViewById(R.id.ar_controls_search);
        arSelfei = (ImageButton) v.findViewById(R.id.ar_controls_selfie);
        arStatic = (ImageButton) v.findViewById(R.id.ar_controls_static);
        searchQuery = (EditText) v.findViewById(R.id.search_query);
        actionBarLocation = v.findViewById(R.id.custome_action_bar_location_container);
        searchControls = (RelativeLayout) v.findViewById(R.id.search_container);
        rg = (RadioGroup) v.findViewById(R.id.radioGroup);
        rg.setOnCheckedChangeListener(this);
        searchControls.setVisibility(View.GONE);

        arSearch.setOnClickListener(this);
        arStatic.setOnClickListener(this);
        arSelfei.setOnClickListener(this);
        searchQuery.addTextChangedListener(this);


        actionBarLocation.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ar_controls_search:
                searchControls.setVisibility(View.VISIBLE);
                searchQuery.requestFocus();
                break;
            case R.id.ar_controls_selfie:
                Intent i = new Intent(getActivity(), SelfieActivity.class);
                startActivity(i);
                break;
            case R.id.ar_controls_static:
                MainActivity.USER_SELECTION = MainActivity.AR;
                ((MainActivity) getActivity()).notifyBrowserChanged();
                break;
            case R.id.custome_action_bar_location_container:
                Intent i2 = new Intent(getActivity(), SelectLocationActivity.class);
                startActivity(i2);
                break;
            case R.id.search_back:
                searchControls.setVisibility(View.GONE);

        }
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radioButton1:
                // mAdapter.filterMyFreinds();
                break;

            case R.id.radioButton2:
                // mAdapter.filterMyFreinds();
                break;

            case R.id.radioButton3:
                //mAdapter.filterOnlyMe();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mAdapter.search(s.toString());
    }

    @Override
    public void onDtaChange() {
        mAdapter.notifyDataSetChanged();
    }
}
