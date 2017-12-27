package com.example.android.project2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by diego.guimaraes on 09/10/16.
 */
public class MoviesFragment extends Fragment {

    private MovieAdapter movieAdapter;
    private MovieRepository movieRepository;

    public MoviesFragment(){
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieAdapter = new MovieAdapter(getContext(), R.id.gridview_movies);
        movieRepository = new MovieRepository(getContext().getContentResolver());
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



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movies_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(movieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movieAdapter.getItem(position);
                loadMoveDetails(movie);
            }

            private void loadMoveDetails(Movie movie) {
                movieRepository.retrieveReviewsFromMovie(movie.getId())
                        .zipWith(movieRepository.retrieveTrailersFromMovie(movie.getId()),
                                (BiFunction<List<Review>, List<Trailer>, Pair<List<Trailer>, List<Review>>>) (reviews, trailers) -> new Pair(trailers, reviews))
                        .subscribeOn(Schedulers.io())
                        .subscribe(pair -> {
                            Intent intent = new Intent(getActivity(), DetailActivity.class);
                            intent.putExtra(Movie.PARCELABLE_KEY, movie);
                            intent.putParcelableArrayListExtra("INTENT_TRAILER_DETAIL", new ArrayList<>(pair.first));
                            intent.putParcelableArrayListExtra("INTENT_REVIEW_DETAIL", new ArrayList<>(pair.second));
                            startActivity(intent);
                        });
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        if ("favorite".equalsIgnoreCase(sharedPreferences.getString(getString(R.string.pref_sort_key),
                getString(R.string.default_sort_order)))) {
            movieRepository.retrieveFavoriteMovies().
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(movies -> setUpAdapter(movies));
        } else {
            if (internetConectivityIsOn()) {
                movieRepository.retrieveMovies(sharedPreferences.getString(getString(R.string.pref_sort_key),
                        getString(R.string.default_sort_order))).
                        zipWith(movieRepository.retrieveFavoriteMovieIds(), (movies, favoriteIds) -> {
                            for (Movie movie : movies) {
                                movie.setFavorite(favoriteIds.contains(movie.getId()));
                            }
                            return movies;
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).
                        subscribe(movies -> setUpAdapter(movies));
            }
        }
    }

    private boolean internetConectivityIsOn() {
        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }

    public void setUpAdapter(List<Movie> movies) {
        movieAdapter.clear();
        movieAdapter.addAll(movies);
        movieAdapter.notifyDataSetChanged();
    }
}
