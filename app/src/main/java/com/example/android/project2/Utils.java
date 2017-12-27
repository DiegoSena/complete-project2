package com.example.android.project2;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.example.android.project2.model.Movie;
import com.example.android.project2.model.Review;
import com.example.android.project2.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();

    private static final String THEMOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String APPID_PARAM = "api_key";
    private static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static String getResponseFromHttpUrl(URL url){
        if(url == null) return null;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try{
            Response response = client.newCall(request).execute();

            Log.v(LOG_TAG, "called url " + url);

            if(response != null && response.body() != null){
                return response.body().string();
            }
        }catch (IOException e){
            Log.e(LOG_TAG, "Error trying to fetch trailers", e);
        }
        return null;
    }

    public static List<Trailer> fetchTrailersFromJson(String response){
        if(response == null) return new ArrayList<>();
        try {
            JSONObject moviesJson = new JSONObject(response);
            JSONArray trailerArray = moviesJson.getJSONArray("results");

            Trailer[] result = new Trailer[trailerArray.length()];

            for (int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);
                result[i] = new Trailer(trailer.getString("key"),
                        trailer.getString("id"),
                        trailer.getString("name"));
            }
            return Arrays.asList(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Review> fetchReviewsFromJson(String response){
        try {
            JSONObject moviesJson = new JSONObject(response);
            JSONArray trailerArray = moviesJson.getJSONArray("results");

            Review[] result = new Review[trailerArray.length()];

            for (int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);
                result[i] = new Review(trailer.getString("id"),
                        trailer.getString("author"),
                        trailer.getString("content"));
            }
            return Arrays.asList(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static URL buildTrailerUri(String movieId){
        String baseUrl = THEMOVIEDB_BASE_URL + movieId  + "/videos";
        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIEDB_KEY)
                .build();

        try {
            return new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static URL buildReviewsUri(String id) {
        String baseUrl = THEMOVIEDB_BASE_URL + id  + "/reviews";
        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIEDB_KEY)
                .build();

        try {
            return new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static URL buildMoviesUri(String sort) {
        String baseUrl = THEMOVIEDB_BASE_URL + sort + "/";
        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIEDB_KEY)
                .build();

        try {
            return new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Movie> fetchMoviesFromJson(String response){
        try {
            JSONObject moviesJson = new JSONObject(response);
            JSONArray movieArray = moviesJson.getJSONArray("results");

            Movie[] result = new Movie[movieArray.length()];

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                try {
                    result[i] = new Movie(movie.getString("poster_path"),
                            movie.getString("title"),
                            getReleaseYear(movie),
                            movie.getDouble("vote_average"),
                            movie.getString("overview"),
                            movie.getString("id"));
                } catch (ParseException e) {
                    Log.e(LOG_TAG, "Error parsing release_date for movie " + movie.getString("title"), e);
                }
            }
            return Arrays.asList(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int getReleaseYear(JSONObject movie) throws JSONException, ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sdf.parse(movie.getString("release_date")));
        return calendar.get(Calendar.YEAR);
    }

    public static boolean internetConectivityIsOn(Activity activity) {
        ConnectivityManager conMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if(i == null || !i.isConnected() || !i.isAvailable()){
            return false;
        }
        return true;
    }
}
