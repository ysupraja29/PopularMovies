package com.supraja_y.popularmovies.Screens;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.squareup.picasso.Picasso;
import com.supraja_y.popularmovies.Adapters.revAdapter;
import com.supraja_y.popularmovies.Adapters.traAdapter;
import com.supraja_y.popularmovies.Application.Application;
import com.supraja_y.popularmovies.BuildConfig;
import com.supraja_y.popularmovies.Listener.RecyclerClickListener;
import com.supraja_y.popularmovies.POJOS.movModel;
import com.supraja_y.popularmovies.POJOS.revModel;
import com.supraja_y.popularmovies.POJOS.traModel;
import com.supraja_y.popularmovies.R;
import com.supraja_y.popularmovies.RetroFit.MovieDBAPI;
import com.supraja_y.popularmovies.data.MovieContract;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class movDetailsFragment extends Fragment {
    private static final String STATE_SCROLL_VIEW = "state_scroll_view";
    private static final String STATE_TRAILER = "state_trailer";
    private static final String STATE_REVIEW = "state_review";

    private Callbacks mCallbacks = sDummyCallbacks;
    private ContentResolver contentResolver;
    private int rev_lastFirstVisiblePosition;
    private int tra_lastFirstVisiblePosition;


    public movDetailsFragment() {
    }

    ScrollView mScrollView;
    movModel movModel;
    RecyclerView traRecyclerView;
    RecyclerView revRecyclerView;
    @Bind(R.id.titleView)
    TextView titleView;
    @Bind(R.id.overview)
    TextView overview;
    @Bind(R.id.releaseText)
    TextView releaseText;
    @Bind(R.id.noReviewView)
    TextView noReviewView;
    @Bind(R.id.rating)
    TextView rating;
    @Bind(R.id.ratingBar)
    RatingBar ratingBar;
    @Bind(R.id.extras)
    LinearLayout extraLayout;
    @Bind(R.id.noTraView)
    TextView noTraView;
    @Bind(R.id.imageView)
    ImageView imageView;

    ArrayList<traModel> trailerList;
    ArrayList<revModel> reviewList;

    revAdapter reviewAdapter;
    traAdapter traAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            return;
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }


    public interface Callbacks {
        void onChangedFavoriteStatus();
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static final Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onChangedFavoriteStatus() {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments().containsKey("movie")) {

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.collapsing_toolbar);
            if (appBarLayout != null) {
                appBarLayout.setTitle("");
            }
            movModel = getArguments().getParcelable("movie");
            assert movModel != null;
        }
        trailerList = new ArrayList<>();
        reviewList = new ArrayList<>();
        (new FetchReviews()).execute(movModel.getId());
        (new FetchTrailers()).execute(movModel.getId());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mov_details, container, false);
        ButterKnife.bind(this, rootView);
        setRetainInstance(true);

        mScrollView= (ScrollView) rootView.findViewById(R.id.movie_scroll_view);
        traRecyclerView=(RecyclerView) rootView.findViewById(R.id.traRV);
        revRecyclerView=(RecyclerView) rootView.findViewById(R.id.revRV);

        contentResolver = getActivity().getContentResolver();

        if (savedInstanceState != null) {
            tra_lastFirstVisiblePosition = savedInstanceState.getInt(STATE_TRAILER);
            rev_lastFirstVisiblePosition = savedInstanceState.getInt(STATE_REVIEW);

            Log.d("restore_trailer_positon",String.valueOf(tra_lastFirstVisiblePosition));
            Log.d("restore_review_positon",String.valueOf(rev_lastFirstVisiblePosition));
            final int[] position = savedInstanceState.getIntArray(STATE_SCROLL_VIEW);
            if (position != null)
                mScrollView.post(new Runnable() {
                    public void run() {
                        mScrollView.scrollTo(position[0], position[1]);
                    }
                });

        }
        titleView.setText(movModel.getoriginal_title());

        Picasso.with(getActivity()).load(BuildConfig.IMAGE_URL + "/w342" + movModel.getposter_path() + "?api_key?=" + BuildConfig.API_KEY).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(imageView);
        rating.setText(Float.toString(movModel.getvote_average()).concat("/10"));
        ratingBar.setMax(5);
        ratingBar.setRating(movModel.getvote_average() / 2f);
        overview.setText(movModel.getOverview());
        releaseText.setText("Release Date: ".concat(movModel.getrelease_date()));
        if (!isNetworkAvailable())
            extraLayout.setVisibility(View.INVISIBLE);

        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(getContext());
        traRecyclerView.setLayoutManager(trailerLayoutManager);
        revRecyclerView.setLayoutManager(reviewLayoutManager);
        reviewAdapter = new revAdapter(getContext(), reviewList);
        traAdapter = new traAdapter(getContext(), trailerList);
        traRecyclerView.setAdapter(traAdapter);
        revRecyclerView.setAdapter(reviewAdapter);

        traRecyclerView.addOnItemTouchListener(new RecyclerClickListener(getContext(), new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String url = "https://www.youtube.com/watch?v=".concat(trailerList.get(position).getKey());
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }

        }));

        revRecyclerView.addOnItemTouchListener(new RecyclerClickListener(getContext(), new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(reviewList.get(position).getUrl()));
                startActivity(i);
            }
        }));
        traRecyclerView.getLayoutManager().scrollToPosition(tra_lastFirstVisiblePosition);
        revRecyclerView.getLayoutManager().scrollToPosition(rev_lastFirstVisiblePosition);

        return rootView;
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.share).setVisible(true);
        MenuItem item = menu.findItem(R.id.fav);
        MenuItem item_share = menu.findItem(R.id.share);
        item.setVisible(true);
        item.setIcon(!isFavorite(getContext(), Integer.parseInt(movModel.getId())) ? new IconicsDrawable(getActivity())
                .icon(MaterialDesignIconic.Icon.gmi_favorite_outline)
                .color(Color.WHITE)
                .sizeDp(24) : new IconicsDrawable(getActivity())
                .icon(MaterialDesignIconic.Icon.gmi_favorite)
                .color(Color.WHITE)
                .sizeDp(24));
        item_share.setIcon(new IconicsDrawable(getContext()).icon(MaterialDesignIconic.Icon.gmi_share)
                .color(Color.WHITE)
                .sizeDp(24));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_SUBJECT, movModel.getoriginal_title());
                share.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=".concat(trailerList.get(0).getKey()));
                startActivity(Intent.createChooser(share, "Share Trailer!"));
                break;


            case R.id.fav:
                new FavoriteActionAsyncTask(getActivity(), item).execute();

                break;


        }
        return super.onOptionsItemSelected(item);
    }

    private class FavoriteActionAsyncTask extends AsyncTask<Void, Void, Boolean> {
        final Context mContext;
        MenuItem mitem;

        public FavoriteActionAsyncTask(Context context, MenuItem item) {
            mContext = context;
            mitem = item;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final IconicsDrawable drawable;
            boolean isFav = isFavorite(getActivity(), Integer.parseInt(movModel.getId()));
            if (!isFav) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieContract.MovieEntry.MOVIE_ID, movModel.getId());
                contentValues.put(MovieContract.MovieEntry.TITLE, movModel.getoriginal_title());
                contentValues.put(MovieContract.MovieEntry.OVERVIEW, movModel.getOverview());
                contentValues.put(MovieContract.MovieEntry.RELEASE_DATE, movModel.getrelease_date());
                contentValues.put(MovieContract.MovieEntry.VOTE_RATE, movModel.getvote_average());
                contentValues.put(MovieContract.MovieEntry.POSTER_URL, movModel.getposter_path());

                contentResolver.insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
                drawable = new IconicsDrawable(getActivity())
                        .icon(MaterialDesignIconic.Icon.gmi_favorite)
                        .color(Color.WHITE)
                        .sizeDp(24);
            } else {
                contentResolver.delete(MovieContract.MovieEntry.CONTENT_URI, MovieContract.MovieEntry.MOVIE_ID + "=?", new String[]{movModel.getId()});

                drawable = new IconicsDrawable(getActivity())
                        .icon(MaterialDesignIconic.Icon.gmi_favorite_outline)
                        .color(Color.WHITE)
                        .sizeDp(24);
            }


            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mitem.setIcon(drawable);

                }
            });


            return isFavorite(getActivity(), Integer.parseInt(movModel.getId()));
        }

        @Override
        protected void onPostExecute(Boolean isFavoriteMovie) {
            if (mContext == null) {
                return;
            }
            mCallbacks.onChangedFavoriteStatus();

        }
    }



    public static boolean isFavorite(Context context, int id) { // Check if movie with passed id is favorite
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MovieContract.MovieEntry.CONTENT_URI, null, MovieContract.MovieEntry.MOVIE_ID + "=?", new String[]{String.valueOf(id)}, null);

        int found = cursor.getCount();

        cursor.close();

        return found != 0;
    }


    private class FetchReviews extends AsyncTask<String, Void, List<revModel>> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected List<revModel> doInBackground(String... params) {
            final String sort = params[0];
            Application.getMovieClient().getMovieDBAPI().loadReviews(sort, BuildConfig.API_KEY).enqueue(new Callback<MovieDBAPI.Reviews>() {

                @Override
                public void onResponse(Response<MovieDBAPI.Reviews> response, Retrofit retrofit) {

                    for (int i = 0; i < response.body().results.size(); i++) {
                        reviewList.add(response.body().results.get(i));
                    }
                    reviewAdapter.notifyDataSetChanged();
                    if (reviewList.isEmpty()) {
                        revRecyclerView.setVisibility(View.INVISIBLE);
                        noReviewView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(List<revModel> movieModels) {
            super.onPostExecute(movieModels);
        }
    }

    private class FetchTrailers extends AsyncTask<String, Void,
            List<traModel>> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected List<traModel> doInBackground(String... params) {
            final String sort = params[0];
            Application.getMovieClient().getMovieDBAPI().loadTrailers(sort, BuildConfig.API_KEY).enqueue(new Callback<MovieDBAPI.Trailers>() {

                @Override
                public void onResponse(Response<MovieDBAPI.Trailers> response, Retrofit retrofit) {

                    for (int i = 0; i < response.body().results.size(); i++) {
                        trailerList.add(response.body().results.get(i));
                    }
                    traAdapter.notifyDataSetChanged();
                    if (trailerList.isEmpty()) {
                        traRecyclerView.setVisibility(View.INVISIBLE);
                        noTraView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(List<traModel> movieModels) {
            super.onPostExecute(movieModels);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        tra_lastFirstVisiblePosition = ((LinearLayoutManager) traRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        rev_lastFirstVisiblePosition = ((LinearLayoutManager) revRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        Log.d("trailer_positon",String.valueOf(tra_lastFirstVisiblePosition));
        Log.d("review_positon",String.valueOf(rev_lastFirstVisiblePosition));
        currentState.putIntArray(STATE_SCROLL_VIEW,
                new int[]{mScrollView.getScrollX(), mScrollView.getScrollY()});
        currentState.putInt(STATE_TRAILER, tra_lastFirstVisiblePosition);
        currentState.putInt(STATE_REVIEW, rev_lastFirstVisiblePosition);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            tra_lastFirstVisiblePosition = savedInstanceState.getInt(STATE_TRAILER);
            rev_lastFirstVisiblePosition = savedInstanceState.getInt(STATE_REVIEW);

            Log.d("restore_trailer_positon",String.valueOf(tra_lastFirstVisiblePosition));
            Log.d("restore_review_positon",String.valueOf(rev_lastFirstVisiblePosition));
            final int[] position = savedInstanceState.getIntArray(STATE_SCROLL_VIEW);
            if (position != null)
                mScrollView.post(new Runnable() {
                    public void run() {
                        mScrollView.scrollTo(position[0], position[1]);
                    }
                });
            traRecyclerView.getLayoutManager().scrollToPosition(tra_lastFirstVisiblePosition);
            revRecyclerView.getLayoutManager().scrollToPosition(rev_lastFirstVisiblePosition);

        }
    }


}
