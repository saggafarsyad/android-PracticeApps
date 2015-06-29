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
    private static final String ARGS_TABLET_MODE = "tablet_mode";

    // Views
    private ListView trackListView;
    private TrackListAdapter mTrackAdapter;

    // Player Fragment
//    private PlayerFragment player;
    // MainFragment
    private String artistName;

    private boolean isTabletMode;

    public TrackFragment() {
    }

    public static TrackFragment newInstance(boolean isTablet) {
        // Add Args
        Bundle args = new Bundle();
        args.putBoolean(ARGS_TABLET_MODE, isTablet);

        TrackFragment fragment = new TrackFragment();
        fragment.setArguments(args);

        return fragment;
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

            artistName = savedInstanceState.getString(getString(R.string.args_artist_name));
        } else {
            // Get Arguments
            isTabletMode = getArguments().getBoolean(ARGS_TABLET_MODE);

            if (!isTabletMode) {
                Intent intent = getActivity().getIntent();
                // Get artist Id
                artistName = intent.getStringExtra(getString(R.string.intent_extra_artist_name));
                // Get Ids
                String artistID = intent.getStringExtra(getString(R.string.intent_extra_spotify_id));
                // Set subtitle
                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(artistName);
                // Fetch Tracks
                fetchTopTracks(artistID);
            }
        }

        // @todo: Set On Item Click Listener
        trackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

    public void setArtistTopTracks(String spotifyId) {
        fetchTopTracks(spotifyId);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Get items
        outState.putParcelableArray(getString(R.string.args_track_list), mTrackAdapter.getDataSet());
        outState.putString(getString(R.string.args_artist_name), artistName);
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
            public void success(final Tracks tracks, Response response) {
                if (!tracks.tracks.isEmpty()) {
                    // Set adapter to track list view
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mTrackAdapter == null) {
                                // Build adapter
                                mTrackAdapter = new TrackListAdapter(tracks.tracks, getActivity());
                                trackListView.setAdapter(mTrackAdapter);
                            } else {
                                mTrackAdapter.updateDataSet(tracks.tracks);
                                mTrackAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                } else {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), R.string.track_not_found, Toast.LENGTH_SHORT).show();

                            if (mTrackAdapter != null) {
                                mTrackAdapter.updateDataSet(new TrackItem[0]);
                                mTrackAdapter.notifyDataSetChanged();
                            }
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
