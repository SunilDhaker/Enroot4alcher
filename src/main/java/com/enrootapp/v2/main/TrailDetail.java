package com.enrootapp.v2.main;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.enrootapp.v2.main.app.EnrootActivity;


public class TrailDetail extends EnrootActivity implements View.OnClickListener {

    ImageView abBack;
    ImageView[] imageView = new ImageView[5];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trail_detail);
        getSupportActionBar().hide();
        imageView[0] = (ImageView) findViewById(R.id.image1);
        imageView[1] = (ImageView) findViewById(R.id.image2);
        imageView[2] = (ImageView) findViewById(R.id.image3);
        imageView[3] = (ImageView) findViewById(R.id.image4);
        imageView[4] = (ImageView) findViewById(R.id.image5);
        abBack = (ImageView) findViewById(R.id.custome_action_bar_back);
        abBack.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trail_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.home || id == R.id.homeAsUp)
            onBackPressed();
        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.custome_action_bar_back:
                onBackPressed();
        }
    }
}
