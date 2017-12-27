package com.example.android.project2.data;

import android.content.ContentResolver;
import android.database.Cursor;

import com.example.android.project2.Utils;
import com.example.android.project2.data.db.MovieContract;
import com.example.android.project2.model.Movie;
import com.example.android.project2.model.Review;
import com.example.android.project2.model.Trailer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

import static com.example.android.project2.Utils.fetchMoviesFromJson;
import static com.example.android.project2.Utils.fetchReviewsFromJson;
import static com.example.android.project2.Utils.fetchTrailersFromJson;
import static com.example.android.project2.Utils.getResponseFromHttpUrl;

/**
 * Created by diego on 26/12/17.
 */

public class MovieRepository {

    private static final String LOG_TAG = MovieRepository.class.getSimpleName();
    private final ContentResolver contentResolver;

    public MovieRepository(ContentResolver contentResolver){
        this.contentResolver = contentResolver;
    }

    public Single<List<Trailer>> retrieveTrailersFromMovie(String id) {
        return Single.create(new SingleOnSubscribe<List<Trailer>>() {
            final URL url = Utils.buildTrailerUri(id);

            @Override
            public void subscribe(SingleEmitter<List<Trailer>> e) throws Exception {
                try {
                    final List<Trailer> trailers = fetchTrailersFromJson(getResponseFromHttpUrl(url));
                    e.onSuccess(trailers);
                } catch (Exception ex) {
                    e.onError(ex);
                }
            }
        });
    }

    public Single<List<Review>> retrieveReviewsFromMovie(String id) {
        return Single.create(new SingleOnSubscribe<List<Review>>() {
            final URL url = Utils.buildReviewsUri(id);

            @Override
            public void subscribe(SingleEmitter<List<Review>> e) throws Exception {
                try {
                    final List<Review> reviews = fetchReviewsFromJson(getResponseFromHttpUrl(url));
                    e.onSuccess(reviews);
                } catch (Exception ex) {
                    e.onError(ex);
                }
            }
        });
    }

    public Single<List<Movie>> retrieveMovies(String sort){
        return Single.create(new SingleOnSubscribe<List<Movie>>() {
            final URL url = Utils.buildMoviesUri(sort);
            @Override
            public void subscribe(SingleEmitter<List<Movie>> e) throws Exception {
                try{
                    final List<Movie> movies = fetchMoviesFromJson(getResponseFromHttpUrl(url));
                    e.onSuccess(movies);
                }catch (Exception ex){
                    e.onError(ex);
                }
            }
        });
    }


    public Single<List<String>> retrieveFavoriteMovieIds() {
        return Single.create(new SingleOnSubscribe<List<String>>() {
            List<String> ids = new ArrayList<>();
            @Override
            public void subscribe(SingleEmitter<List<String>> e) throws Exception {
                final Cursor query = contentResolver.query(MovieContract.MovieEntry.CONTENT_URI, new String[]{MovieContract.MovieEntry.COLUMN_ID}, null, null, null);
                if (query.moveToFirst()) {
                    do {
                        ids.add(query.getString(query.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID)));
                    } while (query.moveToNext());
                }
                e.onSuccess(ids);
            }
        });
    }

    public Single<List<Movie>> retrieveFavoriteMovies() {
        return Single.create(new SingleOnSubscribe<List<Movie>>() {
            List<Movie> movies = new ArrayList<>();

            @Override
            public void subscribe(SingleEmitter<List<Movie>> e) throws Exception {
                try{
                    final Cursor cursor = contentResolver.query(MovieContract.MovieEntry.CONTENT_URI, new String[]{}, null, null, null);
                    if (cursor.moveToFirst()) {
                        do {
                            Movie movie = new Movie(
                                    cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)),
                                    cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)),
                                    cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_YEAR)),
                                    cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)),
                                    cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)),
                                    cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID)));
                            movie.setFavorite(true);
                            movies.add(movie);
                        } while (cursor.moveToNext());
                    }
                    e.onSuccess(movies);
                }catch (Exception ex){
                    e.onError(ex);
                }
            }
        });
    }
}
