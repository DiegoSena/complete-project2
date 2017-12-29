package com.example.android.project2.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.project2.R;
import com.example.android.project2.data.adapter.ReviewAdapter;
import com.example.android.project2.data.adapter.TrailerAdapter;
import com.example.android.project2.data.db.MovieContract.MovieEntry;
import com.example.android.project2.model.Movie;
import com.example.android.project2.model.Review;
import com.example.android.project2.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.android.project2.Utils.internetConectivityIsOn;


public class DetailFragment extends BaseFragment implements DetailAdapterClickListener {

    public static final String IMAGE_BASE_URL_SIZE_W342 = "http://image.tmdb.org/t/p/w342/";
    public static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private List<Trailer> trailers;
    private List<Review> reviews;
    private Movie movie;

    @BindView(R.id.detail_textview) TextView title;
    @BindView(R.id.detail_imageview_poster) ImageView poster;
    @BindView(R.id.detail_textview_releaseyear) TextView releaseYear;
    @BindView(R.id.detail_textview_average) TextView average;
    @BindView(R.id.detail_textview_overview) TextView overview;
    @BindView(R.id.fav_button) Button favoriteButton;
    @BindView(R.id.trailers_container) ViewGroup trailerContainer;
    @BindView(R.id.reviews_container) ViewGroup reviewContainer;
    @BindView(R.id.listview_reviews) RecyclerView recyclerViewReviews;
    @BindView(R.id.listview_trailers) RecyclerView recyclerViewTrailers;

    public DetailFragment(){
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (internetConectivityIsOn(getActivity())) {
            movie = getActivity().getIntent().getParcelableExtra(Movie.PARCELABLE_KEY);
            trailers = getActivity().getIntent().getParcelableArrayListExtra(MoviesFragment.TRAILER_DETAIL_EXTRA);
            reviews = getActivity().getIntent().getParcelableArrayListExtra(MoviesFragment.REVIEW_DETAIL_EXTRA);
            trailerAdapter = new TrailerAdapter(trailers, this);
            reviewAdapter = new ReviewAdapter(reviews);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviesfragment, menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.detail_fragment, container, false);
        ButterKnife.bind(this, rootView);
        title.setText(movie.getTitle());
        setPoster();
        releaseYear.setText(""+movie.getReleaseYear());
        average.setText(String.format("%.1f/10", movie.getVoteAverage()));
        overview.setText(movie.getOverview());
        setFavoriteButtonText();
        inflateVideos();
        inflateReviews();
        return rootView;
    }

    @OnClick(R.id.fav_button)
    public void onFavoriteButtonClick(View view){
        if(movie.isFavorite()){
            getActivity().getContentResolver().delete(MovieEntry.CONTENT_URI,
                    "id=?",
                    new String[]{movie.getId()});
            movie.setFavorite(false);
            changeButtonText((Button) view, R.string.mark_favorite);
            showToast(R.string.unsaved_as_favorite);
        } else {
            ContentValues values = buildContentValues();
            getContext().getContentResolver().insert(MovieEntry.CONTENT_URI, values);
            movie.setFavorite(true);
            changeButtonText((Button) view, R.string.unmark_favorite);
            showToast(R.string.saved_as_favorite);
        }
    }

    @NonNull
    private ContentValues buildContentValues() {
        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_ID, movie.getId());
        values.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        values.put(MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        values.put(MovieEntry.COLUMN_RELEASE_YEAR, movie.getReleaseYear());
        values.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
        values.put(MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        return values;
    }

    private void changeButtonText(Button button, int message) {
        button.setText(message);
    }

    private void showToast(int message) {
        Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void setPoster() {
        Picasso.with(getActivity()).
                load(IMAGE_BASE_URL_SIZE_W342 + movie.getPosterPath()).
                placeholder(R.drawable.uploading)
                .error(R.drawable.error_loading).
                into(poster);
    }

    private void setFavoriteButtonText() {
        favoriteButton.setText( movie.isFavorite() ? R.string.unmark_favorite : R.string.mark_favorite );
    }

    private void inflateVideos() {
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewTrailers.setAdapter(trailerAdapter);
        trailerContainer.setVisibility(View.VISIBLE);
    }

    private void inflateReviews() {
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewReviews.setAdapter(reviewAdapter);
        reviewContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTrailerClick(int position) {
        Trailer trailer = trailerAdapter.getTrailerFromPosition(position);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_BASE_URL + trailer.getKey())));

    }
}
