package com.saggafarsyad.spotifystreamer;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.saggafarsyad.spotifystreamer.adapter.TrackListAdapter;

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
    public static final String EXTRA_ARTIST_ID = "artist_id";
    public static final String EXTRA_ARTIST_NAME = "artist_name";

    // Views
    private ListView trackListView;

    public TrackFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Activity Sub Title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(
                getActivity().getIntent().getStringExtra(EXTRA_ARTIST_NAME)
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate Views
        View rootView = inflater.inflate(R.layout.fragment_track, container, false);
        trackListView = (ListView) rootView.findViewById(R.id.list_track);

        // Get Top 10 Tracks
        fetchTopTracks();

        // @todo: Set On Item Click Listener

        return rootView;
    }

    private void fetchTopTracks() {
        // Get artist Id
        String artistID = getActivity().getIntent().getStringExtra(EXTRA_ARTIST_ID);

        // Init Spotify wrapper
        SpotifyApi api = new SpotifyApi();
        SpotifyService service = api.getService();

        // Get UI Thread
        final Handler mainHandler = new Handler(getActivity().getMainLooper());

        // Build Parameters
        HashMap<String, Object> param = new HashMap<>();
        param.put("country", "US");

        // Fetch Top 10 tracks
        service.getArtistTopTrack(artistID, param, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                if (!tracks.tracks.isEmpty()) {
                    // Build adapter
                    final TrackListAdapter adapter = new TrackListAdapter(tracks.tracks, getActivity());

                    // Set adapter to track list view
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            trackListView.setAdapter(adapter);
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
