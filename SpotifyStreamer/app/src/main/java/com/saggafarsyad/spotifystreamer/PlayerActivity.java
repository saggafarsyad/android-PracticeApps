package com.saggafarsyad.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.saggafarsyad.spotifystreamer.adapter.TrackListAdapter;
import com.saggafarsyad.spotifystreamer.model.TrackItem;


public class PlayerActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private ListView mPlaylistView;
    private PlayerFragment mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Inflate views
        mPlaylistView = (ListView) findViewById(R.id.playlist);
        mPlayer = new PlayerFragment();

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.player_fragment, mPlayer)
                .commit();

        if (mPlaylistView != null)
            mPlaylistView.setOnItemClickListener(this);
    }

    public void setPlaylistAdapter(TrackItem[] dataset) {
        if (mPlaylistView != null)
            mPlaylistView.setAdapter(new TrackListAdapter(dataset, this));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPlayer.setPlaylistPosition(position);
    }
}
