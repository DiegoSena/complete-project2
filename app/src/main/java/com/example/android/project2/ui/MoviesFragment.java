package com.example.android.project2.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.project2.data.adapter.MovieAdapter;
import com.example.android.project2.data.MovieRepository;
import com.example.android.project2.R;
import com.example.android.project2.model.Movie;
import com.example.android.project2.model.Review;
import com.example.android.project2.model.Trailer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Optional;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

import static com.example.android.project2.Utils.internetConectivityIsOn;

public class MoviesFragment extends BaseFragment implements MovieAdapterItemClickListener{

    public static final String TRAILER_DETAIL_EXTRA = "TRAILER_DETAIL_EXTRA";
    public static final String REVIEW_DETAIL_EXTRA = "INTENT_REVIEW_DETAIL";
    private static final int GRID_COLUMNS = 2;
    private static final String SAVED_LAYOUT_MANAGER = "savedRecyclerViewState";
    private static final String FAVORITE = "favorite";

    @Nullable @BindView(R.id.gridview_movies) RecyclerView moviesGridView;
    @Nullable @BindView(R.id.error_message) TextView errorMessage;

    private MovieRepository movieRepository;
    private MovieAdapter movieAdapter;
    private Parcelable layoutManagerSavedState;

    public MoviesFragment(){
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        movieAdapter = new MovieAdapter(getContext(), this);
        movieRepository = new MovieRepository(getContext().getContentResolver());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviesfragment, menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movies_main, container, false);
        ButterKnife.bind(this, rootView);
        moviesGridView.setAdapter(movieAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(moviesGridView.getLayoutManager() == null){
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), GRID_COLUMNS);
            moviesGridView.setLayoutManager(gridLayoutManager);
        }

        if(savedInstanceState != null){
            layoutManagerSavedState = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isFavoriteSortSelected()){
            movieAdapter.clear();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(moviesGridView.getLayoutManager() != null){
            outState.putParcelable(SAVED_LAYOUT_MANAGER, moviesGridView.getLayoutManager().onSaveInstanceState());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        if (isFavoriteSortSelected()) {
            showFavoriteGridView();
        } else {
            showSortedGridView();
        }
    }

    private void showSortedGridView() {
        if (internetConectivityIsOn(getActivity())) {
            movieRepository.retrieveMovies(sortSelected()).
                    zipWith(movieRepository.retrieveFavoriteMovieIds(), (movies, favoriteIds) -> {
                        for (Movie movie : movies) {
                            movie.setFavorite(favoriteIds.contains(movie.getId()));
                        }
                        return movies;
                    }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).
                    subscribe(movies -> {
                        setUpAdapter(movies);
                        moviesGridView.setVisibility(View.VISIBLE);
                        errorMessage.setVisibility(View.GONE);
                    });
        }else{
            moviesGridView.setVisibility(View.INVISIBLE);
            errorMessage.setText(R.string.no_internet_message);
            errorMessage.setVisibility(View.VISIBLE);
        }
    }

    private void showFavoriteGridView() {
        movieRepository.retrieveFavoriteMovies().
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(movies -> {
                    if(movies != null && movies.size() > 0){
                        setUpAdapter(movies);
                        moviesGridView.setVisibility(View.VISIBLE);
                        errorMessage.setVisibility(View.GONE);
                    }else{
                        moviesGridView.setVisibility(View.INVISIBLE);
                        errorMessage.setText(R.string.no_favorite_message);
                        errorMessage.setVisibility(View.VISIBLE);
                    }
                });
    }

    private boolean isFavoriteSortSelected(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        return FAVORITE.equalsIgnoreCase(sharedPreferences.getString(getString(R.string.pref_sort_key),
                getString(R.string.default_sort_order)));
    }

    private String sortSelected(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        return sharedPreferences.getString(getString(R.string.pref_sort_key),
                getString(R.string.default_sort_order));
    }

    public void setUpAdapter(List<Movie> movies) {
        movieAdapter.clear();
        movieAdapter.addAll(movies);
        movieAdapter.notifyDataSetChanged();
        restoreLayoutManagerPosition();
    }

    private void restoreLayoutManagerPosition() {
        if (layoutManagerSavedState != null) {
            moviesGridView.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);
        }
    }

    @Override
    public void onMovieClick(int position) {
        if(internetConectivityIsOn(getActivity())){
            Movie movie = movieAdapter.getMovies().get(position);
            loadMoveDetails(movie);
            layoutManagerSavedState = null;
        }else{
            Toast toast = Toast.makeText(getContext(), R.string.no_internet_message, Toast.LENGTH_LONG);
            toast.show();
        }
    }


    private void loadMoveDetails(Movie movie) {
        movieRepository.retrieveReviewsFromMovie(movie.getId())
                .zipWith(movieRepository.retrieveTrailersFromMovie(movie.getId()),
                        (BiFunction<List<Review>, List<Trailer>, Pair<List<Trailer>, List<Review>>>) (reviews, trailers) -> new Pair(trailers, reviews))
                .subscribeOn(Schedulers.io())
                .subscribe(pair -> {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(Movie.PARCELABLE_KEY, movie);
                    intent.putParcelableArrayListExtra(TRAILER_DETAIL_EXTRA, new ArrayList<>(pair.first));
                    intent.putParcelableArrayListExtra(REVIEW_DETAIL_EXTRA, new ArrayList<>(pair.second));
                    startActivity(intent);
                });
    }
}
