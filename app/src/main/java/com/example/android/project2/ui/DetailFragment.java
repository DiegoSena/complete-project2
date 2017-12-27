package com.example.android.project2.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.project2.R;
import com.example.android.project2.data.MovieRepository;
import com.example.android.project2.data.adapter.ReviewAdapter;
import com.example.android.project2.data.adapter.TrailerAdapter;
import com.example.android.project2.data.db.MovieContract.MovieEntry;
import com.example.android.project2.model.Movie;
import com.example.android.project2.model.Review;
import com.example.android.project2.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.android.project2.Utils.internetConectivityIsOn;


public class DetailFragment extends Fragment implements AdapterClickListener{

    public static final String IMAGE_BASE_URL_SIZE_W342 = "http://image.tmdb.org/t/p/w342/";
    public static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private List<Trailer> trailers;
    private List<Review> reviews;

    private MovieRepository movieRepository;

    public DetailFragment(){
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (internetConectivityIsOn(getActivity())) {
            Movie movie = getActivity().getIntent().getParcelableExtra(Movie.PARCELABLE_KEY);
            trailers = getActivity().getIntent().getParcelableArrayListExtra("INTENT_TRAILER_DETAIL");
            reviews = getActivity().getIntent().getParcelableArrayListExtra("INTENT_REVIEW_DETAIL");
            trailerAdapter = new TrailerAdapter(trailers, this);
            reviewAdapter = new ReviewAdapter(reviews);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviesfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void inflateVideos(RecyclerView recyclerViewTrailers, ViewGroup trailerContainer) {
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewTrailers.setAdapter(trailerAdapter);
        trailerContainer.setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.detail_fragment, container, false);
        Movie movie = getActivity().getIntent().getParcelableExtra(Movie.PARCELABLE_KEY);
        TextView textView = (TextView) rootView.findViewById(R.id.detail_textview);
        textView.setText(movie.getTitle());
        ImageView image = (ImageView) rootView.findViewById(R.id.detail_imageview_poster);
        Picasso.with(getContext()).load(IMAGE_BASE_URL_SIZE_W342 + movie.getPosterPath()).into(image);
        TextView releaseYear = (TextView) rootView.findViewById(R.id.detail_textview_releaseyear);
        releaseYear.setText(""+ movie.getReleaseYear());
        TextView average = (TextView) rootView.findViewById(R.id.detail_textview_average);
        average.setText(String.format("%.1f/10", movie.getVoteAverage()));
        TextView overview = (TextView) rootView.findViewById(R.id.detail_textview_overview);
        overview.setText(movie.getOverview());

        Button favoriteButton = rootView.findViewById(R.id.fav_button);
        if(movie.isFavorite()){
            favoriteButton.setText(R.string.unmark_favorite);
        }else{
            favoriteButton.setText(R.string.mark_favorite);
        }
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(movie.isFavorite()){
                    getContext().getContentResolver().delete(MovieEntry.CONTENT_URI,
                            "id=?",
                            new String[]{movie.getId()});
                    Button button = (Button) v;
                    button.setText(R.string.mark_favorite);
                    Toast toast = Toast.makeText(getContext(), "Unsaved as favorite", Toast.LENGTH_LONG);
                    toast.show();
                }else{
                    ContentValues values = new ContentValues();
                    values.put(MovieEntry.COLUMN_ID, movie.getId());
                    values.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
                    values.put(MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
                    values.put(MovieEntry.COLUMN_RELEASE_YEAR, movie.getReleaseYear());
                    values.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
                    values.put(MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());

                    getContext().getContentResolver().insert(MovieEntry.CONTENT_URI, values);
                    Button button = (Button) v;
                    button.setText(R.string.unmark_favorite);
                    Toast toast = Toast.makeText(getContext(), "Saved as favorite", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        ViewGroup trailerContainer = rootView.findViewById(R.id.trailers_container);
        final RecyclerView recyclerViewTrailers = rootView.findViewById(R.id.listview_trailers);
        inflateVideos(recyclerViewTrailers, trailerContainer);

        ViewGroup reviewContainer = rootView.findViewById(R.id.reviews_container);
        final RecyclerView recyclerViewReviews = rootView.findViewById(R.id.listview_reviews);
        inflateReviews(recyclerViewReviews, reviewContainer);

        return rootView;
    }

    private void inflateReviews(RecyclerView recyclerViewReviews, ViewGroup reviewContainer) {
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
