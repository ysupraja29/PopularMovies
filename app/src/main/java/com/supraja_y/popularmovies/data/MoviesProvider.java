package com.supraja_y.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.supraja_y.popularmovies.POJOS.movModel;

import java.util.ArrayList;

import static com.supraja_y.popularmovies.data.MovieContract.MovieEntry.FAVORITE_TABLE;
import static com.supraja_y.popularmovies.data.MovieContract.MovieEntry.MOVIE_ID;

/**
 * Created by mohammad on 04/08/16.
 */

public class MoviesProvider extends ContentProvider {

    private static final String TAG = MoviesProvider.class.getSimpleName();

    public static final int FAVORITE_CODE = 100;
    public static final int FAVORITE_WITH_ID_CODE = 101;

    public static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(MovieContract.AUTHORITY, FAVORITE_TABLE, FAVORITE_CODE);
        URI_MATCHER.addURI(MovieContract.AUTHORITY, FAVORITE_TABLE + "/#", FAVORITE_WITH_ID_CODE);
    }


    private MoviesDBHelper moviesDBHelper;

    @Override
    public boolean onCreate() {
        moviesDBHelper = new MoviesDBHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        switch (URI_MATCHER.match(uri)) {
            case FAVORITE_CODE:
                return moviesDBHelper.getReadableDatabase().query(FAVORITE_TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);

            case FAVORITE_WITH_ID_CODE:
                return moviesDBHelper.getReadableDatabase().query(FAVORITE_TABLE,
                        projection,
                        MOVIE_ID + "=?",
                        new String[]{String.valueOf(MovieContract.MovieEntry.getIdFromUri(uri))},
                        null, null, null);

            default:
                throw new UnsupportedOperationException("Unknown Uri : " + uri);
        }

    }

    public static ArrayList<movModel> getFavovies(Context context) {
        ArrayList<movModel> movies = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + FAVORITE_TABLE;
        MoviesDBHelper moviesDBHelper = new MoviesDBHelper(context);
        SQLiteDatabase db = moviesDBHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                movModel movie = new movModel();

                movie.setId(String.valueOf(c.getInt(1)));
                movie.setOriginal_title(c.getString(2));
                movie.setOverview(String.valueOf(c.getString(3)));
                movie.setVote_average(c.getFloat(4));
                movie.setPoster_path(String.valueOf(c.getString(5)));
                movie.setRelease_date(String.valueOf(c.getString(6)));
                movies.add(movie);
                Log.d(TAG, "Fetching User from Sqlite: " + movie.getId());

            }

        }

        c.close();
        db.close();
        // return User

        return movies;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int code = URI_MATCHER.match(uri);

        switch (code) {
            case FAVORITE_CODE:
                return MovieContract.MovieEntry.CONTENT_DIR_TYPE;
            case FAVORITE_WITH_ID_CODE:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri : " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        SQLiteDatabase db = moviesDBHelper.getWritableDatabase();

        Uri returnedUri = null;

        switch (URI_MATCHER.match(uri)) {
            case FAVORITE_CODE:
                int id = (int) db.insert(FAVORITE_TABLE, null, contentValues);
                if (id > -1) {
                    returnedUri = MovieContract.MovieEntry.buildMovieByIdUri(id);
                } else {
                    throw new SQLiteException("Failed to insert into table : " + FAVORITE_TABLE);
                }

                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri : " + uri);
        }


        getContext().getContentResolver().notifyChange(uri, null);

        return returnedUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {


        SQLiteDatabase db = moviesDBHelper.getWritableDatabase();

        switch (URI_MATCHER.match(uri)) {
            case FAVORITE_CODE:

                return db.delete(FAVORITE_TABLE, s, strings);

            default:
                throw new UnsupportedOperationException("Unknown Uri :" + uri);
        }

    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) { // In our case , we do not need update method .. because we will not change any movie data
        return 0;
    }
}
