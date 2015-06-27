package com.saggafarsyad.spotifystreamer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerFragment extends DialogFragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    // Playback Handler
    final Handler playbackHandler = new Handler();
    private final String LOG_TAG = "PlayerFragment";
    // Track Playlist
    private String mPlaylist[];
    private int mPlaylistPosition;
    private Track mCurrentTrack;
    // Media Player
    private MediaPlayer mMediaPlayer;
    // Views
    private TextView artistNameTextView;
    private TextView albumNameTextView;
    private ImageView albumArtworkImageView;
    private TextView trackNameTextView;
    private TextView durationTextView;
    private SeekBar playbackSeekBar;
    final Runnable playbackRunnable = new Runnable() {
        @Override
        public void run() {
            // Get position in second
            if (mMediaPlayer != null) {
                int mCurrentPosition = mMediaPlayer.getCurrentPosition();
                // Set progress
                playbackSeekBar.setProgress(mCurrentPosition);
                // Repeat after 100ms
                playbackHandler.postDelayed(this, 1000);
            }
        }
    };
    private ImageButton playPauseButton;

    public PlayerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate Layout
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        artistNameTextView = (TextView) rootView.findViewById(R.id.artist_name);
        albumNameTextView = (TextView) rootView.findViewById(R.id.album_name);
        albumArtworkImageView = (ImageView) rootView.findViewById(R.id.album_artwork);
        trackNameTextView = (TextView) rootView.findViewById(R.id.track_name);
        durationTextView = (TextView) rootView.findViewById(R.id.track_end);
        playbackSeekBar = (SeekBar) rootView.findViewById(R.id.playback_seeker);
        playPauseButton = (ImageButton) rootView.findViewById(R.id.play_pause);
        ImageButton prevButton = (ImageButton) rootView.findViewById(R.id.prev);
        ImageButton nextButton = (ImageButton) rootView.findViewById(R.id.next);
        // Set seek bar on change listener
        playbackSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // If user changed the seekbar, set seekTo
                if (mMediaPlayer != null && fromUser) {
                    mMediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });
        // Set Play/Pause Button on click listener
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer.isPlaying()) {
                    pauseTrack();
                } else {
                    playTrack();
                }
            }
        });
        // Set previous track button on click listener
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlaylistPosition == 0) {
                    // This is the first track,
                    // Select the last track
                    mPlaylistPosition = mPlaylist.length - 1;
                } else {
                    // Select previous track
                    mPlaylistPosition--;
                }

                fetchTrack(mPlaylist[mPlaylistPosition]);
            }
        });
        // Set next track button on click listener
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlaylistPosition == mPlaylist.length - 1) {
                    // This is the last track
                    // Select the first track
                    mPlaylistPosition = 0;
                } else {
                    // Select next track
                    mPlaylistPosition++;
                }
                // Start fetching track
                fetchTrack(mPlaylist[mPlaylistPosition]);
            }
        });

        // Get Intent
        Intent intent = getActivity().getIntent();
        // Get Track List from Intent
        mPlaylist = intent.getStringArrayExtra(getString(R.string.intent_extra_track_id_list));
        // Get Position
        mPlaylistPosition = intent.getIntExtra(getString(R.string.intent_extra_current_track_position), 0);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Init Media player
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);

        // Fetch track
        fetchTrack(mPlaylist[mPlaylistPosition]);
    }

    @Override
    public void onPause() {
        // Release Media Player
        mMediaPlayer.release();
        mMediaPlayer = null;

        super.onPause();
    }

    private void playTrack() {
        // Get playback position
        int progress = playbackSeekBar.getProgress();

        if (progress > 0 && progress <= playbackSeekBar.getMax()) {
            mMediaPlayer.seekTo(progress);
        }

        getActivity().runOnUiThread(playbackRunnable);
        mMediaPlayer.start();
        playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_av_pause, null));
    }

    private void pauseTrack() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();

            playbackHandler.removeCallbacks(playbackRunnable);
            playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_av_play_arrow, null));
        }
    }

    private void fetchTrack(final String trackId) {
        // Init Spotify wrapper
        SpotifyApi api = new SpotifyApi();
        SpotifyService service = api.getService();

        // Get UI Thread
        final Handler mainHandler = new Handler(getActivity().getMainLooper());

        // Build Parameters
        HashMap<String, Object> param = new HashMap<>();
        param.put("country", "US");

        // Fetch track by ID
        service.getTrack(trackId, param, new Callback<Track>() {
            @Override
            public void success(final Track track, Response response) {
                mCurrentTrack = track;

                mMediaPlayer.reset();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                try {
                    mMediaPlayer.setDataSource(mCurrentTrack.preview_url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMediaPlayer.prepareAsync();

                // Update UIs
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        playbackSeekBar.setProgress(0);
                        artistNameTextView.setText(track.artists.get(0).name);
                        albumNameTextView.setText(track.album.name);
                        trackNameTextView.setText(track.name);
                        durationTextView.setText(getDurationString(0));
                        // @todo Set duration
                        // Load artwork
                        Picasso.with(getActivity()).load(track.album.images.get(0).url).into(albumArtworkImageView);
                        playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_av_play_arrow, null));
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                // Make Toast
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.error_connection, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // Setup seekbar
        playbackSeekBar.setMax(mMediaPlayer.getDuration());
        // Set end track duration
        durationTextView.setText(getDurationString(mp.getDuration()));
        // Play
        playTrack();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // Stop seek bar handler
        playbackHandler.removeCallbacks(playbackRunnable);
        // Change to Play button
        playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_av_play_arrow, null));
        // Reset position
        playbackSeekBar.setProgress(0);
        mMediaPlayer.seekTo(0);
    }

    private String getDurationString(long millis) {
        long second = millis / 1000;
        int minutes = 0;

        while (second > 59) {
            minutes++;
            second -= 60;
        }

        if (second < 10) {
            return minutes + ":0" + second;
        }

        return minutes + ":" + second;
    }
}

