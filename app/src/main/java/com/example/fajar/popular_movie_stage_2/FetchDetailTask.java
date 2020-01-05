package com.example.fajar.popular_movie_stage_2;

import android.net.Uri;
import android.os.AsyncTask;

import com.example.fajar.popular_movie_stage_2.callbacks.MovieDetailCallback;
import com.example.fajar.popular_movie_stage_2.model.Movies;

import org.jetbrains.annotations.Contract;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fajar on 21.06.2018.
 */

class FetchDetailTask extends AsyncTask<Movies, Void, Movies> {

    private final MovieDetailCallback detailMovieCallback;

    public FetchDetailTask(MovieDetailCallback detailMovieCallback) {
        this.detailMovieCallback = detailMovieCallback;
    }

    @Override
    protected Movies doInBackground(Movies... movies) {
        if (movies.length == 0) {
            return null;
        }

        final String BASE_URL = "https://api.themoviedb.org/3/movie/";
        final String API_KEY = "api_key";

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(String.valueOf(movies[0].getMovieId()))
                .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIEDB_API_KEY)
                .build();

        String jsonString = NetworkRequest.getJsonString(uri);

        try {
            return getMovieFromJson(jsonString, movies[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Contract("null, _ -> null")
    private Movies getMovieFromJson(String jsonString, Movies movie) throws JSONException {
        final String MOVIE_RUNTIME = "runtime";
        if (jsonString == null || "".equals(jsonString)) {
            return null;
        }

        JSONObject jsonObjectMovie = new JSONObject(jsonString);
        movie.setMovieRuntime(jsonObjectMovie.getString(MOVIE_RUNTIME));
        return movie;
    }

    @Override
    protected void onPostExecute(Movies movie) {
        if (movie != null) {
            detailMovieCallback.updateMovie(movie);
        }
    }
}
