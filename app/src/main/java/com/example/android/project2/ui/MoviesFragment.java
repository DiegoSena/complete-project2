package com.example.android.project2.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.project2.Utils;
import com.example.android.project2.data.adapter.MovieAdapter;
import com.example.android.project2.data.MovieRepository;
import com.example.android.project2.R;
import com.example.android.project2.model.Movie;
import com.example.android.project2.model.Review;
import com.example.android.project2.model.Trailer;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

import static com.example.android.project2.Utils.internetConectivityIsOn;

public class MoviesFragment extends Fragment {


    private GridView moviesGridView;
    private TextView errorMessage;
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
        errorMessage = rootView.findViewById(R.id.error_message);
        moviesGridView = rootView.findViewById(R.id.gridview_movies);
        moviesGridView.setAdapter(movieAdapter);

        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(internetConectivityIsOn(getActivity())){
                    Movie movie = movieAdapter.getItem(position);
                    loadMoveDetails(movie);
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
        if (isFavoriteSortSelected()) {
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
        } else {
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
    }

    private boolean isFavoriteSortSelected(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        return "favorite".equalsIgnoreCase(sharedPreferences.getString(getString(R.string.pref_sort_key),
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
    }
}
