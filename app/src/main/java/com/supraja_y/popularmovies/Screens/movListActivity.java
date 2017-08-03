package com.supraja_y.popularmovies.Screens;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.supraja_y.popularmovies.Adapters.movAdapter;
import com.supraja_y.popularmovies.Application.Application;
import com.supraja_y.popularmovies.BuildConfig;
import com.supraja_y.popularmovies.Listener.RecyclerClickListener;
import com.supraja_y.popularmovies.POJOS.movModel;
import com.supraja_y.popularmovies.R;
import com.supraja_y.popularmovies.RetroFit.MovieDBAPI;
import com.supraja_y.popularmovies.data.MovieContract;
import com.supraja_y.popularmovies.data.MoviesProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class movListActivity extends AppCompatActivity {

    ArrayList<movModel> popularArrayList;
    ArrayList<movModel> ratedArrayList;
    ArrayList<movModel> favArrayList;


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    Cursor favoriteOnly;

    public static ProgressBar progressBar;

    movAdapter popularAdapter;
    movAdapter ratedAdapter;
    movAdapter favouriteAdapter;

    GridLayoutManager gridLayoutManager;
    private boolean mTwoPane;
    String SORT_BY = "POPULAR_MOVIES";
    NetworkReceiver networkReceiver;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    private String TAG=movListActivity.class.getSimpleName();
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mov_list);
        ButterKnife.bind(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        setSupportActionBar(toolbar);
        popularArrayList = new ArrayList<>();
        ratedArrayList = new ArrayList<>();
        favArrayList = new ArrayList<>();
        popularAdapter = new movAdapter(this, popularArrayList);
        ratedAdapter = new movAdapter(this, ratedArrayList);
        favouriteAdapter = new movAdapter(this, favArrayList);
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        networkReceiver = new NetworkReceiver();
        registerReceiver(networkReceiver, intentFilter);

        if (savedInstanceState != null) {
            popularAdapter.addAll(savedInstanceState.<movModel>getParcelableArrayList("POP"));
            ratedAdapter.addAll(savedInstanceState.<movModel>getParcelableArrayList("RATED_MOVIES"));
            SORT_BY = savedInstanceState.getString("SORT_BY");
        } else {
            if (isNetworkAvailable()) {

                (new FetchMoviesFromAPI()).execute("popular");
                (new FetchMoviesFromAPI()).execute("top_rated");
            }
        }
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
        }
        setupRView();

        recyclerView.addOnItemTouchListener(new RecyclerClickListener(this, new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                movModel movModel;
                switch (SORT_BY) {
                    case "POPULAR_MOVIES":
                        movModel = popularArrayList.get(position);
                        break;
                    case "RATED_MOVIES":
                        movModel = ratedArrayList.get(position);
                        break;
                    default:
                        movModel = favArrayList.get(position);
                        break;
                }

                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable("movie", movModel);
                    movDetailsFragment fragment = new movDetailsFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container, fragment)
                            .commit();
                } else {

                    Intent intent = new Intent(getApplicationContext(), movDetailsActivity.class);
                    intent.putExtra("movie", movModel);

                    startActivity(intent);
                }

            }
        }));

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SORT_BY.equals("FAVOURITE_MOVIES")) {


            getFavourites();

        }
    }

//    @Override
//    public void onChangedFavoriteStatus() {
//        Log.e("from mov_listActivity", "true");
//
//    }


    private class FetchMoviesFromAPI extends AsyncTask<String, Void,
            List<movModel>> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected List<movModel> doInBackground(String... params) {
            final String sort = params[0];
            Application.getMovieClient().getMovieDBAPI().loadMovies(sort, BuildConfig.API_KEY).enqueue(new Callback<MovieDBAPI.Movies>() {

                @Override
                public void onResponse(Response<MovieDBAPI.Movies> response, Retrofit retrofit) {

                    if (sort.equals("popular")) {
                        for (int i = 0; i < response.body().results.size(); i++) {
                            popularArrayList.add(response.body().results.get(i));
                            Log.v(sort, response.body().results.get(i).getoriginal_title());
                        }
                        popularAdapter.notifyDataSetChanged();
                    } else {
                        for (int i = 0; i < response.body().results.size(); i++) {
                            ratedArrayList.add(response.body().results.get(i));
                            Log.v(sort, response.body().results.get(i).getoriginal_title());
                        }
                        ratedAdapter.notifyDataSetChanged();
                    }
                    if (mTwoPane && popularArrayList.size() != 0) {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable("movie", popularArrayList.get(0));
                        movDetailsFragment fragment = new movDetailsFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, fragment)
                                .commit();
                    }

                }

                @Override
                public void onFailure(Throwable t) {

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(List<movModel> movModels) {

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_sort_by);

        item.setIcon(new IconicsDrawable(this).icon(MaterialDesignIconic.Icon.gmi_sort)
                .color(Color.WHITE)
                .sizeDp(24));
        switch (SORT_BY) {
            case "POPULAR_MOVIES":
                menu.findItem(R.id.action_sort_by_popularity).setChecked(true);
                break;
            case "RATED_MOVIES":
                menu.findItem(R.id.action_sort_by_rating).setChecked(true);
                break;
            case "FAVOURITE_MOVIES":
                menu.findItem(R.id.action_sort_by_favourite).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort_by_rating:
                recyclerView.setAdapter(ratedAdapter);
                SORT_BY = "RATED_MOVIES";
                break;
            case R.id.action_sort_by_popularity:
                recyclerView.setAdapter(popularAdapter);
                SORT_BY = "POPULAR_MOVIES";
                break;
            case R.id.action_sort_by_favourite:
                getFavourites();
                SORT_BY = "FAVOURITE_MOVIES";

                break;


        }
        item.setChecked(true);

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager.setSpanCount(3);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager.setSpanCount(2);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void setupRView() {
        gridLayoutManager = new GridLayoutManager(this, 2);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            gridLayoutManager.setSpanCount(2);
        else
            gridLayoutManager.setSpanCount(3);

        switch (SORT_BY) {
            case "POPULAR_MOVIES":
                recyclerView.setAdapter(popularAdapter);
                break;
            case "RATED_MOVIES":
                recyclerView.setAdapter(ratedAdapter);
                break;
            case "FAVOURITE_MOVIES":
                recyclerView.setAdapter(favouriteAdapter);
        }

        recyclerView.setLayoutManager(gridLayoutManager);
    }

    public void getFavourites() {

        favArrayList.clear();
        Log.d("Size", String.valueOf(favArrayList.size()));
        favArrayList = MoviesProvider.getFavovies(getBaseContext());
        if(progressBar.getVisibility()==View.VISIBLE)
            progressBar.setVisibility(View.GONE);

        favouriteAdapter = new movAdapter(this, favArrayList);
        recyclerView.setAdapter(favouriteAdapter);
        Log.d("Array Size", String.valueOf(favArrayList.size()));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("POP", popularArrayList);
        outState.putParcelableArrayList("RATED_MOVIES", ratedArrayList);
        outState.putString("SORT_BY", SORT_BY);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkReceiver);
    }

    public class NetworkReceiver extends BroadcastReceiver {
        public NetworkReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkAvailable() && popularArrayList.size() != 0) {
                (new FetchMoviesFromAPI()).execute("popular");
                (new FetchMoviesFromAPI()).execute("top_rated");
            }
            Toast.makeText(getApplicationContext(), "Network chnaged", Toast.LENGTH_SHORT).show();
        }
    }
//    public    ArrayList<movModel> get_fav_movies(Context context) { // Check if movie with passed id is favorite
//        ArrayList<movModel> movies = new ArrayList<>();
//
//        ContentResolver contentResolver = context.getContentResolver();
//        Cursor c = contentResolver.query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
//        if (c.getCount() > 0) {
//            while (c.moveToNext()) {
//                movModel movie = new movModel();
//
//                movie.setId(String.valueOf(c.getInt(1)));
//                movie.setOriginal_title(c.getString(2));
//                movie.setOverview(String.valueOf(c.getString(3)));
//                movie.setBackdrop_path(String.valueOf(c.getString(5)));
//                movie.setRelease_date(String.valueOf(c.getString(6)));
//                movie.setVote_average(c.getFloat(4));
//                movies.add(movie);
//                Log.d(TAG, "Fetching User from Sqlite: " + movie.getId());
//
//            }
//
//        }
//        c.close();
//
//        return movies;
//    }

}
