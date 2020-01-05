package com.example.fajar.popular_movie_stage_2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fajar on 21.06.2018.
 */

public class FavoriteDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    public FavoriteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " +
                FavoriteContract.FavoriteEntry.TABLE_FAVORITE + "(" +
                FavoriteContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteContract.FavoriteEntry.MOVIE_ID + " INTEGER NOT NULL, " +
                FavoriteContract.FavoriteEntry.TITLE + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.POSTER_PATH + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.OVERVIEW + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.VOTE_AVERAGE + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.RELEASE_DATE + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.MOVIE_RUNTIME + " TEXT);";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +
                FavoriteContract.FavoriteEntry.TABLE_FAVORITE);
        onCreate(sqLiteDatabase);
    }
}
