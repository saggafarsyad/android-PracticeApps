package com.saggafarsyad.spotifystreamer;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.saggafarsyad.spotifystreamer.adapter.ArtistListAdapter;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private static final String LOG_TAG = "MainFragment";

    private Handler mTaskHandler;
    private EditText inputArtistEditText;
    private ListView artistListView;
    private Runnable mSearchArtistTask = new Runnable() {
        @Override
        public void run() {
            // Get input string
            String searchInput = inputArtistEditText.getText().toString();

            if (!searchInput.isEmpty()) {
                // Start Artist Search Task
                searchArtist(searchInput);
            }
        }
    };

    public MainFragment() {
        mTaskHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate views
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        inputArtistEditText = (EditText) rootView.findViewById(R.id.input_artist);
        artistListView = (ListView) rootView.findViewById(R.id.list_artist);

        // Set text change listener
        inputArtistEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do Nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Cancel task
                mTaskHandler.removeCallbacks(mSearchArtistTask);

                // Delay 1.5 seconds and start searching artist
                mTaskHandler.postDelayed(mSearchArtistTask, 1500);
            }
        });

        return rootView;
    }

    private void searchArtist(String searchInput) {
        final Handler mainHandler = new Handler(getActivity().getMainLooper());

        SpotifyApi api = new SpotifyApi();
        SpotifyService service = api.getService();

        service.searchArtists(searchInput, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                if (!artistsPager.artists.items.isEmpty()) {
                    // Build adaptar
                    final ArtistListAdapter adapter = new ArtistListAdapter(artistsPager.artists.items, getActivity());

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Set adapter
                            artistListView.setAdapter(adapter);
                        }
                    });
                } else {
                    // Show no artist found
                    Toast.makeText(getActivity(), R.string.artist_not_found, Toast.LENGTH_SHORT).show();
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
