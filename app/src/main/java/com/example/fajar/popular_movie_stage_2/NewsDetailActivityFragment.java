package com.example.fajar.popular_movie_stage_2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fajar.popular_movie_stage_2.callbacks.MovieDetailCallback;
import com.example.fajar.popular_movie_stage_2.callbacks.ReviewCallback;
import com.example.fajar.popular_movie_stage_2.callbacks.TrailerAdapterCallback;
import com.example.fajar.popular_movie_stage_2.callbacks.TrailerCallback;
import com.example.fajar.popular_movie_stage_2.database.FavoriteContract;
import com.example.fajar.popular_movie_stage_2.model.Movies;
import com.example.fajar.popular_movie_stage_2.model.Review;
import com.example.fajar.popular_movie_stage_2.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fajar on 14.06.2018.
 */

public class NewsDetailActivityFragment extends Fragment {

    private static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    private static final String LOGO_SIZE = "w500";
    private static final String YOUTUBE_APP_PACKAGE = "com.google.android.youtube";
    private static final String YOUTUBE_URL_APP = "vnd.youtube://";
    private static final String YOUTUBE_URL_BROWSER = "https://www.youtube.com/watch";
    private static final String VIDEO_PARAMETER = "v";
    private static final String MOVIE_KEY = "movie";
    private static final String KEY_IS_MARK_FAVORITE = "isMarkFavorite";
    private static final String KEY_REVIEW_LIST = "keyReviewList";
    private static final String KEY_TRAILER_LIST = "keyTrailerList";
    @BindView(R.id.logo_image_view)
    ImageView posterLogoImage;
    @BindView(R.id.button_favorite)
    ImageButton btnMarkFavorite;
    @BindView(R.id.movie_runtime)
    TextView movieRuntime;
    @BindView(R.id.trailers_title)
    TextView trailerTitle;
    @BindView(R.id.review_title)
    TextView reviewsTitle;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.year)
    TextView year;
    @BindView(R.id.rating)
    TextView rating;
    @BindView(R.id.description)
    TextView description;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private Movies movie;
    private boolean isMarkFavorite;
    private ArrayList<Trailer> trailersList;
    private ArrayList<Review> reviewsList;


    public NewsDetailActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle arguments = getArguments();
            if (arguments != null) {
                movie = arguments.getParcelable(NewsDetailActivityFragment.MOVIE_KEY);
            } else {
                movie = getActivity().getIntent().getParcelableExtra(MOVIE_KEY);
            }
            assert movie != null;
            isMarkFavorite = MovieIsFavourite(movie.getMovieId());
            trailersList = new ArrayList<>();
            reviewsList = new ArrayList<>();
            updateAdapters(movie.getMovieId());
        } else {
            movie = savedInstanceState.getParcelable(MOVIE_KEY);
            isMarkFavorite = savedInstanceState.getBoolean(KEY_IS_MARK_FAVORITE);
            trailersList = savedInstanceState.getParcelableArrayList(KEY_TRAILER_LIST);
            reviewsList = savedInstanceState.getParcelableArrayList(KEY_REVIEW_LIST);
        }
    }

    private boolean MovieIsFavourite(int movieId) {
        Cursor cursor = getActivity().getContentResolver()
                .query(FavoriteContract.FavoriteEntry.CONTENT_URI,
                        new String[]{FavoriteContract.FavoriteEntry.MOVIE_ID}, null, null, null);
        assert cursor != null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                int id = cursor.getInt(cursor.getColumnIndex(
                        FavoriteContract.FavoriteEntry.MOVIE_ID));
                if (id == movieId) {
                    cursor.close();
                    return true;
                }
            } while (cursor.moveToNext());

        }
        cursor.close();
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_detail, container, false);

        ButterKnife.bind(this, view);
        btnMarkFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favouriteButton();
            }
        });

        if (movie != null) {
            title.setText(movie.getTitle());
            loadPoster(movie.getPosterPath());
            year.setText(String.format("%.4s", movie.getReleaseDate()));
            rating.setText(String.format("%s/10", movie.getVoteAverage()));
            movieRuntime.setText(String.format("%s min", movie.getMovieRuntime()));
            description.setText(movie.getOverview());
        }

        updateButtonImage();

        RecyclerView recyclerViewTrailers = (RecyclerView)
                view.findViewById(R.id.recycler_trailers);
        recyclerViewTrailers.setLayoutManager(new CustomLinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        recyclerViewTrailers.setItemAnimator(new DefaultItemAnimator());

        trailerAdapter = new TrailerAdapter(trailersList,
                new TrailerAdapterCallback() {
                    @Override
                    public void onItemClickListener(String trailerKey) {
                        if (trailerKey != null) {
                            playVideo(trailerKey);
                        }
                    }
                });
        recyclerViewTrailers.setAdapter(trailerAdapter);
        recyclerViewTrailers.setHasFixedSize(true);

        RecyclerView recyclerViewReviews = (RecyclerView) view.findViewById(R.id.recycler_reviews);
        recyclerViewReviews.setLayoutManager(new CustomLinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        recyclerViewTrailers.setItemAnimator(new DefaultItemAnimator());

        reviewAdapter = new ReviewAdapter(reviewsList);
        recyclerViewReviews.setAdapter(reviewAdapter);
        recyclerViewReviews.setHasFixedSize(true);
        return view;
    }


    private void updateMovieDetail() {
        FetchDetailTask detailMoviesTask = new FetchDetailTask(new MovieDetailCallback() {
            @Override
            public void updateMovie(Movies movieDetail) {
                movie = movieDetail;
                movieRuntime.setText(String.format("%s min", movie.getMovieRuntime()));
            }
        });
        detailMoviesTask.execute(movie);
    }

    private void visibleTrailersTitle() {
        if (trailerAdapter.getItemCount() != 0) {
            trailerTitle.setVisibility(View.VISIBLE);
        } else {
            trailerTitle.setVisibility(View.INVISIBLE);
        }
    }

    private void visibleReviewsTitle() {
        if (reviewAdapter.getItemCount() != 0) {
            reviewsTitle.setVisibility(View.VISIBLE);
        } else {
            reviewsTitle.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_TRAILER_LIST, trailersList);
        outState.putParcelableArrayList(KEY_REVIEW_LIST, reviewsList);
        outState.putParcelable(MOVIE_KEY, movie);
        outState.putBoolean(KEY_IS_MARK_FAVORITE, isMarkFavorite);
    }

    private void playVideo(String trailerKey) {
        Intent intent;
        if (isYouTubeAppInstalled()) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_URL_APP + trailerKey));
        } else {
            Uri uri = Uri.parse(YOUTUBE_URL_BROWSER)
                    .buildUpon()
                    .appendQueryParameter(VIDEO_PARAMETER, trailerKey)
                    .build();
            intent = new Intent(Intent.ACTION_VIEW, uri);
        }
        startActivity(intent);
    }

    private boolean isYouTubeAppInstalled() {
        return getActivity().getPackageManager()
                .getLaunchIntentForPackage(YOUTUBE_APP_PACKAGE) != null;
    }

    @SuppressWarnings("deprecation")
    private void updateButtonImage() {
        if (isMarkFavorite) {
            btnMarkFavorite.setImageDrawable(getResources()
                    .getDrawable(R.drawable.fav_button));
        } else {
            btnMarkFavorite.setImageDrawable(getResources()
                    .getDrawable(R.drawable.fav_border));
        }
    }

    private void favouriteButton() {
        if (isMarkFavorite) {
            getActivity().getContentResolver()
                    .delete(FavoriteContract.FavoriteEntry.CONTENT_URI,
                            FavoriteContract.FavoriteEntry.MOVIE_ID + " = ? ",
                            new String[]{String.valueOf(movie.getMovieId())});

            isMarkFavorite = false;
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(FavoriteContract.FavoriteEntry.MOVIE_ID, movie.getMovieId());
            contentValues.put(FavoriteContract.FavoriteEntry.TITLE, movie.getTitle());
            contentValues.put(FavoriteContract.FavoriteEntry.POSTER_PATH, movie.getPosterPath());
            contentValues.put(FavoriteContract.FavoriteEntry.OVERVIEW, movie.getOverview());
            contentValues.put(FavoriteContract.FavoriteEntry.VOTE_AVERAGE, movie.getVoteAverage());
            contentValues.put(FavoriteContract.FavoriteEntry.RELEASE_DATE, movie.getReleaseDate());
            contentValues.put(FavoriteContract.FavoriteEntry.MOVIE_RUNTIME,
                    movie.getMovieRuntime());

            getActivity().getContentResolver()
                    .insert(FavoriteContract.FavoriteEntry.CONTENT_URI, contentValues);
            isMarkFavorite = true;
        }
        updateButtonImage();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressWarnings("StringBufferReplaceableByString")
    private void loadPoster(String path) {
        String urlBuilder = new StringBuilder()
                .append(BASE_POSTER_URL)
                .append(LOGO_SIZE)
                .append(path).toString();

        Picasso.with(getContext())
                .load(urlBuilder)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher)
                .into(posterLogoImage);
    }

    private void updateAdapters(int movieId) {
        if (movie.getMovieRuntime() == null || "".equals(movie.getMovieRuntime()) ||
                movie.getMovieRuntime().equals("null")) {
            updateMovieDetail();
        }
        FetchTrailerTask trailersTask = new FetchTrailerTask(new TrailerCallback() {
            @Override
            public void updateAdapter(Trailer[] trailers) {
                if (trailers != null) {
                    trailersList.clear();
                    Collections.addAll(trailersList, trailers);
                    trailerAdapter.notifyDataSetChanged();
                    visibleTrailersTitle();
                }
            }
        });
        trailersTask.execute(movieId);

        FetchReviewTask reviewsTask = new FetchReviewTask(new ReviewCallback() {
            @Override
            public void updateAdapter(Review[] reviews) {
                if (reviews != null) {
                    reviewsList.clear();
                    Collections.addAll(reviewsList, reviews);
                    reviewAdapter.notifyDataSetChanged();
                    visibleReviewsTitle();
                }
            }
        });
        reviewsTask.execute(movieId);
    }
}
