package com.supraja_y.popularmovies.providers;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.supraja_y.popularmovies.POJOS.movModel;
import com.supraja_y.popularmovies.POJOS.revModel;
import com.supraja_y.popularmovies.POJOS.traModel;
import com.tjeannin.provigen.ProviGenOpenHelper;
import com.tjeannin.provigen.ProviGenProvider;


public class MovieDbProvider extends ProviGenProvider {
    private static final String DB_NAME = "MovieDb";
    private static final int DB_VERSION = 1;

    private static final String AUTHORITY = "content://com.supraja_y.popularmovies";

    private static final Class[] contracts = new Class[]{
            movModel.Contract.class, traModel.Contract.class, revModel.Contract.class};

    @Override
    public SQLiteOpenHelper openHelper(Context context) {
        return new ProviGenOpenHelper(context, DB_NAME, null, DB_VERSION, contracts);
    }

    @Override
    public Class[] contractClasses() {
        return contracts;
    }

    public static Uri getContentUri(String path) {
        return Uri.parse(AUTHORITY).buildUpon().appendPath(path).build();
    }
}
