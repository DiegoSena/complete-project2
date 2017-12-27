package com.example.android.project2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, new DetailFragment())
                    .commit();
    }
}
