package com.saggafarsyad.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.saggafarsyad.spotifystreamer.adapter.ArtistListAdapter;
import com.saggafarsyad.spotifystreamer.model.ArtistItem;

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

    private final String BUNDLE_ARTIST_LIST = "artists";
    private final String BUNDLE_LAST_SEARCH = "last_search";

    private EditText artistSearchInput;
    private ListView artistListView;
    private ArtistListAdapter artistAdapter;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate views
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        artistSearchInput = (EditText) rootView.findViewById(R.id.input_artist);
        artistListView = (ListView) rootView.findViewById(R.id.list_artist);

        // If ther is saved isntance
        if (savedInstanceState != null) {
            // Load last state
            ArtistItem[] artistDataSet = (ArtistItem[]) savedInstanceState.getParcelableArray(BUNDLE_ARTIST_LIST);
            artistAdapter = new ArtistListAdapter(artistDataSet, getActivity());
            artistListView.setAdapter(artistAdapter);

            artistSearchInput.setText(savedInstanceState.getString(BUNDLE_LAST_SEARCH));
        }

        artistSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String searchInput = artistSearchInput.getText().toString();

                    if (!searchInput.isEmpty()) {
                        // Start Artist Search Task
                        searchArtist(searchInput);
                    }
                }

                return false;
            }
        });

        // Set on item click listener
        artistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get Artist
                ArtistItem artist = (ArtistItem) artistAdapter.getItem(position);

                // Build intent
                Intent intent = new Intent(getActivity(), TrackActivity.class);

                // Put Spotify Artist ID and Name
                intent.putExtra(TrackFragment.EXTRA_ARTIST_ID, artist.spotifyId);
                intent.putExtra(TrackFragment.EXTRA_ARTIST_NAME, artist.name);

                // Start Activity
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Get last search
        String inputSearch = artistSearchInput.getText().toString();

        if (!inputSearch.isEmpty()) {
            outState.putParcelableArray(BUNDLE_ARTIST_LIST, artistAdapter.getDataSet());
            outState.putString(BUNDLE_LAST_SEARCH, inputSearch);
        }

        super.onSaveInstanceState(outState);
    }

    private void searchArtist(String searchInput) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService service = api.getService();

        service.searchArtists(searchInput, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                if (!artistsPager.artists.items.isEmpty()) {
                    // Build adapter
                    if (artistAdapter == null) {
                        artistAdapter = new ArtistListAdapter(artistsPager.artists.items, getActivity());

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                artistListView.setAdapter(artistAdapter);
                            }
                        });
                    } else {
                        artistAdapter.updateDataSet(artistsPager.artists.items);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                artistAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } else {
                    // Show no artist found
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), R.string.artist_not_found, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.error_connection, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
