package com.supraja_y.popularmovies.Screens;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.supraja_y.popularmovies.BuildConfig;
import com.supraja_y.popularmovies.POJOS.movModel;
import com.supraja_y.popularmovies.R;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.supraja_y.popularmovies.R.id.recyclerView;


public class movDetailsActivity extends AppCompatActivity {

    @Bind(R.id.toolImage)
    ImageView toolImage;
    private int mScrollPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mov_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        movModel movModel =  getIntent().getParcelableExtra("movie");

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
            Picasso.with(this).load(BuildConfig.IMAGE_URL + "/w500" + movModel.getBackdrop_path() + "?api_key?=" + BuildConfig.API_KEY).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(toolImage);
        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable("movie", movModel);
            movDetailsFragment fragment = new movDetailsFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_details_scrollview, fragment)
                    .commit();
        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detials, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                supportFinishAfterTransition();
                super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }




}
