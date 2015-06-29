package com.saggafarsyad.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isTwoPane;

        // Check for two pane
        isTwoPane = findViewById(R.id.player_fragment) != null;

        if (savedInstanceState == null) {
            // Begin transaction
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.left_fragment, MainFragment.newInstance(isTwoPane), getString(R.string.tag_main_fragment));
            // Initialize new fragments
            if (isTwoPane) {
                transaction.replace(R.id.player_fragment, TrackFragment.newInstance(isTwoPane), getString(R.string.tag_track_fragment));
            }

            transaction.commit();
        }
    }
}
