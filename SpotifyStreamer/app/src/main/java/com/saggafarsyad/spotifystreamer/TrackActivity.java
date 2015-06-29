package com.saggafarsyad.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class TrackActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, TrackFragment.newInstance(false))
                    .commit();
        }
    }
}
