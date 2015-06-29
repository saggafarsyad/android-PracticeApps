package com.saggafarsyad.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.saggafarsyad.spotifystreamer.adapter.TrackListAdapter;
import com.saggafarsyad.spotifystreamer.model.TrackItem;

import java.util.HashMap;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class TrackFragment extends Fragment {
    // Extra const
//    public static final String EXTRA_ARTIST_ID = "artist_id";
//    public static final String EXTRA_ARTIST_NAME = "artist_name";

    // Views
    private ListView trackListView;
    private TrackListAdapter mTrackAdapter;

    public TrackFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Activity Sub Title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(
                getActivity().getIntent().getStringExtra(getString(R.string.intent_extra_artist_name))
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate Views
        View rootView = inflater.inflate(R.layout.fragment_track, container, false);
        trackListView = (ListView) rootView.findViewById(R.id.list_track);

        // Get Top 10 Tracks
        if (savedInstanceState != null) {
            TrackItem[] dataSet = (TrackItem[]) savedInstanceState
                    .getParcelableArray(getString(R.string.args_track_list));
            mTrackAdapter = new TrackListAdapter(dataSet, getActivity());
            trackListView.setAdapter(mTrackAdapter);
        } else {
            // Get artist Id
            if (getActivity().getIntent() != null) {
                String artistID = getActivity().getIntent().getStringExtra(getString(R.string.intent_extra_spotify_id));
                fetchTopTracks(artistID);
            }
        }

        // @todo: Set On Item Click Listener
        trackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Check if adapter exist
                    // Build Intent
                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    // Put current track position
                    intent.putExtra(getString(R.string.intent_extra_current_track_position), position);
                // Put playlist
                intent.putExtra(getString(R.string.intent_extra_playlist), mTrackAdapter.getDataSet());
                    // Start Activity
                    startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Get items
        outState.putParcelableArray(getString(R.string.args_track_list), mTrackAdapter.getDataSet());
    }

    private void fetchTopTracks(String artistID) {
        // Init Spotify wrapper
        SpotifyApi api = new SpotifyApi();
        SpotifyService service = api.getService();

        // Get UI Thread
        final Handler mainHandler = new Handler(getActivity().getMainLooper());

        // @todo Build Settings to change country
        // Build Parameters
        HashMap<String, Object> param = new HashMap<>();
        param.put("country", "US");

        // Fetch Top 10 tracks
        service.getArtistTopTrack(artistID, param, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                if (!tracks.tracks.isEmpty()) {
                    // Build adapter
                    mTrackAdapter = new TrackListAdapter(tracks.tracks, getActivity());

                    // Set adapter to track list view
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            trackListView.setAdapter(mTrackAdapter);
                        }
                    });
                } else {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), R.string.track_not_found, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.error_connection, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
