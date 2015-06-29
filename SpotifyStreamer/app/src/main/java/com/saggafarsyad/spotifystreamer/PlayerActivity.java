package com.saggafarsyad.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPlayer.setPlaylistPosition(position);
    }
}
