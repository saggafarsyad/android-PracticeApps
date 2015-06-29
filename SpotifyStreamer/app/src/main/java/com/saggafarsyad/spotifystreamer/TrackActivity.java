package com.saggafarsyad.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class TrackActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (findViewById(R.id.player_fragment) != null) {
            transaction.replace(R.id.track_fragment, TrackFragment.newInstance(true), getString(R.string.tag_track_fragment));
            transaction.replace(R.id.player_fragment, PlayerFragment.newInstance(true), getString(R.string.tag_player_fragment));
        } else {
            transaction.replace(R.id.track_fragment, TrackFragment.newInstance(false));
        }

        transaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
